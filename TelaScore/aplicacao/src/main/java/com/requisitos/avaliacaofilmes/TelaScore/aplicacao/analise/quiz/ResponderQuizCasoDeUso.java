package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Pergunta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizBase;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizComRestricao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizComponent;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.TentativaQuiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacaoProvider;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.PontuacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ResponderQuizCasoDeUso {

    private final QuizRepositorio quizRepositorio;
    private final PontuacaoServico pontuacaoServico;
    private final EstrategiaPontuacaoProvider estrategias;

    public ResponderQuizCasoDeUso(QuizRepositorio quizRepositorio,
            PontuacaoServico pontuacaoServico,
            EstrategiaPontuacaoProvider estrategias) {
        this.quizRepositorio = quizRepositorio;
        this.pontuacaoServico = pontuacaoServico;
        this.estrategias = estrategias;
    }

    public Resultado executar(ResponderQuizComando comando) {
        QuizId quizId = new QuizId(comando.quizId());
        Quiz quiz = quizRepositorio.obter(quizId);

        if (quiz == null) {
            throw new IllegalArgumentException("Quiz nao encontrado com o ID fornecido.");
        }

        QuizComponent quizComponent = new QuizComRestricao(new QuizBase(quiz.getTitulo()), 30);
        quizComponent.iniciar();

        if (!quizComponent.validarRestricao()) {
            throw new IllegalStateException("O quiz nao atende as restricoes configuradas.");
        }

        int totalPerguntas = quiz.getTotalPerguntas();
        int contadorAcertos = 0;

        for (Pergunta pergunta : quiz.getPerguntas()) {
            String respostaUsuario = comando.respostas().get(pergunta.getEnunciado());
            if (respostaUsuario != null) {
                boolean acertou = pergunta.getAlternativas().stream()
                        .anyMatch(alt -> alt.getTexto().equals(respostaUsuario) && alt.isCorreta());
                if (acertou) contadorAcertos++;
            }
        }

        int pontuacaoFinal = quizComponent.calcularPontuacao(contadorAcertos);
        if (pontuacaoFinal < 0) {
            throw new IllegalStateException("A pontuacao calculada para o quiz e invalida.");
        }

        UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
        TentativaQuiz tentativa = new TentativaQuiz(quizId, usuarioId, contadorAcertos, totalPerguntas);
        quizRepositorio.salvarTentativa(tentativa);

        if (tentativa.foiAprovado()) {
            pontuacaoServico.concederPontos(usuarioId, AcaoPontuada.ACERTAR_QUIZ,
                    estrategias.obter(AcaoPontuada.ACERTAR_QUIZ));
        }

        return new Resultado(contadorAcertos, totalPerguntas, pontuacaoFinal, tentativa.foiAprovado());
    }

    public record Resultado(int acertos, int totalPerguntas, int pontuacao, boolean aprovado) {}
}
