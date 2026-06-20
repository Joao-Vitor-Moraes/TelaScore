package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

public class AdicionarProgressoMetaComando {
    
    private final Integer metaId;
    private final int quantidade;
    private final boolean silencioso;

    public AdicionarProgressoMetaComando(Integer metaId, int quantidade) {
        this(metaId, quantidade, false);
    }

    public AdicionarProgressoMetaComando(Integer metaId, int quantidade, boolean silencioso) {
        this.metaId = metaId;
        this.quantidade = quantidade;
        this.silencioso = silencioso;
    }

    public Integer getMetaId() {
        return metaId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public boolean isSilencioso() {
        return silencioso;
    }
}
