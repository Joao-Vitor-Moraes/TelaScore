package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa;

import java.util.Objects;

public class RegistroPontuacaoId {
	private final int id;

	public RegistroPontuacaoId(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("O id do registo de pontuação deve ser positivo");
		}
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof RegistroPontuacaoId) {
			return id == ((RegistroPontuacaoId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}