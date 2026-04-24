package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record SolicitarFilmeComando(
		int solicitanteId,
		String tituloSugerido,
		String justificativa
) {}
