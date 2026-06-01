package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitacoes")
public class SolicitacaoController {

    private final SolicitarFilmeCasoDeUso solicitarFilme;
    private final CancelarSolicitacaoFilmeCasoDeUso cancelarSolicitacao;

    public SolicitacaoController(SolicitarFilmeCasoDeUso solicitarFilme,
            CancelarSolicitacaoFilmeCasoDeUso cancelarSolicitacao) {
        this.solicitarFilme = solicitarFilme;
        this.cancelarSolicitacao = cancelarSolicitacao;
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
}
