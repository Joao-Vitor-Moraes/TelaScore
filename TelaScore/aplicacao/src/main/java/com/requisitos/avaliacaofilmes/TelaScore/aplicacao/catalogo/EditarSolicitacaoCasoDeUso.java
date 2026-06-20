package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;

public class EditarSolicitacaoCasoDeUso {

    private final SolicitacaoRepositorio solicitacaoRepositorio;

    public EditarSolicitacaoCasoDeUso(SolicitacaoRepositorio solicitacaoRepositorio) {
        this.solicitacaoRepositorio = solicitacaoRepositorio;
    }

    public void executar(EditarSolicitacaoComando comando) {
        SolicitacaoFilme solicitacao = solicitacaoRepositorio.obter(new SolicitacaoId(comando.solicitacaoId()));
        if (solicitacao == null) {
            throw new IllegalArgumentException("Solicitação não encontrada.");
        }
        if (solicitacao.getSolicitanteId().getId() != comando.solicitanteId()) {
            throw new IllegalStateException("Apenas o solicitante pode editar esta solicitação.");
        }
        solicitacao.editar(
                comando.tituloSugerido(),
                comando.justificativa(),
                comando.pais(),
                comando.ano(),
                comando.fotoUrl()
        );
        solicitacaoRepositorio.salvar(solicitacao);
    }
}
