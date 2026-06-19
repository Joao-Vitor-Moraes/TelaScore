package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.ResultadoAtualizacaoMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.StatusMeta;

public abstract class AtualizadorMetaTemplate {
    protected final MetaRepositorio metaRepositorio;

    protected AtualizadorMetaTemplate(MetaRepositorio metaRepositorio) {
        this.metaRepositorio = metaRepositorio;
    }

    public final ResultadoAtualizacaoMeta executarAtualizacao(MetaId metaId, int quantidadeAssistida) {
        Meta meta = metaRepositorio.obter(metaId);
        if (meta == null) {
            throw new IllegalArgumentException("Meta não encontrada.");
        }

        boolean estavaConcluida = meta.getStatus() == StatusMeta.CONCLUIDA;
        meta.adicionarProgresso(quantidadeAssistida);
        boolean concluiuAgora = !estavaConcluida && meta.getStatus() == StatusMeta.CONCLUIDA;

        ResultadoAtualizacaoMeta resultado = executarEfeitoColateral(meta, concluiuAgora);
        metaRepositorio.salvar(meta);
        return resultado;
    }

    protected abstract ResultadoAtualizacaoMeta executarEfeitoColateral(Meta meta, boolean concluiuAgora);
}
