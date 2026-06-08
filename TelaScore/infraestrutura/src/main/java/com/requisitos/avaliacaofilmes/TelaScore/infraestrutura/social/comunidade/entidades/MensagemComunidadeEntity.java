package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem_comunidade")
public class MensagemComunidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "comunidade_id", nullable = false)
    private int comunidadeId;

    @Column(name = "usuario_id", nullable = false)
    private int usuarioId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @Column(name = "enviado_em", nullable = false)
    private LocalDateTime enviadoEm;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getComunidadeId() { return comunidadeId; }
    public void setComunidadeId(int comunidadeId) { this.comunidadeId = comunidadeId; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public LocalDateTime getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(LocalDateTime enviadoEm) { this.enviadoEm = enviadoEm; }
}