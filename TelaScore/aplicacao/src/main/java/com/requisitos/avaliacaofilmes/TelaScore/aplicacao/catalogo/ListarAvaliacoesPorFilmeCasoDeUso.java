package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;

import java.util.List;
import java.util.stream.Collectors;

public class ListarAvaliacoesPorFilmeCasoDeUso {

    private final AvaliacaoRepositorio avaliacaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public ListarAvaliacoesPorFilmeCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio,
                                              FilmeRepositorio filmeRepositorio) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    public List<AvaliacaoResumo> executar(String filmeIdCodigo) {
        FilmeId filmeId = new FilmeId(filmeIdCodigo);

        if (filmeRepositorio.obter(filmeId) == null) {
            throw new IllegalArgumentException("Filme não encontrado: " + filmeIdCodigo);
        }

        return avaliacaoRepositorio.pesquisarPorFilme(filmeId)
                .stream()
                .map(ObterAvaliacaoCasoDeUso::toResumo)
                .collect(Collectors.toList());
    }
}