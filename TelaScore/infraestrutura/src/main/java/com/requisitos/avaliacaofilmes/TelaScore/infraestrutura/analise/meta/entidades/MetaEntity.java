package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "meta")
public class MetaEntity {

    @Id
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private String titulo;

    @Column(name = "quantidade_alvo", nullable = false)
    private Integer quantidadeAlvo;

    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual;

    @Column(name = "data_prazo", nullable = false)
    private LocalDate dataPrazo; 

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String tipo = "FILMES";

    @Column(name = "genero_alvo")
    private String generoAlvo;

    @Column(name = "meta_sistema_id")
    private Integer metaSistemaId;

    @Column(name = "pontos_concedidos", nullable = false)
    private Boolean pontosConcedidos = false;

    @Column(name = "notificacao_ativa")
    private Boolean notificacaoAtiva = true;

    public MetaEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public Integer getQuantidadeAlvo() { return quantidadeAlvo; }
    public void setQuantidadeAlvo(Integer quantidadeAlvo) { this.quantidadeAlvo = quantidadeAlvo; }

    public Integer getQuantidadeAtual() { return quantidadeAtual; }
    public void setQuantidadeAtual(Integer quantidadeAtual) { this.quantidadeAtual = quantidadeAtual; }

    public LocalDate getDataPrazo() { return dataPrazo; }
    public void setDataPrazo(LocalDate dataPrazo) { this.dataPrazo = dataPrazo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getGeneroAlvo() { return generoAlvo; }
    public void setGeneroAlvo(String generoAlvo) { this.generoAlvo = generoAlvo; }
    public Integer getMetaSistemaId() { return metaSistemaId; }
    public void setMetaSistemaId(Integer metaSistemaId) { this.metaSistemaId = metaSistemaId; }
    public Boolean getPontosConcedidos() { return pontosConcedidos; }
    public void setPontosConcedidos(Boolean pontosConcedidos) { this.pontosConcedidos = pontosConcedidos; }
    public Boolean getNotificacaoAtiva() { return notificacaoAtiva; }
    public void setNotificacaoAtiva(Boolean notificacaoAtiva) { this.notificacaoAtiva = notificacaoAtiva; }
}
