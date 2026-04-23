package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;


public class RemoverFilmeCasoDeUso {

    private final FilmeRepositorio filmeRepositorio;

    public RemoverFilmeCasoDeUso(FilmeRepositorio filmeRepositorio) {
        this.filmeRepositorio = filmeRepositorio;
    }

    public void executar(String filmeIdCodigo) {
        FilmeId filmeId = new FilmeId(filmeIdCodigo);

        if (filmeRepositorio.obter(filmeId) == null) {
            throw new IllegalArgumentException("Filme não encontrado: " + filmeIdCodigo);
        }

        filmeRepositorio.remover(filmeId);
    }
}