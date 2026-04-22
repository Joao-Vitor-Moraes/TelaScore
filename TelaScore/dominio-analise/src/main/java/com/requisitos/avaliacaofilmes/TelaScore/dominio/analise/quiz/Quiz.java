package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Quiz {
	private final QuizId id;
	private String titulo;
	private String descricao;
	private List<Pergunta> perguntas = new ArrayList<>();

	public Quiz(QuizId id, String titulo, String descricao) {
		notNull(id, "O id do quiz não pode ser nulo");
		this.id = id;
		
		setTitulo(titulo);
		setDescricao(descricao);
	}

	public QuizId getId() { return id; }
	public String getDescricao() { return descricao; }

	public void setTitulo(String titulo) {
		notNull(titulo, "O título não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");
		this.titulo = titulo;
	}
	public String getTitulo() { return titulo; }

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void adicionarPergunta(Pergunta pergunta) {
		notNull(pergunta, "A pergunta não pode ser nula");
		this.perguntas.add(pergunta);
	}

	public List<Pergunta> getPerguntas() {
		return Collections.unmodifiableList(perguntas);
	}
	
	public int getTotalPerguntas() {
		return perguntas.size();
	}
	
	public void validarQuizPronto() {
		isTrue(!perguntas.isEmpty(), "O quiz deve ter pelo menos uma pergunta para ser disponibilizado");
	}
}