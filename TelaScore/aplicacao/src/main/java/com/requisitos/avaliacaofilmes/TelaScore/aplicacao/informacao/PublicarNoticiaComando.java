package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao;

public record PublicarNoticiaComando(
		int autorId,
		String titulo,
		String conteudo
) {}