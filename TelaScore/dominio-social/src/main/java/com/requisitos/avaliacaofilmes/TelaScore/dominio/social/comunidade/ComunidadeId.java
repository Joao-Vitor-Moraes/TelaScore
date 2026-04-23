package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class ComunidadeId {
	private final int id;

	public ComunidadeId(int id) {
		isTrue(id > 0, "O id da comunidade deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ComunidadeId) {
			return id == ((ComunidadeId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(id); }
}