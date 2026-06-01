package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listas")
public class ListaController {

    private final CriarListaCasoDeUso criarLista;
    private final ConsultarItensListaCasoDeUso consultarItens;
    private final AdicionarFilmeNaListaCasoDeUso adicionarFilme;
    private final RemoverFilmeDaListaCasoDeUso removerFilme;
    private final ReordenarItemListaCasoDeUso reordenarItem;
    private final TornarListaColaborativaCasoDeUso tornarColaborativa;
    private final AdicionarColaboradorListaCasoDeUso adicionarColaborador;

    public ListaController(CriarListaCasoDeUso criarLista,
            ConsultarItensListaCasoDeUso consultarItens,
            AdicionarFilmeNaListaCasoDeUso adicionarFilme,
            RemoverFilmeDaListaCasoDeUso removerFilme,
            ReordenarItemListaCasoDeUso reordenarItem,
            TornarListaColaborativaCasoDeUso tornarColaborativa,
            AdicionarColaboradorListaCasoDeUso adicionarColaborador) {
        this.criarLista = criarLista;
        this.consultarItens = consultarItens;
        this.adicionarFilme = adicionarFilme;
        this.removerFilme = removerFilme;
        this.reordenarItem = reordenarItem;
        this.tornarColaborativa = tornarColaborativa;
        this.adicionarColaborador = adicionarColaborador;
    }

    @PostMapping
    public ResponseEntity<Void> criarLista(@RequestBody CriarListaComando comando) {
        criarLista.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{listaId}/itens")
    public List<ItemListaDetalhe> consultarItens(@PathVariable int listaId) {
        return consultarItens.executar(listaId);
    }

    @PostMapping("/{listaId}/filmes")
    public ResponseEntity<Void> adicionarFilme(@PathVariable int listaId,
            @RequestBody AdicionarFilmeRequest body) {
        adicionarFilme.executar(new AdicionarFilmeNaListaComando(listaId, body.usuarioId(), body.filmeId(), body.filmeJaFoiAssistido()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{listaId}/filmes/{filmeId}")
    public ResponseEntity<Void> removerFilme(@PathVariable int listaId,
            @PathVariable int filmeId,
            @RequestParam int usuarioId) {
        removerFilme.executar(new RemoverFilmeDaListaComando(listaId, usuarioId, filmeId));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{listaId}/filmes/{filmeId}/posicao")
    public ResponseEntity<Void> reordenarFilme(@PathVariable int listaId,
            @PathVariable int filmeId,
            @RequestBody ReordenarRequest body) {
        reordenarItem.executar(new ReordenarItemListaComando(listaId, body.usuarioId(), filmeId, body.novaPosicao()));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{listaId}/colaborativa")
    public ResponseEntity<Void> tornarColaborativa(@PathVariable int listaId,
            @RequestBody TornarColaborativaRequest body) {
        tornarColaborativa.executar(new TornarListaColaborativaComando(listaId, body.usuarioId()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listaId}/colaboradores")
    public ResponseEntity<Void> adicionarColaborador(@PathVariable int listaId,
            @RequestBody AdicionarColaboradorRequest body) {
        adicionarColaborador.executar(new AdicionarColaboradorListaComando(listaId, body.donoId(), body.novoColaboradorId()));
        return ResponseEntity.noContent().build();
    }

    private record AdicionarFilmeRequest(int usuarioId, int filmeId, boolean filmeJaFoiAssistido) {}
    private record ReordenarRequest(int usuarioId, int novaPosicao) {}
    private record TornarColaborativaRequest(int usuarioId) {}
    private record AdicionarColaboradorRequest(int donoId, int novoColaboradorId) {}
}
