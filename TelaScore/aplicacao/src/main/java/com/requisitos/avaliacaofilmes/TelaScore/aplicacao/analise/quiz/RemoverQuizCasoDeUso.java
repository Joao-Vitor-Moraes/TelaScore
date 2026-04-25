package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz;

//-----={Todos os pacotes quiz}=----//

//import java.util.List;
//import java.util.stream.Collectors;

//import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
//import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
//import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizServico;
//import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Pergunta;
//import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Alternativa;


public class RemoverQuizCasoDeUso {
    private final QuizRepositorio repositorio;

    public RemoverQuizCasoDeUso(QuizRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(RemoverQuizComando comando) {
        repositorio.remover(new QuizId(comando.quizId()));
    }
}