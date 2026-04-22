package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class MetaId {
	private final int id;

	public MetaId(int id) {
		isTrue(id > 0, "O id da meta deve ser positivo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof MetaId) {
			return id == ((MetaId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}