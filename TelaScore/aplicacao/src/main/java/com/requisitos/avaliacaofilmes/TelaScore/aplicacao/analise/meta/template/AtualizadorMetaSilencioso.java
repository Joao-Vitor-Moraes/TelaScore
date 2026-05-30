package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;

public class AtualizadorMetaSilencioso extends AtualizadorMetaTemplate {

    public AtualizadorMetaSilencioso(MetaRepositorio metaRepositorio) {
        super(metaRepositorio);
    }

    @Override
    protected void executarEfeitoColateral(Meta meta, boolean concluiuAgora) {
        System.out.println("[LOG SISTEMA] O progresso da meta ID " + meta.getId().getId() + " foi atualizado silenciosamente.");
    }
}