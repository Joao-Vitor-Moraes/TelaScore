package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record FilmeResumo(
		int id,
		String titulo,
		int anoLancamento,
		String nomeDiretor,
		double mediaNotas
) {}