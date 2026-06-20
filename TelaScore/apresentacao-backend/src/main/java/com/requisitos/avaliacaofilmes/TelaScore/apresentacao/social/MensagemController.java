package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.EnviarMensagemCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.EnviarMensagemComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.RemoverMensagemCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.RemoverMensagemComando;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {

    private final EnviarMensagemCasoDeUso enviarMensagem;
    private final RemoverMensagemCasoDeUso removerMensagem;

    public MensagemController(EnviarMensagemCasoDeUso enviarMensagem,
                              RemoverMensagemCasoDeUso removerMensagem) {
        this.enviarMensagem = enviarMensagem;
        this.removerMensagem = removerMensagem;
    }

    @PostMapping
    public ResponseEntity<Void> enviar(@RequestBody EnviarMensagemRequest body) {
        enviarMensagem.executar(new EnviarMensagemComando(body.remetenteId(), body.destinatarioId(), body.texto()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{mensagemId}")
    public ResponseEntity<Void> remover(@PathVariable int mensagemId) {
        removerMensagem.executar(new RemoverMensagemComando(mensagemId));
        return ResponseEntity.noContent().build();
    }

    public static record EnviarMensagemRequest(int remetenteId, int destinatarioId, String texto) {}
}
