package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia;

public enum CategoriaNoticia {
    LANCAMENTO("Lançamento"),
    CRITICA("Crítica"),
    EVENTO("Evento"),
    CURIOSIDADE("Curiosidade");

    private final String descricao;

    CategoriaNoticia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}