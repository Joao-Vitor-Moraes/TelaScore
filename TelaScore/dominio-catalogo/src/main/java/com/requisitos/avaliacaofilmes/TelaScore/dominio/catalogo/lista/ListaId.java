package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.isTrue;

public class ListaId {
	private final int id;

	public ListaId(int id) {
		isTrue(id > 0, "O id da lista deve ser positivo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ListaId) {
			return id == ((ListaId) obj).id;
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