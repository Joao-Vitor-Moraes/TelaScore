package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "alternativa")
public class AlternativaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;
    
    @Column(nullable = false)
    private boolean correta;

    public AlternativaEntity() {}

    // Getters e Setters...
    // O banco gera o ID, mas o mapeador lê se necessário
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Usado no 'obter' para ler o texto e no 'salvar' para preencher o banco
    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    // ATENÇÃO: Para atributos booleans em Java, o padrão do getter começa com 'is' em vez de 'get'
    public boolean isCorreta() {
        return correta;
    }

    public void setCorreta(boolean correta) {
        this.correta = correta;
    }
}
