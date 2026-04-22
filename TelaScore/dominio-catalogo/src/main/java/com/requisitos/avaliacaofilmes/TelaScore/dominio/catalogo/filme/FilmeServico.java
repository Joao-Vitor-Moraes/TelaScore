package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme;

import static org.apache.commons.lang3.Validate.notNull;

public class FilmeServico {
	private final FilmeRepositorio filmeRepositorio;

	public FilmeServico(FilmeRepositorio filmeRepositorio) {
		notNull(filmeRepositorio, "O repositório de filmes não pode ser nulo");
		this.filmeRepositorio = filmeRepositorio;
	}

	public void salvar(Filme filme) {
		notNull(filme, "O filme não pode ser nulo");
		filmeRepositorio.salvar(filme);
	}
}