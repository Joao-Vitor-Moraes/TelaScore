package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;

public class Email {
	private final String endereco;

	public Email(String endereco) {
		notBlank(endereco, "O e-mail não pode estar em branco");
		isTrue(endereco.matches("^[A-Za-z0-9+_.-]+@(.+)$"), "O formato do e-mail é inválido");

		this.endereco = endereco;
	}

	public String getEndereco() {
		return endereco;
	}

	@Override
	public String toString() {
		return endereco;
	}
}