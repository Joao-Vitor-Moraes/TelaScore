package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.util.List;

public record ListaResumo(
		int id,
		int donoId,
		String nome,
		String descricao,
		boolean rankeada,
		String tipo,
		String visibilidade,
		boolean colaborativa,
		int quantidadeTotalDeFilmes,
		List<Integer> colaboradores
) {}