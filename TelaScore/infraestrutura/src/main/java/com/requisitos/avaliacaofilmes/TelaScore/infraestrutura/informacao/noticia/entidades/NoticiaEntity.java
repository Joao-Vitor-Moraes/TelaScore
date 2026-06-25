package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.noticia.entidades;

import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.identidade.usuario.entidades.UsuarioEntity;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.filme.entidades.FilmeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "noticia")
public class NoticiaEntity {

    @Id
    private int id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private UsuarioEntity autor;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDateTime dataPublicacao;

    @Column(nullable = false)
    private String categoria;

    @ManyToOne
    @JoinColumn(name = "filme_id")
    private FilmeEntity filme;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public UsuarioEntity getAutor() { return autor; }
    public void setAutor(UsuarioEntity autor) { this.autor = autor; }

    public LocalDateTime getDataPublicacao() { return dataPublicacao; }
    public void setDataPublicacao(LocalDateTime dataPublicacao) { this.dataPublicacao = dataPublicacao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public FilmeEntity getFilme() { return filme; }
    public void setFilme(FilmeEntity filme) { this.filme = filme; }
}
