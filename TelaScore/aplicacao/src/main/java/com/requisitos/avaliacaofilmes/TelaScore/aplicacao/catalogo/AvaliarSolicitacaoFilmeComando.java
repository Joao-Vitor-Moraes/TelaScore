package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record AvaliarSolicitacaoFilmeComando(
		int solicitacaoId,
		int avaliadorId,
		boolean aprovar
) {}
