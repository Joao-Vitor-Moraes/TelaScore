package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "comunidade")
public class ComunidadeEntity {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}