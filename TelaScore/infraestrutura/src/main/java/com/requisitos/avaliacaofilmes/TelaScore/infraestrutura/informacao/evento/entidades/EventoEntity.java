package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.evento.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "evento")
public class EventoEntity {

    @Id
    private Integer id;

    @Column(name = "criador_id", nullable = false)
    private Integer criadorId;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public EventoEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCriadorId() { return criadorId; }
    public void setCriadorId(Integer criadorId) { this.criadorId = criadorId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}
