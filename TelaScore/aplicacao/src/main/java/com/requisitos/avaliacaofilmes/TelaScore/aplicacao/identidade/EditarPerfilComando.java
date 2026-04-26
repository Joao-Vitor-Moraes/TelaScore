package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

public record EditarPerfilComando(
    int perfilId,
    String apelido,
    String biografia,
    String avatarUrl
) {}
