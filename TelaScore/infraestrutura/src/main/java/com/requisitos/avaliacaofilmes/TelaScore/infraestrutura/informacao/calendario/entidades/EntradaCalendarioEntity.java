package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.calendario.entidades;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entrada_calendario")
public class EntradaCalendarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "calendario_id", nullable = false)
    private CalendarioEntity calendario;

    @Column(name = "filme_id", nullable = false)
    private String filmeId;

    @Column(name = "data_estreia_prevista", nullable = false)
    private LocalDate dataEstreiaPrevista;

    @Column(name = "lembrete_ativo")
    private boolean lembreteAtivo;

    public EntradaCalendarioEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CalendarioEntity getCalendario() { return calendario; }
    public void setCalendario(CalendarioEntity calendario) { this.calendario = calendario; }

    public String getFilmeId() { return filmeId; }
    public void setFilmeId(String filmeId) { this.filmeId = filmeId; }

    public LocalDate getDataEstreiaPrevista() { return dataEstreiaPrevista; }
    public void setDataEstreiaPrevista(LocalDate dataEstreiaPrevista) { this.dataEstreiaPrevista = dataEstreiaPrevista; }

    public boolean isLembreteAtivo() { return lembreteAtivo; }
    public void setLembreteAtivo(boolean lembreteAtivo) { this.lembreteAtivo = lembreteAtivo; }
}
