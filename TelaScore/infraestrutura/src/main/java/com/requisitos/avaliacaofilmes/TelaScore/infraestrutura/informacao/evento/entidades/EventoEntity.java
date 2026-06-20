package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.evento.entidades;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Column(name = "visibilidade")
    private String visibilidade;

    @ElementCollection
    @CollectionTable(name = "evento_comunidade", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "comunidade_id")
    private List<Integer> comunidadesConvidadas = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "evento_convidado", joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "usuario_id")
    private List<Integer> convidados = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "evento_resposta", joinColumns = @JoinColumn(name = "evento_id"))
    @MapKeyColumn(name = "usuario_id")
    @Column(name = "resposta")
    private Map<Integer, String> respostas = new HashMap<>();

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

    public String getVisibilidade() { return visibilidade; }
    public void setVisibilidade(String visibilidade) { this.visibilidade = visibilidade; }

    public List<Integer> getComunidadesConvidadas() { return comunidadesConvidadas; }
    public void setComunidadesConvidadas(List<Integer> comunidadesConvidadas) { this.comunidadesConvidadas = comunidadesConvidadas; }

    public List<Integer> getConvidados() { return convidados; }
    public void setConvidados(List<Integer> convidados) { this.convidados = convidados; }

    public Map<Integer, String> getRespostas() { return respostas; }
    public void setRespostas(Map<Integer, String> respostas) { this.respostas = respostas; }
}
