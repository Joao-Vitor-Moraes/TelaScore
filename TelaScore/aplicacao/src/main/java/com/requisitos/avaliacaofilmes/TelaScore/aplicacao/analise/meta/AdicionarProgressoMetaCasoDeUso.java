package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;

public class AdicionarProgressoMetaCasoDeUso {

    private final MetaRepositorio metaRepositorio;

    public AdicionarProgressoMetaCasoDeUso(MetaRepositorio metaRepositorio) {
        this.metaRepositorio = metaRepositorio;
    }

    public void executar(AdicionarProgressoMetaComando comando) {
        MetaId metaId = new MetaId(comando.getMetaId());
        Meta meta = metaRepositorio.obter(metaId);

        if (meta == null) {
            throw new IllegalArgumentException("Meta não encontrada para o ID fornecido.");
        }

        meta.adicionarProgresso(comando.getQuantidade());

        metaRepositorio.salvar(meta);
    }
}