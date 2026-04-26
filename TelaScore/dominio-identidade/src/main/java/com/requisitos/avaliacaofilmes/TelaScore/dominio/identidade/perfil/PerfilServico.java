package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil;

import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class PerfilServico {
	private final PerfilRepositorio repositorio;

	public PerfilServico(PerfilRepositorio repositorio) {
		notNull(repositorio, "O repositório de perfis não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void salvar(Perfil perfil) {
		notNull(perfil, "O perfil não pode ser nulo");
		repositorio.salvar(perfil);
	}

	public void removerPorUsuario(UsuarioId usuarioId) {
		notNull(usuarioId, "O id do usuário não pode ser nulo");
		repositorio.removerPorUsuario(usuarioId);
	}

	public Perfil editar(PerfilId perfilId, UsuarioId usuarioId, String novoApelido, String novaBiografia,
			String novoAvatarUrl) {
		notNull(perfilId, "O id do perfil não pode ser nulo");
		notNull(usuarioId, "O id do usuário não pode ser nulo");

		Perfil perfil = repositorio.obter(perfilId);

		if (perfil == null) {
			throw new IllegalArgumentException("O perfil informado não existe");
		}

		if (!perfil.getUsuarioId().equals(usuarioId)) {
			throw new IllegalStateException("O perfil não pertence ao usuário");
		}

		if (novoApelido != null && !novoApelido.isBlank()) {
			perfil.setApelido(new Apelido(novoApelido));
		}

		perfil.setBiografia(novaBiografia);
		perfil.setAvatarUrl(novoAvatarUrl);

		repositorio.salvar(perfil);

		return perfil;
	}

	public Perfil obterPorUsuario(UsuarioId usuarioId) {
		notNull(usuarioId, "O id do usuário não pode ser nulo");
		return repositorio.obterPorUsuario(usuarioId);
	}

}
