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
    // Importante! O construtor do seu Domínio de Pergunta precisa do ID do banco
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    // Permite que o mapeador use .stream() para converter as alternativas para o Domínio
    public List<AlternativaEntity> getAlternativas() {
        return alternativas;
    }

    // Essencial para o método 'salvar' injetar a lista montada e disparar o Cascade
    public void setAlternativas(List<AlternativaEntity> alternativas) {
        this.alternativas = alternativas;
    }
}
