package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.EntradaInvalidaException;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.AvaliarDenunciaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.AvaliarDenunciaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.DenunciaResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.ListarDenunciasPendentesCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.ListarDenunciasPorStatusCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.ListarDenunciasPorUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.ListarTodasDenunciasCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.RegistrarDenunciaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia.RegistrarDenunciaComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/denuncias")
public class DenunciaController {

	private final RegistrarDenunciaCasoDeUso registrarDenuncia;
	private final ListarDenunciasPorUsuarioCasoDeUso listarPorUsuario;
	private final ListarDenunciasPendentesCasoDeUso listarPendentes;
	private final ListarTodasDenunciasCasoDeUso listarTodas;
	private final ListarDenunciasPorStatusCasoDeUso listarPorStatus;
	private final AvaliarDenunciaCasoDeUso avaliarDenuncia;
	private final SessaoUsuario sessaoUsuario;

	public DenunciaController(
			RegistrarDenunciaCasoDeUso registrarDenuncia,
			ListarDenunciasPorUsuarioCasoDeUso listarPorUsuario,
			ListarDenunciasPendentesCasoDeUso listarPendentes,
			ListarTodasDenunciasCasoDeUso listarTodas,
			ListarDenunciasPorStatusCasoDeUso listarPorStatus,
			AvaliarDenunciaCasoDeUso avaliarDenuncia,
			SessaoUsuario sessaoUsuario) {
		this.registrarDenuncia = registrarDenuncia;
		this.listarPorUsuario = listarPorUsuario;
		this.listarPendentes = listarPendentes;
		this.listarTodas = listarTodas;
		this.listarPorStatus = listarPorStatus;
		this.avaliarDenuncia = avaliarDenuncia;
		this.sessaoUsuario = sessaoUsuario;
	}

	@PostMapping
	public ResponseEntity<DenunciaResumo> registrar(@RequestBody RegistrarDenunciaRequest body) {
		if (body == null || body.alvoId() == null) {
			throw new EntradaInvalidaException("Dados da denuncia invalidos");
		}

		int denuncianteId = obterDenuncianteId(body.denuncianteId());
		DenunciaResumo resumo = registrarDenuncia.executar(new RegistrarDenunciaComando(
				denuncianteId,
				body.alvoId(),
				normalizarObrigatorio(body.tipoAlvo(), "tipoAlvo"),
				normalizarObrigatorio(body.motivo(), "motivo"),
				body.descricao(),
				body.linkOcorrencia()));

		return ResponseEntity.status(HttpStatus.CREATED).body(resumo);
	}

	@GetMapping("/minhas")
	public List<DenunciaResumo> listarMinhas() {
		UsuarioLogado usuarioLogado = exigirUsuarioLogado();
		return listarPorUsuario.executar(usuarioLogado.getId().getId());
	}

	@GetMapping("/pendentes")
	public List<DenunciaResumo> listarPendentes() {
		exigirAdmin();
		return listarPendentes.executar();
	}

	@GetMapping
	public List<DenunciaResumo> listar(
			@RequestParam(required = false) Integer denuncianteId,
			@RequestParam(required = false) String status) {
		if (denuncianteId != null) {
			return listarPorUsuario.executar(denuncianteId);
		}

		if (status != null) {
			exigirAdmin();
			return listarPorStatus.executar(status);
		}

		exigirAdmin();
		return listarTodas.executar();
	}

	@PatchMapping("/{denunciaId}/avaliar")
	public DenunciaResumo avaliar(@PathVariable int denunciaId, @RequestBody AvaliarDenunciaRequest body) {
		exigirAdmin();
		if (body == null) {
			throw new EntradaInvalidaException("Dados de avaliacao invalidos");
		}

		return avaliarDenuncia.executar(new AvaliarDenunciaComando(
				denunciaId,
				normalizarObrigatorio(body.decisao(), "decisao")));
	}

	public static record RegistrarDenunciaRequest(
			Integer denuncianteId,
			Integer alvoId,
			String tipoAlvo,
			String motivo,
			String descricao,
			String linkOcorrencia) {
	}

	public static record AvaliarDenunciaRequest(String decisao) {
	}

	private int obterDenuncianteId(Integer denuncianteIdInformado) {
		UsuarioLogado usuarioLogado = sessaoUsuario.obterUsuarioLogado();
		if (usuarioLogado != null) {
			return usuarioLogado.getId().getId();
		}

		if (denuncianteIdInformado == null) {
			throw new EntradaInvalidaException("Informe o denuncianteId ou envie um token valido");
		}
		return denuncianteIdInformado;
	}

	private UsuarioLogado exigirUsuarioLogado() {
		UsuarioLogado usuarioLogado = sessaoUsuario.obterUsuarioLogado();
		if (usuarioLogado == null) {
			throw new IllegalStateException("Usuario nao esta logado");
		}
		return usuarioLogado;
	}

	private void exigirAdmin() {
		UsuarioLogado usuarioLogado = exigirUsuarioLogado();
		if (!usuarioLogado.isAdmin()) {
			throw new IllegalStateException("Apenas administradores podem moderar denuncias");
		}
	}

	private String normalizarObrigatorio(String valor, String campo) {
		if (valor == null || valor.isBlank()) {
			throw new EntradaInvalidaException("Informe o campo " + campo);
		}
		return valor.trim().toUpperCase();
	}
}
