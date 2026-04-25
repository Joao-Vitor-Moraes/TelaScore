package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface QuizRepositorio {
	void salvar(Quiz quiz);
	Quiz obter(QuizId id);
	
	void salvarTentativa(TentativaQuiz tentativa);
	List<TentativaQuiz> buscarTentativasPorUsuario(UsuarioId usuarioId);
	void remover(QuizId id);
}