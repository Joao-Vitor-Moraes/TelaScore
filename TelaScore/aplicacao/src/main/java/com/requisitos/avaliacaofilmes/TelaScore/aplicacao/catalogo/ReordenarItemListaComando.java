package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record ReordenarItemListaComando(
		int listaId,
		int usuarioId, 
		int filmeId, 
		int novaPosicao 
) {}