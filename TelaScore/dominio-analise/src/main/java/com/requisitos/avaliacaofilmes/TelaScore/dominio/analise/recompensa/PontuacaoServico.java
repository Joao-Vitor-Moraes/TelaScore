package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa;

import static org.apache.commons.lang3.Validate.notNull;

public class PontuacaoServico {
	private final RegistroPontuacaoRepositorio repositorio;

	public PontuacaoServico(RegistroPontuacaoRepositorio repositorio) {
		notNull(repositorio, "O repositório de pontuação não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void concederPontos(RegistroPontuacao registro) {
		notNull(registro, "O registo de pontuação não pode ser nulo");
		repositorio.salvar(registro);
	}
}