package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record RemoverFilmeDaListaComando(
		int listaId,
		int usuarioId, 
		int filmeId
) {}