package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record CriarListaComando(
		int criadorId,
		String nome,
		String descricao,
		boolean rankeada,
		boolean colaborativa,
		String visibilidade,
		String tipo
) {}