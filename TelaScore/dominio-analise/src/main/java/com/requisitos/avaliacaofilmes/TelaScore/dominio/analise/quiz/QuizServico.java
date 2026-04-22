package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.notNull;

public class QuizServico {
	private final QuizRepositorio repositorio;

	public QuizServico(QuizRepositorio repositorio) {
		notNull(repositorio, "O repositório do quiz não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void criarQuiz(Quiz quiz) {
		notNull(quiz, "O quiz não pode ser nulo");
		quiz.validarQuizPronto();
		repositorio.salvar(quiz);
	}
	
	public void registrarTentativa(TentativaQuiz tentativa) {
		notNull(tentativa, "A tentativa não pode ser nula");
		repositorio.salvarTentativa(tentativa);
	}
}