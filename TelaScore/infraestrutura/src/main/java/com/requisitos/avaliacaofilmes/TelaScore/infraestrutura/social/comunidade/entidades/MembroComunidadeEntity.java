package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "membro_comunidade")
public class MembroComunidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "comunidade_id", nullable = false)
    private int comunidadeId;

    @Column(name = "usuario_id", nullable = false)
    private int usuarioId;

    @Column(nullable = false)
    private String papel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getComunidadeId() {
        return comunidadeId;
    }

    public void setComunidadeId(int comunidadeId) {
        this.comunidadeId = comunidadeId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }
}