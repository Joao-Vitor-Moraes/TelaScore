package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.dto;

public record FilmeResumo(
		int id,
		String titulo,
		int anoLancamento,
		String nomeDiretor,
		double mediaNotas
) {}