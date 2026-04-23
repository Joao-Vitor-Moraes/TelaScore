package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise;

public record RegistrarTentativaQuizComando(
		int quizId,
		int usuarioId,
		int quantidadeAcertos
) {}