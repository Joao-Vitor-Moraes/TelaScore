package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.dto;

public record RegistrarTentativaQuizComando(
		int quizId,
		int usuarioId,
		int quantidadeAcertos
) {}