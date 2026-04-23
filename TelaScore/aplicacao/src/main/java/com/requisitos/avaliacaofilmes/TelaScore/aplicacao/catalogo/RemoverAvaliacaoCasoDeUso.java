package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;


public class RemoverAvaliacaoCasoDeUso {

    private final AvaliacaoRepositorio avaliacaoRepositorio;

    public RemoverAvaliacaoCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    public void executar(int avaliacaoIdValor) {
        AvaliacaoId avaliacaoId = new AvaliacaoId(avaliacaoIdValor);

        if (avaliacaoRepositorio.obter(avaliacaoId) == null) {
            throw new IllegalArgumentException("Avaliação não encontrada: " + avaliacaoIdValor);
        }

        avaliacaoRepositorio.remover(avaliacaoId);
    }
}