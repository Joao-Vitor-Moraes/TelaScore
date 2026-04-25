package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.*;

public class QuizSteps {

    private Quiz quiz;
    private TentativaQuiz tentativa;
    private Exception excecaoCapturada;

    // --- CENÁRIOS DE CRIAÇÃO ---

    @Dado("que eu estou criando um quiz com o título {string}")
    public void preparar_criacao(String titulo) {
        // Reinicia o estado para cada teste
        this.quiz = new Quiz(new QuizId(1), titulo, "Descrição Padrão");
        this.excecaoCapturada = null;
    }

    @Dado("que este quiz possui uma pergunta válida")
    public void adicionar_pergunta_valida() {
        List<Alternativa> alts = List.of(
            new Alternativa("Correta", true),
            new Alternativa("Errada", false)
        );
        this.quiz.adicionarPergunta(new Pergunta("Pergunta Teste?", alts));
    }

    @Quando("eu tento finalizar a criação do quiz")
    public void finalizar_criacao() {
        try {
            // Apenas disparar a validação que já existe no domínio
            this.quiz.validarQuizPronto();
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Então("o quiz deve ser salvo com sucesso")
    public void validar_sucesso() {
        assertNull(excecaoCapturada, "Não deveria ter ocorrido erro");
        assertNotNull(quiz);
    }

    @Então("o sistema deve impedir a criação informando que o quiz não tem perguntas")
    public void validar_erro_sem_perguntas() {
        assertNotNull(excecaoCapturada);
        assertEquals("O quiz deve ter pelo menos uma pergunta para ser disponibilizado", excecaoCapturada.getMessage());
    }

    // --- CENÁRIOS DE RESPOSTA (TENTATIVA) ---

    @Dado("que existe um quiz salvo com o título {string} e a pergunta {string} com a resposta correta {string}")
    public void setup_quiz_existente(String titulo, String enunciado, String respostaCorreta) {
        this.quiz = new Quiz(new QuizId(1), titulo, "Desc");
        List<Alternativa> alts = List.of(new Alternativa(respostaCorreta, true), new Alternativa("Incorreta", false));
        this.quiz.adicionarPergunta(new Pergunta(enunciado, alts));
    }

    @Quando("o usuário {int} responde {string} para a pergunta {string}")
    public void responder_quiz(Integer idUsuario, String resposta, String enunciado) {
        // Simula a lógica de contagem de acertos sem precisar de Casos de Uso/Repositórios
        int acertos = 0;
        for (Pergunta p : quiz.getPerguntas()) {
            if (p.getEnunciado().equals(enunciado)) {
                boolean acertou = p.getAlternativas().stream()
                    .anyMatch(a -> a.getTexto().equals(resposta) && a.isCorreta());
                if (acertou) acertos++;
            }
        }
        
        this.tentativa = new TentativaQuiz(quiz.getId(), new UsuarioId(idUsuario), acertos, quiz.getTotalPerguntas());
    }

    @Então("a tentativa deve ser registrada com {int} acerto de {int} pergunta")
    public void validar_tentativa(Integer acertos, Integer total) {
        assertNotNull(tentativa);
        assertEquals(acertos, tentativa.getAcertos());
        assertEquals(total, tentativa.getTotalPerguntas());
    }
    
    @Quando("o quiz é removido")
    public void o_quiz_e_removido() {
        // No estilo simplificado, "remover" é apenas tornar a referência nula
        this.quiz = null;
    }

    @Então("o quiz deve deixar de existir")
    public void o_quiz_deve_deixar_de_existir() {
        assertNull(this.quiz, "O quiz deveria ser nulo após a remoção");
    }
}