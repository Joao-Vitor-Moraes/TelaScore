package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.observer;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;

import java.util.List;

public class RemoverAvaliacoesDoFilmeObserver implements FilmeObserver {

    private final AvaliacaoRepositorio avaliacaoRepositorio;

    public RemoverAvaliacoesDoFilmeObserver(AvaliacaoRepositorio avaliacaoRepositorio) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    @Override
    public void aoRemoverFilme(FilmeId filmeId) {
        List<Avaliacao> avaliacoes = avaliacaoRepositorio.pesquisarPorFilme(filmeId);

        for (Avaliacao avaliacao : avaliacoes) {
            avaliacaoRepositorio.remover(avaliacao.getId());
        }

        System.out.println(
            "[OBSERVER] " + avaliacoes.size() + " avaliação(ões) removida(s) " +
            "para o filme: " + filmeId.getCodigo()
        );
    }
}