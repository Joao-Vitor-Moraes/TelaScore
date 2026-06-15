package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

public record CadastrarUsuarioComando(
    String nome,
    String email,
    String senha,
    String apelido,
    String biografia,
    String avatarUrl
) {}
