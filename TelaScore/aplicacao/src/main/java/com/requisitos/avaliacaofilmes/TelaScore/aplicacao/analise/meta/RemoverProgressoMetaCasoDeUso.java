package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;

public class RemoverProgressoMetaCasoDeUso {

    private final MetaRepositorio metaRepositorio;

    public RemoverProgressoMetaCasoDeUso(MetaRepositorio metaRepositorio) {
        this.metaRepositorio = metaRepositorio;
    }

    public void executar(RemoverProgressoMetaComando comando) {
        Meta meta = metaRepositorio.obter(new MetaId(comando.metaId()));

        if (meta == null) {
            throw new IllegalArgumentException("Meta não encontrada.");
        }

        meta.removerProgresso(comando.quantidade());
        metaRepositorio.salvar(meta);
    }
}