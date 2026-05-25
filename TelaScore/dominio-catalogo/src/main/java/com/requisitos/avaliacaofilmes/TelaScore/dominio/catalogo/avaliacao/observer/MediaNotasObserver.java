package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.observer;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;

import java.util.List;

public class MediaNotasObserver implements AvaliacaoObserver {

    private final AvaliacaoRepositorio avaliacaoRepositorio;

    public MediaNotasObserver(AvaliacaoRepositorio avaliacaoRepositorio) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    @Override
    public void aoRegistrarAvaliacao(Avaliacao avaliacao) {
        List<Avaliacao> avaliacoes = avaliacaoRepositorio.pesquisarPorFilme(avaliacao.getFilmeId());

        double media = avaliacoes.stream()
                .mapToInt(a -> a.getNota().getValor())
                .average()
                .orElse(0.0);

        System.out.printf(
            "[MÉDIA] Filme %s agora tem %.1f estrela(s) de média com %d avaliação(ões).%n",
            avaliacao.getFilmeId().getCodigo(),
            media,
            avaliacoes.size()
        );
    }
}