package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.notNull;

public abstract class QuizDecorator implements QuizComponent {

	private final QuizComponent quizComponent;

	protected QuizDecorator(QuizComponent quizComponent) {
		notNull(quizComponent, "O componente do quiz não pode ser nulo");
		this.quizComponent = quizComponent;
	}

	@Override
	public void iniciar() {
		quizComponent.iniciar();
	}

	@Override
	public int calcularPontuacao(int acertos) {
		return quizComponent.calcularPontuacao(acertos);
	}

	@Override
	public boolean validarRestricao() {
		return quizComponent.validarRestricao();
	}

	protected QuizComponent getQuizComponent() {
		return quizComponent;
	}
}