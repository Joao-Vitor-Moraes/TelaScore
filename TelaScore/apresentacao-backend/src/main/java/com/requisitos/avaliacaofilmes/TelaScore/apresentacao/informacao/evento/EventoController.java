package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.evento;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.AgendarEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.AgendarEventoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.CancelarEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.EventoResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ListarEventosVisiveisCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ObterEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ResponderEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.AmigoResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao.ListarAmigosCasoDeUso;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final AgendarEventoCasoDeUso agendarEvento;
    private final CancelarEventoCasoDeUso cancelarEvento;
    private final ListarEventosVisiveisCasoDeUso listarEventosVisiveis;
    private final ObterEventoCasoDeUso obterEvento;
    private final ResponderEventoCasoDeUso responderEvento;
    private final ListarAmigosCasoDeUso listarAmigos;

    public EventoController(AgendarEventoCasoDeUso agendarEvento,
                           CancelarEventoCasoDeUso cancelarEvento,
                           ListarEventosVisiveisCasoDeUso listarEventosVisiveis,
                           ObterEventoCasoDeUso obterEvento,
                           ResponderEventoCasoDeUso responderEvento,
                           ListarAmigosCasoDeUso listarAmigos) {
        this.agendarEvento = agendarEvento;
        this.cancelarEvento = cancelarEvento;
        this.listarEventosVisiveis = listarEventosVisiveis;
        this.obterEvento = obterEvento;
        this.responderEvento = responderEvento;
        this.listarAmigos = listarAmigos;
    }

    @PostMapping
    public ResponseEntity<?> agendar(@RequestBody AgendarEventoComando comando) {
        try {
            int id = agendarEvento.executar(comando);
            return ResponseEntity.status(HttpStatus.CREATED).body(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<EventoResumo>> listar(@RequestParam int usuarioId) {
        return ResponseEntity.ok(listarEventosVisiveis.executar(usuarioId));
    }

    @GetMapping("/amigos")
    public ResponseEntity<List<AmigoResumo>> amigos(@RequestParam int usuarioId) {
        return ResponseEntity.ok(listarAmigos.executar(usuarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResumo> obter(@PathVariable int id,
                                              @RequestParam(required = false) Integer usuarioId) {
        EventoResumo evento = obterEvento.executar(id, usuarioId);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(evento);
    }

    @PostMapping("/{id}/resposta")
    public ResponseEntity<?> responder(@PathVariable int id, @RequestBody RespostaRequest body) {
        try {
            responderEvento.executar(id, body.usuarioId(), body.resposta());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable int id) {
        cancelarEvento.executar(id);
        return ResponseEntity.noContent().build();
    }

    public static record RespostaRequest(int usuarioId, String resposta) {}
}
