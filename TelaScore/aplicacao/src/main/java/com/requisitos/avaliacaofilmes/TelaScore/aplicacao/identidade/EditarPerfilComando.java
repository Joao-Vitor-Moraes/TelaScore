package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

public record EditarPerfilComando(
    int perfilId,
    int usuarioId,
    String apelido,
    String biografia,
    String avatarUrl
) {}
