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
}
