package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

public record EditarUsuarioComando(
    int usuarioId,
    String nome,
    String email,
    String papel,
    String apelido,
    String biografia,
    String avatarUrl
) {}
