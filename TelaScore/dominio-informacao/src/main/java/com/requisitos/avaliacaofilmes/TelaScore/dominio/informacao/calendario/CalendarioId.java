package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class CalendarioId {
	private final int id;

	public CalendarioId(int id) {
		isTrue(id > 0, "O id do calendário deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CalendarioId) {
			return id == ((CalendarioId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(id); }
}