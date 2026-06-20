package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.CriarQuizCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.CriarQuizComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.RemoverQuizCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.RemoverQuizComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.ResponderQuizCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.quiz.ResponderQuizComando;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final CriarQuizCasoDeUso criarQuiz;
    private final RemoverQuizCasoDeUso removerQuiz;
    private final ResponderQuizCasoDeUso responderQuiz;

    public QuizController(CriarQuizCasoDeUso criarQuiz,
                          RemoverQuizCasoDeUso removerQuiz,
                          ResponderQuizCasoDeUso responderQuiz) {
        this.criarQuiz = criarQuiz;
        this.removerQuiz = removerQuiz;
        this.responderQuiz = responderQuiz;
    }

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody CriarQuizRequest body) {
        List<CriarQuizComando.PerguntaInfo> perguntas = body.perguntas().stream()
                .map(p -> new CriarQuizComando.PerguntaInfo(
                        p.enunciado(),
                        p.alternativas().stream()
                                .map(a -> new CriarQuizComando.AlternativaInfo(a.texto(), a.correta()))
                                .toList()))
                .toList();

        criarQuiz.executar(new CriarQuizComando(body.id(), body.titulo(), body.descricao(), perguntas));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> remover(@PathVariable int quizId) {
        removerQuiz.executar(new RemoverQuizComando(quizId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/tentativas")
    public ResponseEntity<Void> responder(@PathVariable int quizId,
                                          @RequestBody ResponderQuizRequest body) {
        responderQuiz.executar(new ResponderQuizComando(body.usuarioId(), quizId, body.respostas()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public static record CriarQuizRequest(
            int id,
            String titulo,
            String descricao,
            List<PerguntaRequest> perguntas) {

        public record PerguntaRequest(String enunciado, List<AlternativaRequest> alternativas) {}
        public record AlternativaRequest(String texto, boolean correta) {}
    }

    public static record ResponderQuizRequest(int usuarioId, Map<String, String> respostas) {}
}
