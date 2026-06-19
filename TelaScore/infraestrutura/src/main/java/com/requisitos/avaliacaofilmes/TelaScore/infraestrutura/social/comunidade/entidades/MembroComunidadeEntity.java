package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.comunidade.entidades;

import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.identidade.usuario.entidades.UsuarioEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "membro_comunidade")
public class MembroComunidadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "comunidade_id", nullable = false)
    private Integer comunidadeId;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Column(nullable = false)
    private String papel;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getComunidadeId() { return comunidadeId; }
    public void setComunidadeId(Integer comunidadeId) { this.comunidadeId = comunidadeId; }
    public UsuarioEntity getUsuario() { return usuario; }
    public void setUsuario(UsuarioEntity usuario) { this.usuario = usuario; }
    public String getPapel() { return papel; }
    public void setPapel(String papel) { this.papel = papel; }
}