package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.filme.entidades;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "filme")
public class FilmeEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "ano_lancamento", nullable = false)
    private Integer anoLancamento;

    @Column(name = "data_estreia")
    private LocalDate dataEstreia;

    @Column(name = "imagem_url")
    private String imagemUrl;

    // Diretores são IDs simples — armazenados em tabela auxiliar
    @ElementCollection
    @CollectionTable(name = "filme_diretor", joinColumns = @JoinColumn(name = "filme_id"))
    @Column(name = "diretor_id", nullable = false)
    private List<Integer> diretores = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "filme_genero", joinColumns = @JoinColumn(name = "filme_id"))
    @Column(name = "genero", nullable = false)
    private List<String> generos = new ArrayList<>();

    public FilmeEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }

    public Integer getAnoLancamento() { return anoLancamento; }
    public void setAnoLancamento(Integer anoLancamento) { this.anoLancamento = anoLancamento; }

    public LocalDate getDataEstreia() { return dataEstreia; }
    public void setDataEstreia(LocalDate dataEstreia) { this.dataEstreia = dataEstreia; }

    public List<Integer> getDiretores() { return diretores; }
    public void setDiretores(List<Integer> diretores) { this.diretores = diretores; }

    public List<String> getGeneros() { return generos; }
    public void setGeneros(List<String> generos) { this.generos = generos; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
}
