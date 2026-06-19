package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.calendario;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.AlternarLembreteCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.CalendarioResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.CriarCalendarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.DispararLembretesCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.ObterCalendarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.RegistrarFilmeComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.RegistrarFilmeNoCalendarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario.RemoverFilmeDoCalendarioCasoDeUso;

@RestController
@RequestMapping("/api/calendarios")
public class CalendarioController {

    private final CriarCalendarioCasoDeUso criarCalendario;
    private final ObterCalendarioCasoDeUso obterCalendario;
    private final RegistrarFilmeNoCalendarioCasoDeUso registrarFilme;
    private final RemoverFilmeDoCalendarioCasoDeUso removerFilme;
    private final AlternarLembreteCasoDeUso alternarLembrete;
    private final DispararLembretesCasoDeUso dispararLembretes;

    public CalendarioController(CriarCalendarioCasoDeUso criarCalendario,
                               ObterCalendarioCasoDeUso obterCalendario,
                               RegistrarFilmeNoCalendarioCasoDeUso registrarFilme,
                               RemoverFilmeDoCalendarioCasoDeUso removerFilme,
                               AlternarLembreteCasoDeUso alternarLembrete,
                               DispararLembretesCasoDeUso dispararLembretes) {
        this.criarCalendario = criarCalendario;
        this.obterCalendario = obterCalendario;
        this.registrarFilme = registrarFilme;
        this.removerFilme = removerFilme;
        this.alternarLembrete = alternarLembrete;
        this.dispararLembretes = dispararLembretes;
    }

    @PostMapping
    public ResponseEntity<Void> criarCalendario(@RequestBody CriarCalendarioRequest body) {
        criarCalendario.executar(body.usuarioId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CalendarioResumo> obterCalendario(@PathVariable int usuarioId) {
        CalendarioResumo calendario = obterCalendario.executar(usuarioId);
        if (calendario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(calendario);
    }

    @PostMapping("/{usuarioId}/filmes")
    public ResponseEntity<?> registrarFilme(@PathVariable int usuarioId, @RequestBody RegistrarFilmeRequest body) {
        try {
            RegistrarFilmeComando comando = new RegistrarFilmeComando(usuarioId, body.filmeId(), body.dataEstreia());
            registrarFilme.executar(comando);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{usuarioId}/filmes/{filmeId}")
    public ResponseEntity<Void> removerFilme(@PathVariable int usuarioId, @PathVariable String filmeId) {
        removerFilme.executar(usuarioId, filmeId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{usuarioId}/filmes/{filmeId}/lembrete")
    public ResponseEntity<Void> alternarLembrete(@PathVariable int usuarioId, @PathVariable String filmeId) {
        alternarLembrete.executar(usuarioId, filmeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{usuarioId}/lembretes")
    public ResponseEntity<Void> dispararLembretes(@PathVariable int usuarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        dispararLembretes.executar(usuarioId, data);
        return ResponseEntity.noContent().build();
    }

    public static record CriarCalendarioRequest(int usuarioId) {}
    public static record RegistrarFilmeRequest(String filmeId, LocalDate dataEstreia) {}
}
