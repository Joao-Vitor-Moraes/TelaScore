package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class QuizBase implements QuizComponent {

	private static final int PONTOS_POR_ACERTO = 10;

	private final String titulo;

	private boolean iniciado;

	public QuizBase(String titulo) {
		notNull(titulo, "O título do quiz não pode ser nulo");
		notBlank(titulo, "O título do quiz não pode estar em branco");
		this.titulo = titulo;
	}

	public String getTitulo() {
		return titulo;
	}

	public boolean isIniciado() {
		return iniciado;
	}

	@Override
	public void iniciar() {
		this.iniciado = true;
	}

	@Override
	public int calcularPontuacao(int acertos) {
		if (acertos < 0) {
			throw new IllegalArgumentException("O número de acertos não pode ser negativo");
		}
		return acertos * PONTOS_POR_ACERTO;
	}

	@Override
	public boolean validarRestricao() {
		return true;
	}
}