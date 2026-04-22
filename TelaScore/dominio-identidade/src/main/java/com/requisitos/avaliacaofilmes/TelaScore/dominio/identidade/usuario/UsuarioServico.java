package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import static org.apache.commons.lang3.Validate.notNull;

public class UsuarioServico {
	private final UsuarioRepositorio repositorio;

	public UsuarioServico(UsuarioRepositorio repositorio) {
		notNull(repositorio, "O repositório de usuários não pode ser nulo");
		
		this.repositorio = repositorio;
	}

	public void salvar(Usuario usuario) {
		notNull(usuario, "O usuário não pode ser nulo");
		
		repositorio.salvar(usuario);
	}
}