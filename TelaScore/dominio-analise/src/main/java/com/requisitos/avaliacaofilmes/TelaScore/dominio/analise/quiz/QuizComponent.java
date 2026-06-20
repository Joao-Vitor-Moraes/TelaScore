package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

public interface QuizComponent {

	void iniciar();

	int calcularPontuacao(int acertos);

	boolean validarRestricao();
}