package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record EditarListaComando(
        int listaId,
        int usuarioId,
        String nome,
        String descricao,
        String visibilidade,
        boolean rankeada
) {}
