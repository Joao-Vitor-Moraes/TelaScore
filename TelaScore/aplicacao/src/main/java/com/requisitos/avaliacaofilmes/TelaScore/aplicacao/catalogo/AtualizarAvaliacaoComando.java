package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record AtualizarAvaliacaoComando(
        Integer avaliacaoId,
        Integer valorNota,
        String resenha    
) {}