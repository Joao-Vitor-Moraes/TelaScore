package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class EventoId {
	private final int id;

	public EventoId(int id) {
		isTrue(id > 0, "O id do evento deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof EventoId) {
			return id == ((EventoId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(id); }
}