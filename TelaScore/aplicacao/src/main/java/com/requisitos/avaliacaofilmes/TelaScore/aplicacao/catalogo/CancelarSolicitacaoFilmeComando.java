package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record CancelarSolicitacaoFilmeComando(
		int solicitacaoId,
		int usuarioId
) {}
