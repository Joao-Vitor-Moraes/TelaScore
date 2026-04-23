package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.dto;

public record PublicarNoticiaComando(
		int autorId,
		String titulo,
		String conteudo
) {}