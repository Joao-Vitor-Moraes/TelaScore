package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record AtualizarAvaliacaoComando(
        int avaliacaoId,
        Integer valorNota,
        String resenha    
) {}