package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AtualizarFilmeComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AtualizarFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.CadastrarFilmeComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.CadastrarFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.FilmeResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ObterFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverFilmeCasoDeUso;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/filmes")
public class FilmeController {

    private final CadastrarFilmeCasoDeUso cadastrarFilme;
    private final ObterFilmeCasoDeUso obterFilme;
    private final AtualizarFilmeCasoDeUso atualizarFilme;
    private final RemoverFilmeCasoDeUso removerFilme;

    public FilmeController(CadastrarFilmeCasoDeUso cadastrarFilme,
                           ObterFilmeCasoDeUso obterFilme,
                           AtualizarFilmeCasoDeUso atualizarFilme,
                           RemoverFilmeCasoDeUso removerFilme) {
        this.cadastrarFilme = cadastrarFilme;
        this.obterFilme = obterFilme;
        this.atualizarFilme = atualizarFilme;
        this.removerFilme = removerFilme;
    }

    // POST /filmes
    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody CadastrarFilmeComando comando) {
        cadastrarFilme.executar(comando);
        return ResponseEntity.status(201).build();
    }

    // GET /filmes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<FilmeResumo> obter(@PathVariable String id) {
        FilmeResumo resumo = obterFilme.executar(id);
        return ResponseEntity.ok(resumo);
    }

    // PUT /filmes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable String id,
                                           @RequestBody AtualizarFilmeComando comando) {
        // Garante que o id da URL é o mesmo do comando
        AtualizarFilmeComando comandoComId = new AtualizarFilmeComando(
                id,
                comando.titulo(),
                comando.sinopse(),
                comando.anoLancamento()
        );
        atualizarFilme.executar(comandoComId);
        return ResponseEntity.noContent().build();
    }

    // DELETE /filmes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        removerFilme.executar(id);
        return ResponseEntity.noContent().build();
    }
}