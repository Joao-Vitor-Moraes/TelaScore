package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.util.List;

public record CadastrarFilmeComando(
		String titulo,
		String sinopse,
		int anoLancamento,
		int diretorId,
		String imagemUrl,
		List<String> generos
) {}
