package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

public class ComunidadeResumo {
    private final int id;
    private final String nome;

    public ComunidadeResumo(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
}