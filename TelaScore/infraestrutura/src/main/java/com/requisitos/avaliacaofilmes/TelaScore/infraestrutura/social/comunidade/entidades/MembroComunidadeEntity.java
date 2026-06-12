package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "membro_comunidade")
public class MembroComunidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comunidade_id", nullable = false)
    private Integer comunidadeId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(nullable = false)
    private String papel;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getComunidadeId() { return comunidadeId; }
    public void setComunidadeId(Integer comunidadeId) { this.comunidadeId = comunidadeId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }
}