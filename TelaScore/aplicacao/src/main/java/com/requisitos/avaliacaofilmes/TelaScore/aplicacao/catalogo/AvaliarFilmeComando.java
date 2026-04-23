package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record AvaliarFilmeComando(
		int filmeId,
		int usuarioId,
		int valorNota,
		String comentario
) {}