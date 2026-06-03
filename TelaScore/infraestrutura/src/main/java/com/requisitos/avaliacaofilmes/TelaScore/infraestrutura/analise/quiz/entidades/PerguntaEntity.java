package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pergunta")
public class PerguntaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "pergunta_id") // Une com a tabela alternativa
    private List<AlternativaEntity> alternativas = new ArrayList<>();

    public PerguntaEntity() {}

    // Getters e Setters...
}
