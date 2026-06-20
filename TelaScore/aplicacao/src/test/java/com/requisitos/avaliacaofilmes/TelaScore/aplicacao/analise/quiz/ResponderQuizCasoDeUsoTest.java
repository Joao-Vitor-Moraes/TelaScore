package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Alternativa;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Pergunta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.TentativaQuiz;

class ResponderQuizCasoDeUsoTest {

	@Test
	void executarDeveSalvarTentativaQuandoQuizExiste() {
		QuizRepositorio quizRepositorio = mock(QuizRepositorio.class);
		ResponderQuizCasoDeUso casoDeUso = new ResponderQuizCasoDeUso(quizRepositorio);

		Quiz quiz = new Quiz(new QuizId(1), "Quiz de testes", "Descricao");
		quiz.adicionarPergunta(new Pergunta(
			"Pergunta 1",
			List.of(
				new Alternativa("Alternativa A", false),
				new Alternativa("Alternativa B", true)
			)
		));

		when(quizRepositorio.obter(new QuizId(1))).thenReturn(quiz);

		casoDeUso.executar(new ResponderQuizComando(7, 1, Map.of("Pergunta 1", "Alternativa B")));

		verify(quizRepositorio).salvarTentativa(org.mockito.ArgumentMatchers.any(TentativaQuiz.class));
	}

	@Test
	void comandoDeveSerProcessadoSemAlterarQuizOriginal() {
		QuizRepositorio quizRepositorio = mock(QuizRepositorio.class);
		ResponderQuizCasoDeUso casoDeUso = new ResponderQuizCasoDeUso(quizRepositorio);

		Quiz quiz = new Quiz(new QuizId(2), "Outro quiz", "Descricao");
		quiz.adicionarPergunta(new Pergunta(
			"Pergunta 2",
			List.of(
				new Alternativa("Alternativa A", true),
				new Alternativa("Alternativa B", false)
			)
		));

		when(quizRepositorio.obter(new QuizId(2))).thenReturn(quiz);

		casoDeUso.executar(new ResponderQuizComando(8, 2, Map.of("Pergunta 2", "Alternativa A")));

		assertNotNull(quiz.getTitulo());
		assertEquals(1, quiz.getTotalPerguntas());
	}
}