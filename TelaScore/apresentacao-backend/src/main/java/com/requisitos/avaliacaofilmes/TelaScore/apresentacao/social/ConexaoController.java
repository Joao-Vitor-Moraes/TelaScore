package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.AmigoResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.DeixarDeSeguirCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.DeixarDeSeguirComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.ListarAmigosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.ListarConexoesCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.SeguirUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.SeguirUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;

import java.util.List;

@RestController
@RequestMapping("/api/conexoes")
public class ConexaoController {

    private final SeguirUsuarioCasoDeUso seguirUsuario;
    private final DeixarDeSeguirCasoDeUso deixarDeSeguir;
    private final ListarConexoesCasoDeUso listarConexoes;
    private final ListarAmigosCasoDeUso listarAmigos;
    private final ConexaoRepositorio conexaoRepositorio;

    public ConexaoController(SeguirUsuarioCasoDeUso seguirUsuario,
                             DeixarDeSeguirCasoDeUso deixarDeSeguir,
                             ListarConexoesCasoDeUso listarConexoes,
                             ListarAmigosCasoDeUso listarAmigos,
                             ConexaoRepositorio conexaoRepositorio) {
        this.seguirUsuario = seguirUsuario;
        this.deixarDeSeguir = deixarDeSeguir;
        this.listarConexoes = listarConexoes;
        this.listarAmigos = listarAmigos;
        this.conexaoRepositorio = conexaoRepositorio;
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

    @GetMapping("/{usuarioId}/seguindo")
    public ResponseEntity<List<AmigoResumo>> listarSeguindo(@PathVariable int usuarioId) {
        return ResponseEntity.ok(listarConexoes.listarSeguindo(usuarioId));
    }

    @GetMapping("/{usuarioId}/seguidores")
    public ResponseEntity<List<AmigoResumo>> listarSeguidores(@PathVariable int usuarioId) {
        return ResponseEntity.ok(listarConexoes.listarSeguidores(usuarioId));
    }

    @GetMapping("/{usuarioId}/amigos")
    public ResponseEntity<List<AmigoResumo>> listarAmigos(@PathVariable int usuarioId) {
        return ResponseEntity.ok(listarAmigos.executar(usuarioId));
    }

    @GetMapping("/status")
    public ResponseEntity<StatusConexaoResponse> status(@RequestParam int seguidorId,
                                                        @RequestParam int seguidoId) {
        UsuarioId seguidor = new UsuarioId(seguidorId);
        UsuarioId seguido = new UsuarioId(seguidoId);
        boolean seguindo = conexaoRepositorio.buscarConexao(seguidor, seguido) != null;
        boolean seguidoPor = conexaoRepositorio.buscarConexao(seguido, seguidor) != null;
        return ResponseEntity.ok(new StatusConexaoResponse(seguindo, seguidoPor, seguindo && seguidoPor));
    }

    public static record SeguirRequest(int seguidorId, int seguidoId) {}

    public static record StatusConexaoResponse(boolean seguindo, boolean seguidoPor, boolean amigo) {}
}
