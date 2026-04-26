package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.notBlank;

public class Senha {
	private final String valor;

	public Senha(String valor) {
		notBlank(valor, "A senha não pode estar em branco");
		inclusiveBetween(6, 50, valor.length(), "A senha deve ter entre 6 e 50 caracteres");

		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}
}
