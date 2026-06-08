package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;

public class ObterSolicitacaoCasoDeUso {

    private final SolicitacaoRepositorio solicitacaoRepositorio;

    public ObterSolicitacaoCasoDeUso(SolicitacaoRepositorio solicitacaoRepositorio) {
        this.solicitacaoRepositorio = solicitacaoRepositorio;
    }

    public SolicitacaoResumo executar(int solicitacaoId) {
        SolicitacaoFilme solicitacao = solicitacaoRepositorio.obter(new SolicitacaoId(solicitacaoId));
        if (solicitacao == null) {
            throw new IllegalArgumentException("Solicitação não encontrada.");
        }
        return new SolicitacaoResumo(
                solicitacao.getId().getId(),
                solicitacao.getSolicitanteId().getId(),
                solicitacao.getTituloSugerido(),
                solicitacao.getJustificativa(),
                solicitacao.getStatus().name(),
                solicitacao.getDataCriacao(),
                solicitacao.getFeedbackAdmin()
        );
    }
}
