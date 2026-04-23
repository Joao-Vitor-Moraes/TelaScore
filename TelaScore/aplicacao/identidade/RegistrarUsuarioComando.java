package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.dto;

public record RegistrarUsuarioComando(
		String nome,
		String email,
		String senha,
		String apelido
) {}