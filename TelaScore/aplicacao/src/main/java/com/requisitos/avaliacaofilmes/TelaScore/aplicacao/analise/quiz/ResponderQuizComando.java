package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz;

import java.util.Map;

public record ResponderQuizComando(
    int usuarioId,
    int quizId,
    // Mapa onde a chave é o enunciado da pergunta e o valor é o texto da alternativa escolhida
    Map<String, String> respostas 
) {}