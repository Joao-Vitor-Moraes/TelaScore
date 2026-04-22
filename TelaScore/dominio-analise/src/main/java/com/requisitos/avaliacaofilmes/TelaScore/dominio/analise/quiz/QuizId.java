package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class QuizId {
	private final int id;

	public QuizId(int id) {
		isTrue(id > 0, "O id do quiz deve ser positivo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof QuizId) {
			return id == ((QuizId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}