package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class FilmeId {
	private final String codigo;

	public FilmeId(String codigo) {
		notNull(codigo, "O código do filme não pode ser nulo");
		notBlank(codigo, "O código do filme não pode estar em branco");
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FilmeId) {
			return codigo.equals(((FilmeId) obj).codigo);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo);
	}

	@Override
	public String toString() {
		return codigo;
	}
}