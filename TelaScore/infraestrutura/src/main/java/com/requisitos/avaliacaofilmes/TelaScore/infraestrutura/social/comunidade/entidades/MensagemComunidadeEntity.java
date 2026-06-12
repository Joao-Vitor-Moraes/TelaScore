package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem_comunidade")
public class MensagemComunidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comunidade_id", nullable = false)
    private Integer comunidadeId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Column(name = "enviado_em", nullable = false)
    private LocalDateTime enviadoEm;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getComunidadeId() { return comunidadeId; }
    public void setComunidadeId(Integer comunidadeId) { this.comunidadeId = comunidadeId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public LocalDateTime getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(LocalDateTime enviadoEm) { this.enviadoEm = enviadoEm; }
}