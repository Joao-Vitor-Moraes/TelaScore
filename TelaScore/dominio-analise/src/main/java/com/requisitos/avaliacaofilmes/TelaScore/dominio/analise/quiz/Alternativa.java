package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Alternativa {
	private final String texto;
	private final boolean correta;

	public Alternativa(String texto, boolean correta) {
		notNull(texto, "O texto da alternativa não pode ser nulo");
		notBlank(texto, "O texto da alternativa não pode estar em branco");
		
		this.texto = texto;
		this.correta = correta;
	}

	public String getTexto() { return texto; }
	public boolean isCorreta() { return correta; }
}