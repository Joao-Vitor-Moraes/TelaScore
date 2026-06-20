package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.DeixarDeSeguirCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.DeixarDeSeguirComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.SeguirUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.SeguirUsuarioComando;

@RestController
@RequestMapping("/api/conexoes")
public class ConexaoController {

    private final SeguirUsuarioCasoDeUso seguirUsuario;
    private final DeixarDeSeguirCasoDeUso deixarDeSeguir;

    public ConexaoController(SeguirUsuarioCasoDeUso seguirUsuario,
                             DeixarDeSeguirCasoDeUso deixarDeSeguir) {
        this.seguirUsuario = seguirUsuario;
        this.deixarDeSeguir = deixarDeSeguir;
    }

    @PostMapping
    public ResponseEntity<Void> seguir(@RequestBody SeguirRequest body) {
        seguirUsuario.executar(new SeguirUsuarioComando(body.seguidorId(), body.seguidoId()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{seguidoId}")
    public ResponseEntity<Void> deixarDeSeguir(@PathVariable int seguidoId,
                                               @RequestHeader("X-Usuario-Id") int seguidorId) {
        deixarDeSeguir.executar(new DeixarDeSeguirComando(seguidorId, seguidoId));
        return ResponseEntity.noContent().build();
    }

    public static record SeguirRequest(int seguidorId, int seguidoId) {}
}
