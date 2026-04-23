package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

public record AtualizarFilmeComando(
        String filmeId,
        String titulo,       
        String sinopse,      
        Integer anoLancamento
) {}
