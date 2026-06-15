package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record FilmeResumo(
		int id,
		String titulo,
		String sinopse,
		int anoLancamento,
		String nomeDiretor,
		double mediaNotas,
		String imagemUrl
) {}