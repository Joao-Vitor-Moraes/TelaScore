package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import java.util.Objects;

public class UsuarioId {
	private final int id;

	public UsuarioId(int id) {
		if (id <= 0) {
			throw new IllegalArgumentException("O id deve ser positivo");
		}

		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof UsuarioId) {
			var usuarioId = (UsuarioId) obj;
			return id == usuarioId.id;
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