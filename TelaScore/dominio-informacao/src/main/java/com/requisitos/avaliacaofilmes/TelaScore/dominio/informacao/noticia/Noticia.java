package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Noticia {
    private final NoticiaId id;
    private final UsuarioId autorId;
    private final String autorApelido;

    private String titulo;
    private String conteudo;
    private final LocalDateTime dataPublicacao;
    private CategoriaNoticia categoria;
    private final String filmeId;
    private final String filmeTitulo;
    private final String filmeImagemUrl;

    public Noticia(NoticiaId id, UsuarioId autorId, String titulo, String conteudo, CategoriaNoticia categoria) {
        this(id, autorId, titulo, conteudo, categoria, null);
    }

    public Noticia(NoticiaId id, UsuarioId autorId, String titulo, String conteudo,
                   CategoriaNoticia categoria, String filmeId) {
        notNull(id, "O id da noticia nao pode ser nulo");
        notNull(autorId, "O id do autor nao pode ser nulo");

        this.id = id;
        this.autorId = autorId;
        this.autorApelido = null;
        this.dataPublicacao = LocalDateTime.now();
        this.categoria = categoria;
        this.filmeId = normalizarFilmeId(filmeId);
        this.filmeTitulo = null;
        this.filmeImagemUrl = null;

        setTitulo(titulo);
        setConteudo(conteudo);
    }

    public Noticia(NoticiaId id, UsuarioId autorId, String autorApelido, String titulo, String conteudo,
                   LocalDateTime dataPublicacao, CategoriaNoticia categoria) {
        this(id, autorId, autorApelido, titulo, conteudo, dataPublicacao, categoria, null, null, null);
    }

    public Noticia(NoticiaId id, UsuarioId autorId, String autorApelido, String titulo, String conteudo,
                   LocalDateTime dataPublicacao, CategoriaNoticia categoria, String filmeId,
                   String filmeTitulo, String filmeImagemUrl) {
        notNull(id, "O id da noticia nao pode ser nulo");
        notNull(autorId, "O id do autor nao pode ser nulo");
        notNull(dataPublicacao, "A data de publicacao nao pode ser nula");

        this.id = id;
        this.autorId = autorId;
        this.autorApelido = autorApelido;
        this.dataPublicacao = dataPublicacao;
        this.categoria = categoria;
        this.filmeId = normalizarFilmeId(filmeId);
        this.filmeTitulo = filmeTitulo;
        this.filmeImagemUrl = filmeImagemUrl;

        setTitulo(titulo);
        setConteudo(conteudo);
    }

    public NoticiaId getId() { return id; }
    public UsuarioId getAutorId() { return autorId; }
    public String getAutorApelido() { return autorApelido; }
    public LocalDateTime getDataPublicacao() { return dataPublicacao; }
    public String getFilmeId() { return filmeId; }
    public String getFilmeTitulo() { return filmeTitulo; }
    public String getFilmeImagemUrl() { return filmeImagemUrl; }

    public void setTitulo(String titulo) {
        notNull(titulo, "O titulo da noticia nao pode ser nulo");
        notBlank(titulo, "O titulo nao pode estar em branco");
        isTrue(titulo.trim().length() >= 5, "Titulo muito curto");
        this.titulo = titulo;
    }
    public String getTitulo() { return titulo; }

    public void setConteudo(String conteudo) {
        notNull(conteudo, "O conteudo da noticia nao pode ser nulo");
        notBlank(conteudo, "O conteudo nao pode estar em branco");
        this.conteudo = conteudo;
    }
    public String getConteudo() { return conteudo; }

    public CategoriaNoticia getCategoria() { return categoria; }
    public void setCategoria(CategoriaNoticia categoria) {
        notNull(categoria, "A categoria nao pode ser nula");
        this.categoria = categoria;
    }

    private String normalizarFilmeId(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
