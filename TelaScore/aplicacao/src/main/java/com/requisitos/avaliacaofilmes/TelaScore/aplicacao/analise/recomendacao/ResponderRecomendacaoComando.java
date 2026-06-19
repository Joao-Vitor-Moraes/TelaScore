package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

public record ResponderRecomendacaoComando(
    int recomendacaoId,
    String resposta,
    String comentario
) {}
