package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class QuizDecoratorTest {

	@Test
	void quizBaseCalculaPontuacaoPadrao() {
		QuizComponent quiz = new QuizBase("Quiz de testes");

		quiz.iniciar();

		assertTrue(quiz.validarRestricao());
		assertEquals(30, quiz.calcularPontuacao(3));
	}

	@Test
	void quizComRestricaoAplicaBonusDePontuacao() {
		QuizComponent quiz = new QuizComRestricao(new QuizBase("Quiz com restricao"), 15);

		quiz.iniciar();

		assertTrue(quiz.validarRestricao());
		assertEquals(36, quiz.calcularPontuacao(3));
	}

	@Test
	void quizComRestricaoRejeitaTempoLimiteInvalido() {
		QuizComponent quiz = new QuizComRestricao(new QuizBase("Quiz com restricao"), 0);

		assertFalse(quiz.validarRestricao());
	}
}