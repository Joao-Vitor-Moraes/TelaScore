package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;

public class EstenderPrazoMetaCasoDeUso {

    private final MetaRepositorio metaRepositorio;

    public EstenderPrazoMetaCasoDeUso(MetaRepositorio metaRepositorio) {
        this.metaRepositorio = metaRepositorio;
    }

    public void executar(EstenderPrazoMetaComando comando) {
        Meta meta = metaRepositorio.obter(new MetaId(comando.metaId()));

        if (meta == null) {
            throw new IllegalArgumentException("Meta não encontrada.");
        }

        meta.estenderPrazo(comando.novoPrazo());
        metaRepositorio.salvar(meta);
    }
}