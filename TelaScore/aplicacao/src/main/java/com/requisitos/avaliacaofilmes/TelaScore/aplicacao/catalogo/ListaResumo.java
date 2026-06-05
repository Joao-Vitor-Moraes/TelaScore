package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record ListaResumo(
		int id,
		int donoId,
		String nome,
		String descricao,
		boolean rankeada,
		String tipo,
		String visibilidade,
		boolean colaborativa,
		int quantidadeTotalDeFilmes
) {}