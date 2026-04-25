package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz;

import java.util.List;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Pergunta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Alternativa;

public class CriarQuizCasoDeUso {

    private final QuizRepositorio quizRepositorio;
    private final QuizServico quizServico; // Opcional: validar o quiz antes de salvar
    private final GeradorId geradorId;

    public CriarQuizCasoDeUso(QuizRepositorio quizRepositorio, QuizServico quizServico, GeradorId geradorId) {
        this.quizRepositorio = quizRepositorio;
        this.quizServico = quizServico;
        this.geradorId = geradorId;
    }

    public void executar(CriarQuizComando comando) {
        // 1. Pede um novo ID para o Quiz (Siga o padrão do GeradorId)
        // Se ainda não existir o método gerarProximoIdQuiz, você precisará criá-lo na interface GeradorId
        QuizId novoQuizId = new QuizId(geradorId.gerarProximoIdQuiz());

        // 2. Cria o objeto de Domínio Quiz
        Quiz quiz = new Quiz(novoQuizId, comando.titulo(), comando.descricao());

        // 3. Mapeia as perguntas e alternativas que vieram do comando
        for (var pInfo : comando.perguntas()) {
            List<Alternativa> alternativas = pInfo.alternativas().stream()
                .map(a -> new Alternativa(a.texto(), a.correta()))
                .collect(Collectors.toList());
            
            Pergunta pergunta = new Pergunta(pInfo.enunciado(), alternativas);
            quiz.adicionarPergunta(pergunta);
        }

        // 4. Valida se o quiz está pronto (regra: deve ter perguntas)
        quiz.validarQuizPronto();
        
        // 5. Salva através do repositório (definido no domínio, implementado na infra)
        quizRepositorio.salvar(quiz);
    }
}