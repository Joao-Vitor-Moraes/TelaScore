package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.notBlank;

public class Apelido {
	private final String valor;

	public Apelido(String valor) {
		notBlank(valor, "O apelido não pode estar em branco");
		inclusiveBetween(3, 20, valor.length(), "O apelido deve ter entre 3 e 20 caracteres");
		
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public String toString() {
		return valor;
	}
}