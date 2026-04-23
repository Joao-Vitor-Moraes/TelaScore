package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record CriarListaComando(
		int criadorId,
		String nome,
		String descricao,
		boolean rankeada
) {}