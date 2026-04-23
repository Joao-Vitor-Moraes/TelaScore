package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.TentativaQuiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class RegistrarTentativaQuizCasoDeUso {

	private final QuizRepositorio quizRepositorio;
	private final RegistroPontuacaoRepositorio pontuacaoRepositorio;
	private final GeradorId geradorId;

	public RegistrarTentativaQuizCasoDeUso(QuizRepositorio quizRepositorio, RegistroPontuacaoRepositorio pontuacaoRepositorio, GeradorId geradorId) {
		this.quizRepositorio = quizRepositorio;
		this.pontuacaoRepositorio = pontuacaoRepositorio;
		this.geradorId = geradorId;
	}

	public void executar(RegistrarTentativaQuizComando comando) {
		QuizId quizId = new QuizId(comando.quizId());
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
		
		// 1. Busca o Quiz no banco para saber o total de perguntas
		Quiz quiz = quizRepositorio.obter(quizId);
		if (quiz == null) {
			throw new IllegalArgumentException("Quiz não encontrado.");
		}
		
		// 2. Registra a tentativa
		TentativaQuiz tentativa = new TentativaQuiz(quizId, usuarioId, comando.quantidadeAcertos(), quiz.getTotalPerguntas());
		quizRepositorio.salvarTentativa(tentativa);
		
		// 3. Integração com a Gamificação: Se aprovado (acertou mais de 50%), ganha 50 pontos!
		if (tentativa.foiAprovado()) {
			RegistroPontuacaoId pontuacaoId = new RegistroPontuacaoId(geradorId.gerarProximoIdRegistroPontuacao());
			Pontos recompensa = new Pontos(50); // 50 pontos por acertar o Quiz
			
			RegistroPontuacao registro = new RegistroPontuacao(pontuacaoId, usuarioId, recompensa, AcaoPontuada.ACERTAR_QUIZ);
			pontuacaoRepositorio.salvar(registro);
		}
	}
}