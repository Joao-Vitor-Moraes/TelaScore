package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.quiz.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tentativa_quiz")
public class TentativaQuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quiz_id", nullable = false)
    private int quizId;

    @Column(name = "usuario_id", nullable = false)
    private int usuarioId;

    @Column(nullable = false)
    private int pontuacao;

    @Column(name = "data_tentativa", nullable = false)
    private LocalDateTime dataTentativa;

    public TentativaQuizEntity() {}

    public TentativaQuizEntity(int quizId, int usuarioId, int pontuacao, LocalDateTime dataTentativa) {
        this.quizId = quizId;
        this.usuarioId = usuarioId;
        this.pontuacao = pontuacao;
        this.dataTentativa = dataTentativa;
    }

    // Getters e Setters (pode gerar automaticamente pelo VS Code)
    public int getId() { return id; }
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public int getPontuacao() { return pontuacao; }
    public void setPontuacao(int pontuacao) { this.pontuacao = pontuacao; }
    public LocalDateTime getDataTentativa() { return dataTentativa; }
    public void setDataTentativa(LocalDateTime dataTentativa) { this.dataTentativa = dataTentativa; }
}
