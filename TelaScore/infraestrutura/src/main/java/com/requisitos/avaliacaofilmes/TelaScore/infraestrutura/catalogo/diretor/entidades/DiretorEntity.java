package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.diretor.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "diretor")
public class DiretorEntity {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String nome;

    public DiretorEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}