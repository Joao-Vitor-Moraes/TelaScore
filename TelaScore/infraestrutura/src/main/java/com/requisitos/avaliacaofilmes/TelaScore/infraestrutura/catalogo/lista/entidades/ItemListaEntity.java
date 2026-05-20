package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista.entidades;

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
@Table(name = "item_lista")
public class ItemListaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lista_id", nullable = false)
    private ListaEntity lista;

    @Column(name = "filme_id", nullable = false)
    private String filmeId;

    private Integer posicao;

    @Column(name = "data_adicao")
    private LocalDate dataAdicao;

    public ItemListaEntity() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ListaEntity getLista() {
        return lista;
    }

    public void setLista(ListaEntity lista) {
        this.lista = lista;
    }

    public String getFilmeId() {
        return filmeId;
    }

    public void setFilmeId(String filmeId) {
        this.filmeId = filmeId;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }

    public LocalDate getDataAdicao() {
        return dataAdicao;
    }

    public void setDataAdicao(LocalDate dataAdicao) {
        this.dataAdicao = dataAdicao;
    }
}
