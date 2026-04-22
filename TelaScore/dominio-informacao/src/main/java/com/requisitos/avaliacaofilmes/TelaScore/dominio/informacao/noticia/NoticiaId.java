package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia;

import static org.apache.commons.lang3.Validate.isTrue;
import java.util.Objects;

public class NoticiaId {
	private final int id;

	public NoticiaId(int id) {
		isTrue(id > 0, "O id da notícia deve ser positivo");
		this.id = id;
	}

	public int getId() { return id; }

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof NoticiaId) {
			return id == ((NoticiaId) obj).id;
		}
		return false;
	}

	@Override
	public int hashCode() { return Objects.hash(id); }
}