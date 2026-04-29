package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia;

import static org.apache.commons.lang3.Validate.isTrue;

import java.util.Objects;

public class DenunciaId {
	private final int id;

	public DenunciaId(int id) {
		isTrue(id > 0, "O id da denúncia deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DenunciaId) {
			return id == ((DenunciaId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(id); }
}
