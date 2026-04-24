package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.Recomendacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoRepositorio;

public class ResponderRecomendacaoCasoDeUso {

    private final RecomendacaoRepositorio repositorio;

    public ResponderRecomendacaoCasoDeUso(RecomendacaoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(ResponderRecomendacaoComando comando) {
        Recomendacao recomendacao = repositorio.obter(new RecomendacaoId(comando.recomendacaoId()));

        if (recomendacao == null) {
            throw new IllegalArgumentException("Recomendação não encontrada.");
        }
        if (comando.aceitar()) {
            recomendacao.aceitar();
        } else {
            recomendacao.rejeitar();
        }

        repositorio.salvar(recomendacao);
    }
}