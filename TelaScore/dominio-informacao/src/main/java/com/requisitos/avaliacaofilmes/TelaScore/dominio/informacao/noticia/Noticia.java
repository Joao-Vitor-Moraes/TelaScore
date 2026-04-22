package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Noticia {
	private final NoticiaId id;
	private final UsuarioId autorId;
	
	private String titulo;
	private String conteudo;
	private final LocalDateTime dataPublicacao;

	public Noticia(NoticiaId id, UsuarioId autorId, String titulo, String conteudo) {
		notNull(id, "O id da notícia não pode ser nulo");
		notNull(autorId, "O id do autor não pode ser nulo");
		
		this.id = id;
		this.autorId = autorId;
		this.dataPublicacao = LocalDateTime.now();
		
		setTitulo(titulo);
		setConteudo(conteudo);
	}

	public NoticiaId getId() { return id; }
	public UsuarioId getAutorId() { return autorId; }
	public LocalDateTime getDataPublicacao() { return dataPublicacao; }

	public void setTitulo(String titulo) {
		notNull(titulo, "O título da notícia não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");
		this.titulo = titulo;
	}
	public String getTitulo() { return titulo; }

	public void setConteudo(String conteudo) {
		notNull(conteudo, "O conteúdo da notícia não pode ser nulo");
		notBlank(conteudo, "O conteúdo não pode estar em branco");
		this.conteudo = conteudo;
	}
	public String getConteudo() { return conteudo; }
}