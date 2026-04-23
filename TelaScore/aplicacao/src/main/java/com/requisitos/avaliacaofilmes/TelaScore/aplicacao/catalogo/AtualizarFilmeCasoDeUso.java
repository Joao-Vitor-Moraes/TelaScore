package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;


public class AtualizarFilmeCasoDeUso {

    private final FilmeRepositorio filmeRepositorio;

    public AtualizarFilmeCasoDeUso(FilmeRepositorio filmeRepositorio) {
        this.filmeRepositorio = filmeRepositorio;
    }

    public void executar(AtualizarFilmeComando comando) {
        FilmeId filmeId = new FilmeId(comando.filmeId());

        Filme filme = filmeRepositorio.obter(filmeId);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado: " + comando.filmeId());
        }

        // Aplica apenas os campos que foram fornecidos
        if (comando.titulo() != null) {
            filme.setTitulo(comando.titulo());
        }
        if (comando.sinopse() != null) {
            filme.setSinopse(comando.sinopse());
        }
        if (comando.anoLancamento() != null) {
            filme.setAnoLancamento(comando.anoLancamento());
        }

        filmeRepositorio.salvar(filme);
    }
}