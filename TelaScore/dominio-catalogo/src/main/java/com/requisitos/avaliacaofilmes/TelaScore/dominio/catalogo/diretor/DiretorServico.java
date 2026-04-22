package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor;

import static org.apache.commons.lang3.Validate.notNull;

public class DiretorServico {
	private final DiretorRepositorio diretorRepositorio;

	public DiretorServico(DiretorRepositorio diretorRepositorio) {
		notNull(diretorRepositorio, "O repositório de diretores não pode ser nulo");
		this.diretorRepositorio = diretorRepositorio;
	}

	public void salvar(Diretor diretor) {
		notNull(diretor, "O diretor não pode ser nulo");
		diretorRepositorio.salvar(diretor);
	}
}