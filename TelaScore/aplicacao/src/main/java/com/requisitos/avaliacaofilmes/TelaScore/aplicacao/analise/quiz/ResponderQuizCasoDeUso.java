package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz;

//import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.TentativaQuiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Pergunta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ResponderQuizCasoDeUso {

    private final QuizRepositorio quizRepositorio;

    public ResponderQuizCasoDeUso(QuizRepositorio quizRepositorio) {
        this.quizRepositorio = quizRepositorio;
    }

    public void executar(ResponderQuizComando comando) {
        // 1. Busca o quiz original no repositório
        QuizId quizId = new QuizId(comando.quizId());
        Quiz quiz = quizRepositorio.obter(quizId);
        
        if (quiz == null) {
            throw new IllegalArgumentException("Quiz não encontrado com o ID fornecido.");
        }

        int totalPerguntas = quiz.getTotalPerguntas();
        int contadorAcertos = 0;

        // 2. Compara as respostas do comando com o gabarito das perguntas do quiz
        for (Pergunta pergunta : quiz.getPerguntas()) {
            // Obtém o texto da resposta que o usuário enviou para esta pergunta específica
            String respostaUsuario = comando.respostas().get(pergunta.getEnunciado());

            if (respostaUsuario != null) {
                // Verifica se a alternativa escolhida pelo texto é a correta
                boolean acertou = pergunta.getAlternativas().stream()
                    .anyMatch(alt -> alt.getTexto().equals(respostaUsuario) && alt.isCorreta());

                if (acertou) {
                    contadorAcertos++;
                }
            }
        }

        // 3. Cria a Entidade de Domínio TentativaQuiz com os parâmetros exatos do seu construtor
        UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
        
        TentativaQuiz tentativa = new TentativaQuiz(
            quizId, 
            usuarioId, 
            contadorAcertos, 
            totalPerguntas
        );

        // 4. Salva a tentativa através do repositório
        quizRepositorio.salvarTentativa(tentativa);
    }
}