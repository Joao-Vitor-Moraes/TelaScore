package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.*;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.EditarSolicitacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.EditarSolicitacaoComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.NotificacaoMetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final EditarSolicitacaoCasoDeUso editarSolicitacao;
    private final SolicitarFilmeCasoDeUso solicitarFilme;
    private final ObterSolicitacaoCasoDeUso obterSolicitacao;
    private final CancelarSolicitacaoFilmeCasoDeUso cancelarSolicitacao;
    private final ListarSolicitacoesPorSolicitanteCasoDeUso listarPorSolicitante;
    private final ListarSolicitacoesPorStatusCasoDeUso listarPorStatus;
    private final AvaliarSolicitacaoFilmeCasoDeUso avaliarSolicitacao;
    private final SolicitarAjustesFilmeCasoDeUso solicitarAjustes;
    private final NotificacaoMetaRepositorio notificacoes;
    private final UsuarioRepositorio usuarios;

    public SolicitacaoController(EditarSolicitacaoCasoDeUso editarSolicitacao,
            SolicitarFilmeCasoDeUso solicitarFilme,
            ObterSolicitacaoCasoDeUso obterSolicitacao,
            CancelarSolicitacaoFilmeCasoDeUso cancelarSolicitacao,
            ListarSolicitacoesPorSolicitanteCasoDeUso listarPorSolicitante,
            ListarSolicitacoesPorStatusCasoDeUso listarPorStatus,
            AvaliarSolicitacaoFilmeCasoDeUso avaliarSolicitacao,
            SolicitarAjustesFilmeCasoDeUso solicitarAjustes,
            NotificacaoMetaRepositorio notificacoes,
            UsuarioRepositorio usuarios) {
        this.editarSolicitacao = editarSolicitacao;
        this.solicitarFilme = solicitarFilme;
        this.obterSolicitacao = obterSolicitacao;
        this.cancelarSolicitacao = cancelarSolicitacao;
        this.listarPorSolicitante = listarPorSolicitante;
        this.listarPorStatus = listarPorStatus;
        this.avaliarSolicitacao = avaliarSolicitacao;
        this.solicitarAjustes = solicitarAjustes;
        this.notificacoes = notificacoes;
        this.usuarios = usuarios;
    }

    @PutMapping("/{solicitacaoId}")
    public ResponseEntity<Void> editar(@PathVariable int solicitacaoId, @RequestBody EditarSolicitacaoRequest body) {
        editarSolicitacao.executar(new EditarSolicitacaoComando(
                solicitacaoId, body.solicitanteId(), body.tituloSugerido(),
                body.justificativa(), body.pais(), body.ano(), body.fotoUrl()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{solicitacaoId}")
    public SolicitacaoResumo obter(@PathVariable int solicitacaoId) {
        return obterSolicitacao.executar(solicitacaoId);
    }

    @PostMapping
    public ResponseEntity<SolicitacaoResumo> solicitar(@RequestBody SolicitarFilmeComando comando) {
        SolicitacaoResumo resumo = solicitarFilme.executar(comando);
        usuarios.listarTodos().stream()
                .filter(usuario -> usuario.getPapel() == PapelUsuario.ADMIN)
                .forEach(admin -> notificacoes.criarSistema(
                        admin.getId(),
                        "SOLICITACAO",
                        "Nova solicitacao de filme",
                        resumo.tituloSugerido() + " aguarda avaliacao.",
                        "/admin/solicitacoes"));
        return ResponseEntity.status(HttpStatus.CREATED).body(resumo);
    }

    @DeleteMapping("/{solicitacaoId}")
    public ResponseEntity<Void> cancelar(@PathVariable int solicitacaoId,
            @RequestParam int usuarioId) {
        cancelarSolicitacao.executar(new CancelarSolicitacaoFilmeComando(solicitacaoId, usuarioId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{solicitacaoId}/avaliar")
    public ResponseEntity<Void> avaliar(@PathVariable int solicitacaoId,
            @RequestBody AvaliarSolicitacaoFilmeComando corpo) {
        avaliarSolicitacao.executar(new AvaliarSolicitacaoFilmeComando(solicitacaoId, corpo.avaliadorId(), corpo.aprovar()));
        SolicitacaoResumo solicitacao = obterSolicitacao.executar(solicitacaoId);
        notificacoes.criarSistema(
                new UsuarioId(solicitacao.solicitanteId()),
                "SOLICITACAO",
                corpo.aprovar() ? "Solicitacao aprovada" : "Solicitacao recusada",
                corpo.aprovar()
                        ? solicitacao.tituloSugerido() + " foi adicionada ao catalogo."
                        : solicitacao.tituloSugerido() + " nao foi aprovada desta vez.",
                "/solicitacoes");
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{solicitacaoId}/ajustes")
    public ResponseEntity<Void> solicitarAjustes(@PathVariable int solicitacaoId,
            @RequestBody SolicitarAjustesFilmeComando corpo) {
        solicitarAjustes.executar(new SolicitarAjustesFilmeComando(solicitacaoId, corpo.avaliadorId(), corpo.feedback()));
        SolicitacaoResumo solicitacao = obterSolicitacao.executar(solicitacaoId);
        notificacoes.criarSistema(
                new UsuarioId(solicitacao.solicitanteId()),
                "SOLICITACAO",
                "Ajustes solicitados",
                corpo.feedback() == null || corpo.feedback().isBlank()
                        ? "Revise sua solicitacao de filme."
                        : corpo.feedback(),
                "/solicitacoes");
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<SolicitacaoResumo> listar(@RequestParam(required = false) Integer solicitanteId,
            @RequestParam(required = false) String status) {
        if (solicitanteId != null) {
            return listarPorSolicitante.executar(solicitanteId);
        }
        if (status != null) {
            return listarPorStatus.executar(status);
        }
        throw new com.requisitos.avaliacaofilmes.TelaScore.aplicacao.EntradaInvalidaException("Informe 'solicitanteId' ou 'status' como parâmetro.");
    }

    record EditarSolicitacaoRequest(int solicitanteId, String tituloSugerido, String justificativa, String pais, Integer ano, String fotoUrl) {}
}
