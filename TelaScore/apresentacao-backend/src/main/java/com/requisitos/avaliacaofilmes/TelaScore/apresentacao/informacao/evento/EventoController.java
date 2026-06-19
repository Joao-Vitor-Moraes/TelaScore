package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.evento;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.AgendarEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.AgendarEventoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.CancelarEventoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.EventoResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ListarEventosFuturosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento.ObterEventoCasoDeUso;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final AgendarEventoCasoDeUso agendarEvento;
    private final CancelarEventoCasoDeUso cancelarEvento;
    private final ListarEventosFuturosCasoDeUso listarEventosFuturos;
    private final ObterEventoCasoDeUso obterEvento;

    public EventoController(AgendarEventoCasoDeUso agendarEvento,
                           CancelarEventoCasoDeUso cancelarEvento,
                           ListarEventosFuturosCasoDeUso listarEventosFuturos,
                           ObterEventoCasoDeUso obterEvento) {
        this.agendarEvento = agendarEvento;
        this.cancelarEvento = cancelarEvento;
        this.listarEventosFuturos = listarEventosFuturos;
        this.obterEvento = obterEvento;
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
    public ResponseEntity<List<EventoResumo>> listarFuturos(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime aPartirDe) {
        return ResponseEntity.ok(listarEventosFuturos.executar(aPartirDe));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoResumo> obter(@PathVariable int id) {
        EventoResumo evento = obterEvento.executar(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(evento);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable int id) {
        cancelarEvento.executar(id);
        return ResponseEntity.noContent().build();
    }
}
