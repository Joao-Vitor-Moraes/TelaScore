package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class SolicitacaoId {
	private final int id;

	public SolicitacaoId(int id) {
		isTrue(id > 0, "O id da solicitação deve ser positivo");
		this.id = id;
	}

	public int getId() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof SolicitacaoId) {
			return id == ((SolicitacaoId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}