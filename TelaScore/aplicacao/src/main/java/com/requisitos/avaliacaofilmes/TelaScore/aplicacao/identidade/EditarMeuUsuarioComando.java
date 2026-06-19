package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

public record EditarMeuUsuarioComando(
    String nome,
    String apelido,
    String biografia,
    String avatarUrl
) {
}
