package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;

// Importações das classes que estão no subpacote .quiz
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.TentativaQuiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Pergunta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Alternativa;

// Importação do ID de usuário que está em outro módulo
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class QuizSteps {

    private Quiz quiz;
    private QuizId quizId;
    private QuizServico quizServico;
    private Exception excecaoCapturada;
    
    // Mock manual do repositório
    private QuizRepositorio repositorio = new QuizRepositorio() {
        @Override public void salvar(Quiz quiz) {}
        @Override public Quiz obter(QuizId id) { return null; }
        @Override public void salvarTentativa(TentativaQuiz tentativa) {}
        @Override public List<TentativaQuiz> buscarTentativasPorUsuario(UsuarioId usuarioId) { return null; }
    };

    @Dado("que eu estou criando um quiz com o título {string}")
    public void criar_quiz_valido(String titulo) {
        quizId = new QuizId(1);
        quiz = new Quiz(quizId, titulo, "Descrição padrão do quiz");
        quizServico = new QuizServico(repositorio);
        excecaoCapturada = null;
    }

    @Dado("que este quiz possui uma pergunta válida")
    public void quiz_possui_pergunta_valida() {
        List<Alternativa> alternativas = Arrays.asList(
            new Alternativa("Alternativa Correta", true),
            new Alternativa("Alternativa Errada", false)
        );
        Pergunta pergunta = new Pergunta("Quanto é 2 + 2?", alternativas);
        quiz.adicionarPergunta(pergunta);
    }

    @Quando("eu tento finalizar a criação do quiz")
    public void finalizar_criar_quiz() {
        try {
            quizServico.criarQuiz(quiz);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o quiz deve ser salvo com sucesso")
    public void quiz_salvo_com_sucesso() {
        assertNull(excecaoCapturada, "Não deveria ter lançado exceção");
    }

    @Então("o sistema deve impedir a criação informando que o quiz não tem perguntas")
    public void impedir_quiz_sem_perguntas() {
        assertNotNull(excecaoCapturada);
        assertEquals("O quiz deve ter pelo menos uma pergunta para ser disponibilizado", excecaoCapturada.getMessage());
    }
}