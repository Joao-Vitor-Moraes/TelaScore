package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

public class ComunidadeResumo {
    private final int id;
    private final String nome;
    private final String descricao;

    public ComunidadeResumo(int id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
}