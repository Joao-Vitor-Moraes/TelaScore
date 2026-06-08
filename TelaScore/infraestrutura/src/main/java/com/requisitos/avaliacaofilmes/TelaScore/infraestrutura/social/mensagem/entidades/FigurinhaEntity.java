package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "figurinha")
public class FigurinhaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false, unique = true)
    private String nome;
    
    @Column(name = "conteudo_imagem", nullable = false, columnDefinition = "TEXT")
    private String conteudoImagem;

    public FigurinhaEntity() {}

    public FigurinhaEntity(String nome, String conteudoImagem) {
        this.nome = nome;
        this.conteudoImagem = conteudoImagem;
    }

    // Getters e Setters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getConteudoImagem() { return conteudoImagem; }
}