package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.solicitacao.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_filme")
public class SolicitacaoFilmeEntity {

    @Id
    private Integer id;

    @Column(name = "solicitante_id", nullable = false)
    private Integer solicitanteId;

    @Column(name = "titulo_sugerido", nullable = false)
    private String tituloSugerido;

    private String justificativa;

    private String pais;

    private Integer ano;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(nullable = false)
    private String status;

    @Column(name = "feedback_admin")
    private String feedbackAdmin;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    public SolicitacaoFilmeEntity() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSolicitanteId() {
        return solicitanteId;
    }

    public void setSolicitanteId(Integer solicitanteId) {
        this.solicitanteId = solicitanteId;
    }

    public String getTituloSugerido() {
        return tituloSugerido;
    }

    public void setTituloSugerido(String tituloSugerido) {
        this.tituloSugerido = tituloSugerido;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeedbackAdmin() {
        return feedbackAdmin;
    }

    public void setFeedbackAdmin(String feedbackAdmin) {
        this.feedbackAdmin = feedbackAdmin;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
