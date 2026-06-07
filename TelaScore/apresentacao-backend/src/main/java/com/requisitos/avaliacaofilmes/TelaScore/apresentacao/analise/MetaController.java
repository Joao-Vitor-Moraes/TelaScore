package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.CriarMetaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.CriarMetaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.AdicionarProgressoMetaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.AdicionarProgressoMetaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.RemoverProgressoMetaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.RemoverProgressoMetaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.EstenderPrazoMetaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.EstenderPrazoMetaComando;

@RestController
@RequestMapping("/api/metas")
public class MetaController {

    private final CriarMetaCasoDeUso criarMetaCasoDeUso;
    private final AdicionarProgressoMetaCasoDeUso adicionarProgressoCasoDeUso;
    private final RemoverProgressoMetaCasoDeUso removerProgressoCasoDeUso;
    private final EstenderPrazoMetaCasoDeUso estenderPrazoCasoDeUso;

    public MetaController(CriarMetaCasoDeUso criarMetaCasoDeUso, 
                          AdicionarProgressoMetaCasoDeUso adicionarProgressoCasoDeUso,
                          RemoverProgressoMetaCasoDeUso removerProgressoCasoDeUso,
                          EstenderPrazoMetaCasoDeUso estenderPrazoCasoDeUso) {
        this.criarMetaCasoDeUso = criarMetaCasoDeUso;
        this.adicionarProgressoCasoDeUso = adicionarProgressoCasoDeUso;
        this.removerProgressoCasoDeUso = removerProgressoCasoDeUso;
        this.estenderPrazoCasoDeUso = estenderPrazoCasoDeUso;
    }

    @PostMapping
    public ResponseEntity<String> criarMeta(@RequestBody CriarMetaComando comando) {
        try {
            criarMetaCasoDeUso.executar(comando);
            return ResponseEntity.ok("Meta criada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/progresso")
    public ResponseEntity<String> adicionarProgresso(@PathVariable Integer id, @RequestParam int quantidade) {
        try {
            AdicionarProgressoMetaComando comando = new AdicionarProgressoMetaComando(id, quantidade);
            adicionarProgressoCasoDeUso.executar(comando);
            return ResponseEntity.ok("Progresso atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar progresso: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/progresso/remover")
    public ResponseEntity<String> removerProgresso(@PathVariable Integer id, @RequestParam int quantidade) {
        try {
            RemoverProgressoMetaComando comando = new RemoverProgressoMetaComando(id, quantidade);
            removerProgressoCasoDeUso.executar(comando);
            return ResponseEntity.ok("Progresso removido com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao remover progresso: " + e.getMessage());
        }
    }

    @PutMapping("/prazo")
    public ResponseEntity<String> estenderPrazo(@RequestBody EstenderPrazoMetaComando comando) {
        try {
            estenderPrazoCasoDeUso.executar(comando);
            return ResponseEntity.ok("Prazo alterado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao estender prazo: " + e.getMessage());
        }
    }
}