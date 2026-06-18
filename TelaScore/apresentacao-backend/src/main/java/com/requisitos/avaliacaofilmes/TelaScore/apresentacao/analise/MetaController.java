package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.*;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.PontuacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;

@RestController
@RequestMapping("/api/metas")
public class MetaController {
    private final CriarMetaCasoDeUso criarMeta;
    private final AdicionarProgressoMetaCasoDeUso adicionarProgresso;
    private final RemoverProgressoMetaCasoDeUso removerProgresso;
    private final EstenderPrazoMetaCasoDeUso estenderPrazo;
    private final ListarMetasPorUsuarioCasoDeUso listarMetas;
    private final CriarMetaSistemaCasoDeUso criarMetaSistema;
    private final MetaSistemaRepositorio metasSistema;
    private final MetaRepositorio metas;
    private final PontuacaoServico pontuacao;
    private final SessaoUsuario sessao;

    public MetaController(
            CriarMetaCasoDeUso criarMeta,
            AdicionarProgressoMetaCasoDeUso adicionarProgresso,
            RemoverProgressoMetaCasoDeUso removerProgresso,
            EstenderPrazoMetaCasoDeUso estenderPrazo,
            ListarMetasPorUsuarioCasoDeUso listarMetas,
            CriarMetaSistemaCasoDeUso criarMetaSistema,
            MetaSistemaRepositorio metasSistema,
            MetaRepositorio metas,
            PontuacaoServico pontuacao,
            SessaoUsuario sessao) {
        this.criarMeta = criarMeta;
        this.adicionarProgresso = adicionarProgresso;
        this.removerProgresso = removerProgresso;
        this.estenderPrazo = estenderPrazo;
        this.listarMetas = listarMetas;
        this.criarMetaSistema = criarMetaSistema;
        this.metasSistema = metasSistema;
        this.metas = metas;
        this.pontuacao = pontuacao;
        this.sessao = sessao;
    }

    @GetMapping
    public List<MetaResumo> listar() {
        return listarMetas.executar(usuarioAtual().getId().getId());
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody CriarMetaComando comando) {
        try {
            UsuarioLogado usuario = usuarioAtual();
            criarMeta.executar(new CriarMetaComando(
                    usuario.getId().getId(), comando.titulo(),
                    comando.quantidadeAlvo(), comando.dataPrazo()));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/progresso")
    public ResultadoAtualizacaoMeta adicionar(
            @PathVariable int id, @RequestParam int quantidade) {
        exigirDono(id);
        return adicionarProgresso.executar(new AdicionarProgressoMetaComando(id, quantidade));
    }

    @PutMapping("/{id}/progresso/remover")
    public ResponseEntity<String> remover(
            @PathVariable int id, @RequestParam int quantidade) {
        exigirDono(id);
        removerProgresso.executar(new RemoverProgressoMetaComando(id, quantidade));
        return ResponseEntity.ok("Progresso atualizado.");
    }

    @PutMapping("/prazo")
    public ResponseEntity<String> prazo(@RequestBody EstenderPrazoMetaComando comando) {
        exigirDono(comando.metaId());
        estenderPrazo.executar(comando);
        return ResponseEntity.ok("Prazo alterado.");
    }

    @GetMapping("/pontuacao")
    public PontuacaoResumo pontuacao() {
        return new PontuacaoResumo(pontuacao.calcularTotal(usuarioAtual().getId()));
    }

    @GetMapping("/sistema")
    public List<MetaSistemaResumo> listarSistema() {
        usuarioAtual();
        return metasSistema.listarAtivas().stream().map(MetaSistemaResumo::de).toList();
    }

    @PostMapping("/sistema")
    public ResponseEntity<MetaSistemaResumo> criarSistema(@RequestBody CriarMetaSistemaRequest request) {
        UsuarioLogado admin = exigirAdmin();
        MetaSistemaResumo criada = criarMetaSistema.executar(new CriarMetaSistemaComando(
                request.titulo(), request.quantidadeAlvo(), request.duracaoDias(),
                admin.getId().getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    private UsuarioLogado usuarioAtual() {
        UsuarioLogado usuario = sessao.obterUsuarioLogado();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faça login para acessar suas metas.");
        }
        return usuario;
    }

    private UsuarioLogado exigirAdmin() {
        UsuarioLogado usuario = usuarioAtual();
        if (!usuario.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas administradores podem criar metas do sistema.");
        }
        return usuario;
    }

    private Meta exigirDono(int metaId) {
        Meta meta = metas.obter(new MetaId(metaId));
        if (meta == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta não encontrada.");
        }
        if (!usuarioAtual().isMesmoUsuario(meta.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta meta pertence a outro usuário.");
        }
        return meta;
    }

    public record CriarMetaSistemaRequest(String titulo, int quantidadeAlvo, int duracaoDias) {}
    public record PontuacaoResumo(int totalPontos) {}
}
