package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record ListaResumo(
		int id,
		String nome,
		boolean rankeada,
		int quantidadeTotalDeFilmes
) {}