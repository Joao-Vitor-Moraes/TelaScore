package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class TentativaQuiz {
	private final QuizId quizId;
	private final UsuarioId usuarioId;
	
	private final int acertos;
	private final int totalPerguntas;
	private final LocalDateTime dataTentativa;

	public TentativaQuiz(QuizId quizId, UsuarioId usuarioId, int acertos, int totalPerguntas) {
		notNull(quizId, "O id do quiz não pode ser nulo");
		notNull(usuarioId, "O id do utilizador não pode ser nulo");
		isTrue(acertos >= 0, "O número de acertos não pode ser negativo");
		isTrue(totalPerguntas > 0, "O total de perguntas deve ser maior que zero");
		isTrue(acertos <= totalPerguntas, "O número de acertos não pode ser maior que o total de perguntas");
		
		this.quizId = quizId;
		this.usuarioId = usuarioId;
		this.acertos = acertos;
		this.totalPerguntas = totalPerguntas;
		this.dataTentativa = LocalDateTime.now();
	}

	public QuizId getQuizId() { return quizId; }
	public UsuarioId getUsuarioId() { return usuarioId; }
	public int getAcertos() { return acertos; }
	public int getTotalPerguntas() { return totalPerguntas; }
	public LocalDateTime getDataTentativa() { return dataTentativa; }
	
	public boolean foiAprovado() {
		return ((double) acertos / totalPerguntas) >= 0.5;
	}
	
	public boolean naoFoiAprovado() {
		return !foiAprovado();
	}
}