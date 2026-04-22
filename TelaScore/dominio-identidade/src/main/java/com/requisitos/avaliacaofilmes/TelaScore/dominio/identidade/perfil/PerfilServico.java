package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil;

import static org.apache.commons.lang3.Validate.notNull;

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
}