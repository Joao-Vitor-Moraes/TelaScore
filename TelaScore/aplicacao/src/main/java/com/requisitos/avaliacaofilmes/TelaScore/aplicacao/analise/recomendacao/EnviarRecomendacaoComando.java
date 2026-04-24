package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

public record EnviarRecomendacaoComando(
    int remetenteId,
    int destinatarioId,
    String filmeId,
    String mensagem,
    double pesoAcerto 
) {}