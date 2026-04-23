package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.dto;

public record AvaliarFilmeComando(
		int filmeId,
		int usuarioId,
		int valorNota,
		String comentario
) {}