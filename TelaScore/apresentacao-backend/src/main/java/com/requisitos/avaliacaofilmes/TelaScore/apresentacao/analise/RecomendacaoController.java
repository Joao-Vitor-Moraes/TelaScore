package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.EnviarRecomendacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.EnviarRecomendacaoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ResponderRecomendacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ResponderRecomendacaoComando;

@RestController
@RequestMapping("/api/recomendacoes")
public class RecomendacaoController {

    private final EnviarRecomendacaoCasoDeUso enviarRecomendacaoCasoDeUso;
    private final ResponderRecomendacaoCasoDeUso responderRecomendacaoCasoDeUso;

    public RecomendacaoController(EnviarRecomendacaoCasoDeUso enviarRecomendacaoCasoDeUso,
                                  ResponderRecomendacaoCasoDeUso responderRecomendacaoCasoDeUso) {
        this.enviarRecomendacaoCasoDeUso = enviarRecomendacaoCasoDeUso;
        this.responderRecomendacaoCasoDeUso = responderRecomendacaoCasoDeUso;
    }

    @PostMapping
    public ResponseEntity<String> enviarRecomendacao(@RequestBody EnviarRecomendacaoComando comando) {
        try {
            enviarRecomendacaoCasoDeUso.executar(comando);
            return ResponseEntity.ok("Recomendação enviada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao enviar recomendação: " + e.getMessage());
        }
    }

    @PutMapping("/reagir")
    public ResponseEntity<String> responderRecomendacao(@RequestBody ResponderRecomendacaoComando comando) {
        try {
            responderRecomendacaoCasoDeUso.executar(comando);
            return ResponseEntity.ok("Resposta registrada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao responder recomendação: " + e.getMessage());
        }
    }
}