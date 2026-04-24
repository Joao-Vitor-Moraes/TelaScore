package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

public record ResponderRecomendacaoComando(
    int recomendacaoId,
    boolean aceitar
) {}