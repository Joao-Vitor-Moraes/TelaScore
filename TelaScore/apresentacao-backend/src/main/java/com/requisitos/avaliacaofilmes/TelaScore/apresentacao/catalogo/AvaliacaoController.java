package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AtualizarAvaliacaoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AtualizarAvaliacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AvaliacaoResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AvaliarFilmeComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AvaliarFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListarAvaliacoesPorFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ObterAvaliacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverAvaliacaoCasoDeUso;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliarFilmeCasoDeUso avaliarFilme;
    private final ObterAvaliacaoCasoDeUso obterAvaliacao;
    private final AtualizarAvaliacaoCasoDeUso atualizarAvaliacao;
    private final RemoverAvaliacaoCasoDeUso removerAvaliacao;
    private final ListarAvaliacoesPorFilmeCasoDeUso listarPorFilme;

    public AvaliacaoController(AvaliarFilmeCasoDeUso avaliarFilme,
                                ObterAvaliacaoCasoDeUso obterAvaliacao,
                                AtualizarAvaliacaoCasoDeUso atualizarAvaliacao,
                                RemoverAvaliacaoCasoDeUso removerAvaliacao,
                                ListarAvaliacoesPorFilmeCasoDeUso listarPorFilme) {
        this.avaliarFilme = avaliarFilme;
        this.obterAvaliacao = obterAvaliacao;
        this.atualizarAvaliacao = atualizarAvaliacao;
        this.removerAvaliacao = removerAvaliacao;
        this.listarPorFilme = listarPorFilme;
    }

    // POST /avaliacoes
    @PostMapping
    public ResponseEntity<Void> avaliar(@RequestBody AvaliarFilmeComando comando) {
        avaliarFilme.executar(comando);
        return ResponseEntity.status(201).build();
    }

    // GET /avaliacoes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoResumo> obter(@PathVariable int id) {
        AvaliacaoResumo resumo = obterAvaliacao.executar(id);
        return ResponseEntity.ok(resumo);
    }

    // GET /avaliacoes/filme/{filmeId}?usuarioId=1
    @GetMapping("/filme/{filmeId}")
    public ResponseEntity<List<AvaliacaoResumo>> listarPorFilme(
            @PathVariable String filmeId,
            @RequestParam(required = false) Integer usuarioId) {

        List<AvaliacaoResumo> avaliacoes;

        if (usuarioId != null) {
            avaliacoes = listarPorFilme.executarParaUsuario(filmeId, usuarioId);
        } else {
            avaliacoes = listarPorFilme.executar(filmeId);
        }

        return ResponseEntity.ok(avaliacoes);
    }

    // PUT /avaliacoes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id,
                                           @RequestBody AtualizarAvaliacaoComando comando) {
        AtualizarAvaliacaoComando comandoComId = new AtualizarAvaliacaoComando(
                id,
                comando.valorNota(),
                comando.resenha()
        );
        atualizarAvaliacao.executar(comandoComId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /avaliacoes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        removerAvaliacao.executar(id);
        return ResponseEntity.noContent().build();
    }
}