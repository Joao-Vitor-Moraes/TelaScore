package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.*;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.EditarSolicitacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.EditarSolicitacaoComando;

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

    public SolicitacaoController(EditarSolicitacaoCasoDeUso editarSolicitacao,
            SolicitarFilmeCasoDeUso solicitarFilme,
            ObterSolicitacaoCasoDeUso obterSolicitacao,
            CancelarSolicitacaoFilmeCasoDeUso cancelarSolicitacao,
            ListarSolicitacoesPorSolicitanteCasoDeUso listarPorSolicitante,
            ListarSolicitacoesPorStatusCasoDeUso listarPorStatus,
            AvaliarSolicitacaoFilmeCasoDeUso avaliarSolicitacao,
            SolicitarAjustesFilmeCasoDeUso solicitarAjustes) {
        this.editarSolicitacao = editarSolicitacao;
        this.solicitarFilme = solicitarFilme;
        this.obterSolicitacao = obterSolicitacao;
        this.cancelarSolicitacao = cancelarSolicitacao;
        this.listarPorSolicitante = listarPorSolicitante;
        this.listarPorStatus = listarPorStatus;
        this.avaliarSolicitacao = avaliarSolicitacao;
        this.solicitarAjustes = solicitarAjustes;
    }

    @PutMapping("/{solicitacaoId}")
    public ResponseEntity<Void> editar(@PathVariable int solicitacaoId, @RequestBody EditarSolicitacaoRequest body) {
        editarSolicitacao.executar(new EditarSolicitacaoComando(
                solicitacaoId, body.solicitanteId(), body.tituloSugerido(),
                body.justificativa(), body.pais(), body.ano(), body.genero(), body.fotoUrl()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{solicitacaoId}")
    public SolicitacaoResumo obter(@PathVariable int solicitacaoId) {
        return obterSolicitacao.executar(solicitacaoId);
    }

    @PostMapping
    public ResponseEntity<SolicitacaoResumo> solicitar(@RequestBody SolicitarFilmeComando comando) {
        SolicitacaoResumo resumo = solicitarFilme.executar(comando);
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
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{solicitacaoId}/ajustes")
    public ResponseEntity<Void> solicitarAjustes(@PathVariable int solicitacaoId,
            @RequestBody SolicitarAjustesFilmeComando corpo) {
        solicitarAjustes.executar(new SolicitarAjustesFilmeComando(solicitacaoId, corpo.avaliadorId(), corpo.feedback()));
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

    record EditarSolicitacaoRequest(int solicitanteId, String tituloSugerido, String justificativa, String pais, Integer ano, String genero, String fotoUrl) {}
}
