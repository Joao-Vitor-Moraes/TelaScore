package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class RecomendacaoId {
	private final int id;

	public RecomendacaoId(int id) {
		isTrue(id > 0, "O id da recomendação deve ser positivo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof RecomendacaoId) {
			return id == ((RecomendacaoId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}