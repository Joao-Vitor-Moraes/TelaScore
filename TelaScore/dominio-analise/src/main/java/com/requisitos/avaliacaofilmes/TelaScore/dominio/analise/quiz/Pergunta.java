package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pergunta {
	private String enunciado;
	private List<Alternativa> alternativas = new ArrayList<>();

	public Pergunta(String enunciado, List<Alternativa> alternativas) {
		notNull(enunciado, "O enunciado não pode ser nulo");
		notBlank(enunciado, "O enunciado não pode estar em branco");
		notNull(alternativas, "A lista de alternativas não pode ser nula");
		isTrue(alternativas.size() >= 2, "Uma pergunta deve ter pelo menos 2 alternativas");
		
		// Regra: Tem de haver exatamente UMA alternativa correta (ou pelo menos uma)
		long corretas = alternativas.stream().filter(Alternativa::isCorreta).count();
		isTrue(corretas == 1, "A pergunta deve ter exatamente uma alternativa correta");

		this.enunciado = enunciado;
		this.alternativas.addAll(alternativas);
	}

	public String getEnunciado() { return enunciado; }

	public List<Alternativa> getAlternativas() {
		return Collections.unmodifiableList(alternativas);
	}
}