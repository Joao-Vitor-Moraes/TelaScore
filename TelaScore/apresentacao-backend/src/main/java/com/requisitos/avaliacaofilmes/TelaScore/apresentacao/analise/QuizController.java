package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.CriarQuizCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.CriarQuizComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.RemoverQuizCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.RemoverQuizComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final CriarQuizCasoDeUso criarQuiz;
    private final RemoverQuizCasoDeUso removerQuiz;
    private final QuizRepositorio quizRepositorio;

    public QuizController(
            CriarQuizCasoDeUso criarQuiz,
            RemoverQuizCasoDeUso removerQuiz,
            QuizRepositorio quizRepositorio) {
        this.criarQuiz = criarQuiz;
        this.removerQuiz = removerQuiz;
        this.quizRepositorio = quizRepositorio;
    }

    @GetMapping
    public List<QuizResumoResponse> listar() {
        return quizRepositorio.listarTodos().stream()
                .map(QuizResumoResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    public QuizResumoResponse obter(@PathVariable int id) {
        Quiz quiz = quizRepositorio.obter(new QuizId(id));
        if (quiz == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz nao encontrado.");
        }
        return QuizResumoResponse.de(quiz);
    }

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody CriarQuizRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corpo da requisicao e obrigatorio.");
        }

        CriarQuizComando comando = new CriarQuizComando(
                null,
                request.titulo(),
                request.descricao(),
                request.perguntas());

        criarQuiz.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable int id) {
        removerQuiz.executar(new RemoverQuizComando(id));
        return ResponseEntity.noContent().build();
    }

    public record CriarQuizRequest(
            String titulo,
            String descricao,
            List<CriarQuizComando.PerguntaInfo> perguntas,
            Integer autorId) {
    }

    public record QuizResumoResponse(
            int id,
            String titulo,
            String descricao,
            List<PerguntaResponse> perguntas) {
        static QuizResumoResponse de(Quiz quiz) {
            List<PerguntaResponse> perguntas = quiz.getPerguntas().stream()
                    .map(pergunta -> new PerguntaResponse(
                            pergunta.getEnunciado(),
                            pergunta.getAlternativas().stream()
                                    .map(alt -> new AlternativaResponse(alt.getTexto(), alt.isCorreta()))
                                    .toList()))
                    .toList();

            return new QuizResumoResponse(
                    quiz.getId().getId(),
                    quiz.getTitulo(),
                    quiz.getDescricao(),
                    perguntas);
        }
    }

    public record PerguntaResponse(
            String enunciado,
            List<AlternativaResponse> alternativas) {
    }

    public record AlternativaResponse(
            String texto,
            boolean correta) {
    }
}
