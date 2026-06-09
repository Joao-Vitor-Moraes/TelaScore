package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record CadastrarFilmeComando(
		String titulo,
		String sinopse,
		int anoLancamento,
		int diretorId,
		String imagemUrl
) {}