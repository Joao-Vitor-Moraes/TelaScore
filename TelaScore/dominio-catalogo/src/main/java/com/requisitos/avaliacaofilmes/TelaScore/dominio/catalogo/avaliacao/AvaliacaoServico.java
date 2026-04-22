package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao;

import static org.apache.commons.lang3.Validate.notNull;

public class AvaliacaoServico {
	private final AvaliacaoRepositorio avaliacaoRepositorio;

	public AvaliacaoServico(AvaliacaoRepositorio avaliacaoRepositorio) {
		notNull(avaliacaoRepositorio, "O repositório de avaliações não pode ser nulo");
		this.avaliacaoRepositorio = avaliacaoRepositorio;
	}

	public void registrarAvaliacao(Avaliacao avaliacao) {
		notNull(avaliacao, "A avaliação não pode ser nula");
		
		avaliacaoRepositorio.salvar(avaliacao);
	}
}