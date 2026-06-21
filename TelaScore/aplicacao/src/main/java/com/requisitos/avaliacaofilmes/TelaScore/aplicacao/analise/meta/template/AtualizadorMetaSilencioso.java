package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.ResultadoAtualizacaoMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;

public class AtualizadorMetaSilencioso extends AtualizadorMetaTemplate {
    public AtualizadorMetaSilencioso(MetaRepositorio metaRepositorio) {
        super(metaRepositorio);
    }

    @Override
    protected ResultadoAtualizacaoMeta executarEfeitoColateral(Meta meta, boolean concluiuAgora) {
        if (concluiuAgora) {
            meta.marcarPontosConcedidos();
        }

        return new ResultadoAtualizacaoMeta(
                meta.getId().getId(), meta.getQuantidadeAtual(), meta.getQuantidadeAlvo(),
                meta.getStatus().name(), "Progresso atualizado.", 0, 0);
    }
}
