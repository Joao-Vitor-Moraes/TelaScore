package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.isTrue;

public class UsuarioId {
	private final int id;

	public UsuarioId(int id) {
		isTrue(id > 0, "O id deve ser positivo");

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