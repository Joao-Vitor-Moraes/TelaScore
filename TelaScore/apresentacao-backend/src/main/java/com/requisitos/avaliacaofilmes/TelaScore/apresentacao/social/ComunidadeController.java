package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.social;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.MembroComunidade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

@RestController
@RequestMapping("/api/comunidades")
public class ComunidadeController {

    private final CriarComunidadeCasoDeUso criarComunidade;
    private final EntrarComunidade entrarComunidade;
    private final RemoverMembroCasoDeUso removerMembro;
    private final ListarComunidadesUsuarioCasoDeUso listarComunidades;
    private final ListarTodasComunidadesCasoDeUso listarTodasComunidades;
    private final ListarMembrosComunidadeCasoDeUso listarMembros;
    private final PromoverMembroCasoDeUso promoverMembro;
    private final ExcluirComunidadeCasoDeUso excluirComunidade;
    private final RebaixarMembroCasoDeUso rebaixarMembro;

    public ComunidadeController(CriarComunidadeCasoDeUso criarComunidade,
                                EntrarComunidade entrarComunidade,
                                RemoverMembroCasoDeUso removerMembro,
                                ListarComunidadesUsuarioCasoDeUso listarComunidades,
                                ListarTodasComunidadesCasoDeUso listarTodasComunidades,
                                ListarMembrosComunidadeCasoDeUso listarMembros,
                                PromoverMembroCasoDeUso promoverMembro,
                                ExcluirComunidadeCasoDeUso excluirComunidade,
                                RebaixarMembroCasoDeUso rebaixarMembro) {
        this.criarComunidade = criarComunidade;
        this.entrarComunidade = entrarComunidade;
        this.removerMembro = removerMembro;
        this.listarComunidades = listarComunidades;
        this.listarTodasComunidades = listarTodasComunidades;
        this.listarMembros = listarMembros;
        this.promoverMembro = promoverMembro;
        this.excluirComunidade = excluirComunidade;
        this.rebaixarMembro = rebaixarMembro;
    }

    @PostMapping
    public ResponseEntity<Void> criarComunidade(@RequestBody CriarComunidadeRequest body) {
        UsuarioId criadorId = new UsuarioId(body.criadorId());

        CriarComunidadeComando comando = new CriarComunidadeComando(
                body.idSugerido(),
                body.nome(),
                body.descricao(),
                criadorId
        );

        criarComunidade.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{comunidadeId}/membros")
    public ResponseEntity<Void> entrarNaComunidade(@PathVariable int comunidadeId,
                                                   @RequestBody EntrarComunidadeRequest body) {
        UsuarioId usuarioId = new UsuarioId(body.usuarioId());
        entrarComunidade.executar(comunidadeId, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{comunidadeId}/membros/{usuarioId}")
    public ResponseEntity<Void> removerMembro(@PathVariable int comunidadeId,
                                              @PathVariable int usuarioId,
                                              @RequestHeader("X-Usuario-Id") int solicitanteId) {
        removerMembro.executar(comunidadeId, usuarioId, solicitanteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> listarComunidades(@RequestParam(required = false) Integer usuarioId) {
        if (usuarioId != null) {
            return ResponseEntity.ok(listarComunidades.executar(usuarioId));
        }
        return ResponseEntity.ok(listarTodasComunidades.executar());
    }

    @GetMapping("/{comunidadeId}/membros")
    public ResponseEntity<List<MembroComunidade>> listarMembros(@PathVariable int comunidadeId) {
        List<MembroComunidade> membros = listarMembros.executar(comunidadeId);
        return ResponseEntity.ok(membros);
    }

    @PutMapping("/{comunidadeId}/membros/{usuarioId}/promover")
    public ResponseEntity<Void> promoverParaModerador(@PathVariable int comunidadeId,
                                                      @PathVariable int usuarioId,
                                                      @RequestHeader("X-Usuario-Id") int solicitanteId) {
        promoverMembro.executar(comunidadeId, usuarioId, solicitanteId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{comunidadeId}/membros/{usuarioId}/rebaixar")
    public ResponseEntity<Void> rebaixarParaMembro(@PathVariable int comunidadeId,
                                                   @PathVariable int usuarioId,
                                                   @RequestHeader("X-Usuario-Id") int solicitanteId) {
        rebaixarMembro.executar(comunidadeId, usuarioId, solicitanteId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{comunidadeId}")
    public ResponseEntity<Void> excluirComunidade(@PathVariable int comunidadeId,
                                                  @RequestHeader("X-Usuario-Id") int solicitanteId) {
        excluirComunidade.executar(comunidadeId, solicitanteId);
        return ResponseEntity.noContent().build();
    }

    public static record CriarComunidadeRequest(int idSugerido, String nome, String descricao, int criadorId) {}
    public static record EntrarComunidadeRequest(int usuarioId) {}
}