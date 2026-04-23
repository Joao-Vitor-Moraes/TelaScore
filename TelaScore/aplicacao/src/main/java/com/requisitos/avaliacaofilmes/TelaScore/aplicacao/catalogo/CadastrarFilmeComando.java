package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record CadastrarFilmeComando(
		String titulo,
		String sinopse,
		int anoLancamento,
		int diretorId // Recebemos apenas o ID numérico que veio do select/dropdown na tela
) {}