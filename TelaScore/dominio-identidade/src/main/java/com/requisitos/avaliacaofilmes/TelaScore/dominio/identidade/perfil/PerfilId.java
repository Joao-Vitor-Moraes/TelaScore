package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class PerfilId {
	private final int id;

	public PerfilId(int id) {
		isTrue(id > 0, "O id do perfil deve ser positivo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof PerfilId) {
			return id == ((PerfilId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}