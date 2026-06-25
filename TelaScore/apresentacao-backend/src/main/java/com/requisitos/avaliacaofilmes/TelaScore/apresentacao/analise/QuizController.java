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
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.Quiz;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.quiz.QuizRepositorio;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final CriarQuizCasoDeUso criarQuiz;
    private final RemoverQuizCasoDeUso removerQuiz;
    private final ResponderQuizCasoDeUso responderQuiz;
    private final QuizRepositorio quizRepositorio;

    public QuizController(CriarQuizCasoDeUso criarQuiz,
                          RemoverQuizCasoDeUso removerQuiz,
                          ResponderQuizCasoDeUso responderQuiz,
                          QuizRepositorio quizRepositorio) {
        this.criarQuiz = criarQuiz;
        this.removerQuiz = removerQuiz;
        this.responderQuiz = responderQuiz;
        this.quizRepositorio = quizRepositorio;
    }

    @GetMapping
    public List<QuizResponse> listar() {
        return quizRepositorio.listar().stream().map(this::mapearResponse).toList();
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> obter(@PathVariable int quizId) {
        Quiz quiz = quizRepositorio.obter(new QuizId(quizId));
        if (quiz == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(mapearResponse(quiz));
    }

    @PostMapping
    public ResponseEntity<QuizResponse> criar(@RequestBody CriarQuizRequest body) {
        List<CriarQuizComando.PerguntaInfo> perguntas = body.perguntas().stream()
                .map(p -> new CriarQuizComando.PerguntaInfo(
                        p.enunciado(),
                        p.alternativas().stream()
                                .map(a -> new CriarQuizComando.AlternativaInfo(a.texto(), a.correta()))
                                .toList()))
                .toList();

        Quiz criado = criarQuiz.executar(new CriarQuizComando(body.id(), body.titulo(), body.descricao(), perguntas));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearResponse(criado));
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> remover(@PathVariable int quizId) {
        removerQuiz.executar(new RemoverQuizComando(quizId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{quizId}/tentativas")
    public ResponseEntity<ResultadoQuizResponse> responder(@PathVariable int quizId,
                                                           @RequestBody ResponderQuizRequest body) {
        ResponderQuizCasoDeUso.Resultado resultado =
                responderQuiz.executar(new ResponderQuizComando(body.usuarioId(), quizId, body.respostas()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResultadoQuizResponse(
                resultado.acertos(),
                resultado.totalPerguntas(),
                resultado.pontuacao(),
                resultado.aprovado(),
                resultado.totalPerguntas() == 0 ? 0 : (resultado.acertos() * 100.0 / resultado.totalPerguntas())));
    }

    private QuizResponse mapearResponse(Quiz quiz) {
        return new QuizResponse(
                quiz.getId().getId(),
                quiz.getTitulo(),
                quiz.getDescricao(),
                quiz.getPerguntas().stream()
                        .map(p -> new PerguntaResponse(
                                p.getEnunciado(),
                                p.getAlternativas().stream()
                                        .map(a -> new AlternativaResponse(a.getTexto(), a.isCorreta()))
                                        .toList()))
                        .toList());
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
    public static record QuizResponse(int id, String titulo, String descricao, List<PerguntaResponse> perguntas) {}
    public static record PerguntaResponse(String enunciado, List<AlternativaResponse> alternativas) {}
    public static record AlternativaResponse(String texto, boolean correta) {}
    public static record ResultadoQuizResponse(int acertos, int totalPerguntas, int pontuacao, boolean aprovado, double percentual) {}
}
