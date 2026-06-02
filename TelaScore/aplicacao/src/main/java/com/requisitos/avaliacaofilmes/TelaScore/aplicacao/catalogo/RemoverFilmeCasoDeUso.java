package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.observer.RemoverAvaliacoesDoFilmeObserver;

public class RemoverFilmeCasoDeUso {

    private final FilmeServico filmeServico;

    public RemoverFilmeCasoDeUso(FilmeRepositorio filmeRepositorio,
                                  AvaliacaoRepositorio avaliacaoRepositorio) {

        this.filmeServico = new FilmeServico(filmeRepositorio);

        this.filmeServico.adicionarObserver(
                new RemoverAvaliacoesDoFilmeObserver(avaliacaoRepositorio));
    }

    public void executar(String filmeIdCodigo) {
        FilmeId filmeId = new FilmeId(filmeIdCodigo);
        filmeServico.remover(filmeId);
    }
}