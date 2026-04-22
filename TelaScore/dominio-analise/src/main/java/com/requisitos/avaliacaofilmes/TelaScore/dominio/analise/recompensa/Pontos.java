package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa;

import java.util.Objects;

public class Pontos {
	private final Integer quantidade;

	public Pontos(Integer quantidade) {
		Objects.requireNonNull(quantidade, "A quantidade de pontos não pode ser nula");
		if (quantidade <= 0) {
			throw new IllegalArgumentException("A quantidade de pontos ganhos deve ser maior que zero");
		}
		this.quantidade = quantidade;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	@Override
	public String toString() {
		return quantidade + " pts";
	}
}