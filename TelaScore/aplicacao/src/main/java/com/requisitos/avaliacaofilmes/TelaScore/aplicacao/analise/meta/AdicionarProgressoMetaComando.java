package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

public class AdicionarProgressoMetaComando {
    
    private final Integer metaId;
    private final int quantidade;

    public AdicionarProgressoMetaComando(Integer metaId, int quantidade) {
        this.metaId = metaId;
        this.quantidade = quantidade;
    }

    public Integer getMetaId() {
        return metaId;
    }

    public int getQuantidade() {
        return quantidade;
    }
}