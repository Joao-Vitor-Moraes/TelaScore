package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.util.List;

public record FilmeResumo(
		int id,
		String titulo,
		String sinopse,
		int anoLancamento,
		String nomeDiretor,
		double mediaNotas,
		String imagemUrl,
		String dataEstreia,
		List<String> generos
) {}
