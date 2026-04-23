// ─── APLICAÇÃO: Casos de uso — Avaliações ────────────────────────────────────

package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;


public class ObterAvaliacaoCasoDeUso {

    private final AvaliacaoRepositorio avaliacaoRepositorio;

    public ObterAvaliacaoCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    public AvaliacaoResumo executar(int avaliacaoIdValor) {
        AvaliacaoId avaliacaoId = new AvaliacaoId(avaliacaoIdValor);

        Avaliacao avaliacao = avaliacaoRepositorio.obter(avaliacaoId);
        if (avaliacao == null) {
            throw new IllegalArgumentException("Avaliação não encontrada: " + avaliacaoIdValor);
        }

        return toResumo(avaliacao);
    }

    static AvaliacaoResumo toResumo(Avaliacao avaliacao) {
        return new AvaliacaoResumo(
                avaliacao.getId().getId(),
                avaliacao.getUsuarioId().getId(),
                avaliacao.getNota().getValor(),
                avaliacao.getResenha(),
                avaliacao.getDataAvaliacao()
        );
    }
}