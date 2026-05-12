package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record SolicitarAjustesFilmeComando(
		int solicitacaoId,
		int avaliadorId,
		String feedback
) {}
