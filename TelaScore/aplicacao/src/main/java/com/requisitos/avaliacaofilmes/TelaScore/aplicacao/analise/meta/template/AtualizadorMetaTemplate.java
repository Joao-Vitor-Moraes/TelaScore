package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;

public abstract class AtualizadorMetaTemplate {

    protected final MetaRepositorio metaRepositorio;

    public AtualizadorMetaTemplate(MetaRepositorio metaRepositorio) {
        this.metaRepositorio = metaRepositorio;
    }


    public final void executarAtualizacao(MetaId metaId, int quantidadeAssistida) {
        Meta meta = metaRepositorio.obter(metaId);
        if (meta == null) {
            throw new IllegalArgumentException("Meta não encontrada para o ID fornecido.");
        }

        boolean estavaConcluidaAntes = meta.getStatus().name().equals("CONCLUIDA");

        meta.adicionarProgresso(quantidadeAssistida);

        metaRepositorio.salvar(meta);

        boolean concluiuAgora = !estavaConcluidaAntes && meta.getStatus().name().equals("CONCLUIDA");

        executarEfeitoColateral(meta, concluiuAgora);
    }

    protected abstract void executarEfeitoColateral(Meta meta, boolean concluiuAgora);
}