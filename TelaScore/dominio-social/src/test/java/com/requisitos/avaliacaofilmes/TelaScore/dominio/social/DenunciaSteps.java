package com.requisitos.avaliacaofilmes.TelaScore.dominio.social;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.Denuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.MotivoDenuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.StatusDenuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.TipoAlvoDenuncia;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

public class DenunciaSteps {
	private final List<Denuncia> bancoDenuncias = new ArrayList<>();
	private final DenunciaRepositorio repositorio = new DenunciaRepositorio() {
		@Override
		public void salvar(Denuncia denuncia) {
			bancoDenuncias.removeIf(d -> d.getId().equals(denuncia.getId()));
			bancoDenuncias.add(denuncia);
		}

		@Override
		public Denuncia obter(DenunciaId id) {
			return bancoDenuncias.stream().filter(d -> d.getId().equals(id)).findFirst().orElse(null);
		}

		@Override
		public List<Denuncia> listarPorStatus(StatusDenuncia status) {
			return bancoDenuncias.stream().filter(d -> d.getStatus() == status).toList();
		}

		@Override
		public List<Denuncia> listarPorUsuario(UsuarioId denuncianteId) {
			return bancoDenuncias.stream()
				.filter(d -> d.getDenuncianteId().equals(denuncianteId))
				.toList();
		}

		@Override
		public boolean existeDenunciaDoUsuarioParaAlvo(UsuarioId denuncianteId, TipoAlvoDenuncia tipoAlvo, int alvoId) {
			return bancoDenuncias.stream().anyMatch(d ->
				d.getDenuncianteId().equals(denuncianteId)
					&& d.getTipoAlvo() == tipoAlvo
					&& d.getAlvoId() == alvoId);
		}
	};

	private DenunciaServico servico;
	private UsuarioId denuncianteId;
	private int alvoId;
	private Denuncia denuncia;
	private Exception excecaoCapturada;

	@Dado("que o usuário {int} deseja denunciar a mensagem {int}")
	public void que_o_usuario_deseja_denunciar_a_mensagem(Integer usuarioId, Integer mensagemId) {
		servico = new DenunciaServico(repositorio);
		denuncianteId = new UsuarioId(usuarioId);
		alvoId = mensagemId;
		excecaoCapturada = null;
	}

	@Quando("ele registra uma denúncia pelo motivo {string} com a descrição {string}")
	public void ele_registra_uma_denuncia_pelo_motivo_com_a_descricao(String motivo, String descricao) {
		try {
			denuncia = new Denuncia(
				new DenunciaId(1),
				denuncianteId,
				alvoId,
				TipoAlvoDenuncia.MENSAGEM,
				MotivoDenuncia.valueOf(motivo),
				descricao
			);
			servico.registrar(denuncia);
		} catch (Exception e) {
			excecaoCapturada = e;
		}
	}

	@Quando("ele tenta registrar uma denúncia sem descrição")
	public void ele_tenta_registrar_uma_denuncia_sem_descricao() {
		ele_registra_uma_denuncia_pelo_motivo_com_a_descricao("OFENSIVO", "");
	}

	@Entao("a denúncia deve ser criada com status {string}")
	public void a_denuncia_deve_ser_criada_com_status(String status) {
		assertNotNull(denuncia);
		assertEquals(StatusDenuncia.valueOf(status), denuncia.getStatus());
		assertTrue(bancoDenuncias.contains(denuncia));
	}

	@Entao("o sistema deve rejeitar a denúncia informando que a descrição não pode estar em branco")
	public void o_sistema_deve_rejeitar_a_denuncia_informando_que_a_descricao_nao_pode_estar_em_branco() {
		assertNotNull(excecaoCapturada);
		assertTrue(excecaoCapturada.getMessage().contains("descrição"));
		assertTrue(excecaoCapturada.getMessage().contains("branco"));
	}

	@Dado("que o usuário {int} já denunciou a mensagem {int}")
	public void que_o_usuario_ja_denunciou_a_mensagem(Integer usuarioId, Integer mensagemId) {
		que_o_usuario_deseja_denunciar_a_mensagem(usuarioId, mensagemId);
		Denuncia existente = new Denuncia(
			new DenunciaId(1),
			denuncianteId,
			alvoId,
			TipoAlvoDenuncia.MENSAGEM,
			MotivoDenuncia.OFENSIVO,
			"Mensagem ofensiva"
		);
		repositorio.salvar(existente);
	}

	@Entao("o sistema deve rejeitar a denúncia informando que o usuário já denunciou este alvo")
	public void o_sistema_deve_rejeitar_a_denuncia_informando_que_o_usuario_ja_denunciou_este_alvo() {
		assertNotNull(excecaoCapturada);
		assertEquals("O usuário já denunciou este alvo", excecaoCapturada.getMessage());
	}

	@Dado("que existe uma denúncia pendente")
	public void que_existe_uma_denuncia_pendente() {
		servico = new DenunciaServico(repositorio);
		denuncia = new Denuncia(
			new DenunciaId(1),
			new UsuarioId(1),
			10,
			TipoAlvoDenuncia.MENSAGEM,
			MotivoDenuncia.OFENSIVO,
			"Mensagem ofensiva"
		);
		repositorio.salvar(denuncia);
	}

	@Quando("a denúncia é aceita pela moderação")
	public void a_denuncia_e_aceita_pela_moderacao() {
		servico.aceitar(denuncia);
	}

	@Quando("a denúncia é rejeitada pela moderação")
	public void a_denuncia_e_rejeitada_pela_moderacao() {
		servico.rejeitar(denuncia);
	}

	@Entao("a denúncia deve ficar com status {string}")
	public void a_denuncia_deve_ficar_com_status(String status) {
		assertEquals(StatusDenuncia.valueOf(status), denuncia.getStatus());
	}
}
