package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class ConexaoId {
	private final int id;

	public ConexaoId(int id) {
		isTrue(id > 0, "O id da conexão deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ConexaoId) {
			return id == ((ConexaoId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(id); }
}