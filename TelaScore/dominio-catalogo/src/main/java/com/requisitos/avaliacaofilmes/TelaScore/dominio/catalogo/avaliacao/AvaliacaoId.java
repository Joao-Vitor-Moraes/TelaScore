package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.isTrue;

public class AvaliacaoId {
	private final int id;

	public AvaliacaoId(int id) {
		isTrue(id > 0, "O id da avaliação deve ser positivo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof AvaliacaoId) {
			return id == ((AvaliacaoId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}
}