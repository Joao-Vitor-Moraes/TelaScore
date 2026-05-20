package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.lista.entidades;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lista")
public class ListaEntity {

    @Id
    private Integer id;

    @Column(name = "dono_id", nullable = false)
    private Integer donoId;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    private boolean ranqueada;

    @Column(nullable = false)
    private String visibilidade;

    @Column(nullable = false)
    private String tipo;

    private boolean colaborativa;

    @ElementCollection
    @CollectionTable(name = "lista_colaborador", joinColumns = @JoinColumn(name = "lista_id"))
    @Column(name = "usuario_id")
    private List<Integer> colaboradores = new ArrayList<>();

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemListaEntity> itens = new ArrayList<>();

    public ListaEntity() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDonoId() {
        return donoId;
    }

    public void setDonoId(Integer donoId) {
        this.donoId = donoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isRanqueada() {
        return ranqueada;
    }

    public void setRanqueada(boolean ranqueada) {
        this.ranqueada = ranqueada;
    }

    public String getVisibilidade() {
        return visibilidade;
    }

    public void setVisibilidade(String visibilidade) {
        this.visibilidade = visibilidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isColaborativa() {
        return colaborativa;
    }

    public void setColaborativa(boolean colaborativa) {
        this.colaborativa = colaborativa;
    }

    public List<Integer> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(List<Integer> colaboradores) {
        this.colaboradores = colaboradores;
    }

    public List<ItemListaEntity> getItens() {
        return itens;
    }

    public void setItens(List<ItemListaEntity> itens) {
        this.itens = itens;
    }
}
