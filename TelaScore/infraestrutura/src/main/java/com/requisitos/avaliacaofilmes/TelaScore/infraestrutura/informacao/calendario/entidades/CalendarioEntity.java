package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.calendario.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calendario")
public class CalendarioEntity {

    @Id
    private Integer id;

    @Column(name = "usuario_id", nullable = false, unique = true)
    private Integer usuarioId;

    @OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntradaCalendarioEntity> entradas = new ArrayList<>();

    public CalendarioEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public List<EntradaCalendarioEntity> getEntradas() { return entradas; }
    public void setEntradas(List<EntradaCalendarioEntity> entradas) { this.entradas = entradas; }
}
