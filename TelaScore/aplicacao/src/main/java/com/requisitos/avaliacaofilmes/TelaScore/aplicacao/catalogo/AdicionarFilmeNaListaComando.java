package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record AdicionarFilmeNaListaComando(
		int listaId,
		int usuarioId,
		int filmeId
) {}