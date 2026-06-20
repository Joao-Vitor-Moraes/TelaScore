package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.isTrue;

public class QuizComRestricao extends QuizDecorator {

	private final int tempoLimiteMinutos;

	public QuizComRestricao(QuizComponent quizComponent, int tempoLimiteMinutos) {
		super(quizComponent);
		this.tempoLimiteMinutos = tempoLimiteMinutos;
	}

	public int getTempoLimiteMinutos() {
		return tempoLimiteMinutos;
	}

	@Override
	public int calcularPontuacao(int acertos) {
		int pontuacaoBase = super.calcularPontuacao(acertos);
		return (int) Math.round(pontuacaoBase * 1.2d);
	}

	@Override
	public boolean validarRestricao() {
		return super.validarRestricao() && tempoLimiteMinutos > 0;
	}

	@Override
	public void iniciar() {
		isTrue(tempoLimiteMinutos > 0, "O tempo limite deve ser maior que zero");
		super.iniciar();
	}
}