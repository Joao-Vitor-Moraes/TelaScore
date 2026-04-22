package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.notNull;

public class Nota {
	private final Integer valor;

	public Nota(Integer valor) {
		notNull(valor, "A nota não pode ser nula");
		inclusiveBetween(1, 5, valor, "A nota deve estar entre 1 e 5 estrelas");
		
		this.valor = valor;
	}

	public Integer getValor() {
		return valor;
	}

	@Override
	public String toString() {
		return valor + " Estrela(s)";
	}
}