package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notificacao_meta")
public class NotificacaoMetaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "meta_id")
    private Integer metaId;

    @Column(name = "titulo_meta")
    private String tituloMeta;

    @Column(name = "pontos_ganhos", nullable = false)
    private Integer pontosGanhos;

    @Column
    private String tipo = "META";

    @Column
    private String titulo = "Meta concluida";

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Column
    private String rota;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    private Boolean lida = false;

    public Integer getId() { return id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getMetaId() { return metaId; }
    public void setMetaId(Integer metaId) { this.metaId = metaId; }
    public String getTituloMeta() { return tituloMeta; }
    public void setTituloMeta(String tituloMeta) { this.tituloMeta = tituloMeta; }
    public Integer getPontosGanhos() { return pontosGanhos; }
    public void setPontosGanhos(Integer pontosGanhos) { this.pontosGanhos = pontosGanhos; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public String getRota() { return rota; }
    public void setRota(String rota) { this.rota = rota; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public Boolean getLida() { return lida; }
    public void setLida(Boolean lida) { this.lida = lida; }
}
