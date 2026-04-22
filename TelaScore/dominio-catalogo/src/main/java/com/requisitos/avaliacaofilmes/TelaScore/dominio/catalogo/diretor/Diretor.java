package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Diretor {
	private final DiretorId id;
	private String nome;

	public Diretor(DiretorId id, String nome) {
		notNull(id, "O id não pode ser nulo");
		this.id = id;

		this.nome = nome;
	}

	public DiretorId getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		notNull(nome, "O nome não pode ser nulo");
		notBlank(nome, "O nome não pode estar em branco");
		this.nome = nome;
	}

	@Override
	public String toString() {
		return nome;
	}
}