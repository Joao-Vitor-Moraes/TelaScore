package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class MensagemId {
	private final int id;

	public MensagemId(int id) {
		isTrue(id > 0, "O id da mensagem deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof MensagemId) {
			return id == ((MensagemId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(id); }
}