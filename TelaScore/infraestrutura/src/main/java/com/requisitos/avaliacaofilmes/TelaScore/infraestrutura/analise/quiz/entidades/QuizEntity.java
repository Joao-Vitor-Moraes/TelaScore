package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz")
public class QuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String titulo;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id") // Une com a chave estrangeira da tabela pergunta
    private List<PerguntaEntity> perguntas = new ArrayList<>();

    public QuizEntity() {}

    // Getters e Setters...
    public int getId() { return id; }
    public void setId(int id) {
        this.id = id;
    }

    // Getters e Setters para as Strings
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    // Getter e Setter para a lista de Perguntas (importante para o cascateamento)
    public List<PerguntaEntity> getPerguntas() { return perguntas; }
    public void setPerguntas(List<PerguntaEntity> perguntas) { this.perguntas = perguntas; }

}
