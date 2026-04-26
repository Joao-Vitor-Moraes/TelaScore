package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social;

public record EnviarMensagemComando(
    int remetenteId,
    int destinatarioId,
    String texto
) {}
