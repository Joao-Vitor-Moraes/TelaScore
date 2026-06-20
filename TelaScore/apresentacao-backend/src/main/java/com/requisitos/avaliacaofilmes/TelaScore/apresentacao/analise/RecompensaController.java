package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa.ConcederPontosComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa.ConcederPontosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa.ConsultarTotalPontosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa.ListarHistoricoPontosCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacao;

@RestController
@RequestMapping("/api/recompensas")
public class RecompensaController {

    private final ConcederPontosCasoDeUso concederPontos;
    private final ConsultarTotalPontosCasoDeUso consultarTotal;
    private final ListarHistoricoPontosCasoDeUso listarHistorico;

    public RecompensaController(
            ConcederPontosCasoDeUso concederPontos,
            ConsultarTotalPontosCasoDeUso consultarTotal,
            ListarHistoricoPontosCasoDeUso listarHistorico) {
        this.concederPontos = concederPontos;
        this.consultarTotal = consultarTotal;
        this.listarHistorico = listarHistorico;
    }

    @PostMapping("/conceder")
    public ResponseEntity<Void> conceder(@RequestBody ConcederPontosRequest body) {
        concederPontos.executar(new ConcederPontosComando(
            body.usuarioId(),
            AcaoPontuada.valueOf(body.acao()),
            0
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/total")
    public ResponseEntity<Integer> consultarTotal(@RequestParam int usuarioId) {
        return ResponseEntity.ok(consultarTotal.executar(usuarioId));
    }

    @GetMapping("/historico")
    public ResponseEntity<List<RegistroPontuacaoResumo>> listarHistorico(@RequestParam int usuarioId) {
        List<RegistroPontuacao> lista = listarHistorico.executar(usuarioId);
        List<RegistroPontuacaoResumo> resumo = lista.stream()
            .map(r -> new RegistroPontuacaoResumo(
                r.getId().getId(),
                r.getAcao().name(),
                r.getPontosGanhos().getQuantidade(),
                r.getDataRegistro().toString()
            ))
            .collect(Collectors.toList());
        return ResponseEntity.ok(resumo);
    }

    public static record ConcederPontosRequest(int usuarioId, String acao) {}

    public static record RegistroPontuacaoResumo(
        int id,
        String acao,
        int pontos,
        String dataRegistro
    ) {}
}