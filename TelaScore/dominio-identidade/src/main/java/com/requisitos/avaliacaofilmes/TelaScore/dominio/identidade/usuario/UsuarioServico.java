package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.List;

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

	public Usuario obter(UsuarioId id) {
		notNull(id, "O ID do usuário não pode ser nulo");

		return repositorio.obter(id);
	}

	public Usuario obterPorEmail(Email email) {
		notNull(email, "O e-mail não pode ser nulo");

		return repositorio.obterPorEmail(email);
	}

	public void remover(UsuarioId id) {
		notNull(id, "O ID do usuário não pode ser nulo");

		Usuario usuario = repositorio.obter(id);

		if (usuario == null) {
			throw new IllegalArgumentException("O usuário informado não existe");
		}

		repositorio.remover(id);
	}

	public List<Usuario> listarTodos() {
		return repositorio.listarTodos();
	}
}
