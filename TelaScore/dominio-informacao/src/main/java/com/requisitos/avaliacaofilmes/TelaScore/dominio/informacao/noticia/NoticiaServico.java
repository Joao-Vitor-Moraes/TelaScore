package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia;

import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class NoticiaServico {
	private final NoticiaRepositorio repositorio;

	public NoticiaServico(NoticiaRepositorio repositorio) {
		notNull(repositorio, "O repositório de notícias não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void publicarNoticia(Noticia noticia) {
		notNull(noticia, "A notícia não pode ser nula");
		repositorio.salvar(noticia);
	}
	
	public void excluirNoticia(NoticiaId id) {
		notNull(id, "O id da notícia não pode ser nulo");
		repositorio.remover(id);
	}

	public List<Noticia> pesquisarNoticias(String termo, CategoriaNoticia categoria) {
		// Aqui você pode adicionar regras, como: se o termo for muito curto, ignorar, etc.
		return repositorio.buscarPorFiltros(termo, categoria);
	}
}