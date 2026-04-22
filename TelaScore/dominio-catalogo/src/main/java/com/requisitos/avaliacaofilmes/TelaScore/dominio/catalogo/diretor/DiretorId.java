package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class DiretorId {
	private final int id;

	public DiretorId(int id) {
		isTrue(id > 0, "O id do diretor deve ser positivo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DiretorId) {
			return id == ((DiretorId) obj).id;
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