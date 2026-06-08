package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitarFilmeCasoDeUso solicitarFilme;
    private final ObterSolicitacaoCasoDeUso obterSolicitacao;
    private final CancelarSolicitacaoFilmeCasoDeUso cancelarSolicitacao;
    private final ListarSolicitacoesPorSolicitanteCasoDeUso listarPorSolicitante;
    private final ListarSolicitacoesPorStatusCasoDeUso listarPorStatus;

    public SolicitacaoController(SolicitarFilmeCasoDeUso solicitarFilme,
            ObterSolicitacaoCasoDeUso obterSolicitacao,
            CancelarSolicitacaoFilmeCasoDeUso cancelarSolicitacao,
            ListarSolicitacoesPorSolicitanteCasoDeUso listarPorSolicitante,
            ListarSolicitacoesPorStatusCasoDeUso listarPorStatus) {
        this.solicitarFilme = solicitarFilme;
        this.obterSolicitacao = obterSolicitacao;
        this.cancelarSolicitacao = cancelarSolicitacao;
        this.listarPorSolicitante = listarPorSolicitante;
        this.listarPorStatus = listarPorStatus;
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
}
