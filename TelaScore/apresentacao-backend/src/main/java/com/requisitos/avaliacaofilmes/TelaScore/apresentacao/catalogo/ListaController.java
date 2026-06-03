package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.catalogo;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AdicionarColaboradorListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AdicionarColaboradorListaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RegistrarFilmeAssistidoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RegistrarFilmeAssistidoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AdicionarFilmeNaListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AdicionarFilmeNaListaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ConsultarItensListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.CriarListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.CriarListaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ItemListaDetalhe;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListarListasPorUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ListaResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ObterListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverFilmeDaListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverFilmeDaListaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.RemoverListaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ReordenarItemListaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.ReordenarItemListaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.TornarListaColaborativaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.TornarListaColaborativaComando;

@RestController
@RequestMapping("/api/listas")
public class ListaController {

    private final CriarListaCasoDeUso criarLista;
    private final ObterListaCasoDeUso obterLista;
    private final ListarListasPorUsuarioCasoDeUso listarPorUsuario;
    private final RemoverListaCasoDeUso removerLista;
    private final ConsultarItensListaCasoDeUso consultarItens;
    private final AdicionarFilmeNaListaCasoDeUso adicionarFilme;
    private final RemoverFilmeDaListaCasoDeUso removerFilme;
    private final ReordenarItemListaCasoDeUso reordenarItem;
    private final TornarListaColaborativaCasoDeUso tornarColaborativa;
    private final AdicionarColaboradorListaCasoDeUso adicionarColaborador;
    private final RegistrarFilmeAssistidoCasoDeUso registrarAssistido;

    public ListaController(CriarListaCasoDeUso criarLista,
            ObterListaCasoDeUso obterLista,
            ListarListasPorUsuarioCasoDeUso listarPorUsuario,
            RemoverListaCasoDeUso removerLista,
            ConsultarItensListaCasoDeUso consultarItens,
            AdicionarFilmeNaListaCasoDeUso adicionarFilme,
            RemoverFilmeDaListaCasoDeUso removerFilme,
            ReordenarItemListaCasoDeUso reordenarItem,
            TornarListaColaborativaCasoDeUso tornarColaborativa,
            AdicionarColaboradorListaCasoDeUso adicionarColaborador,
            RegistrarFilmeAssistidoCasoDeUso registrarAssistido) {
        this.criarLista = criarLista;
        this.obterLista = obterLista;
        this.listarPorUsuario = listarPorUsuario;
        this.removerLista = removerLista;
        this.consultarItens = consultarItens;
        this.adicionarFilme = adicionarFilme;
        this.removerFilme = removerFilme;
        this.reordenarItem = reordenarItem;
        this.tornarColaborativa = tornarColaborativa;
        this.adicionarColaborador = adicionarColaborador;
        this.registrarAssistido = registrarAssistido;
    }

    @PostMapping
    public ResponseEntity<Void> criarLista(@RequestBody CriarListaComando comando) {
        criarLista.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{listaId}")
    public ListaResumo obterLista(@PathVariable int listaId) {
        return obterLista.executar(listaId);
    }

    @GetMapping
    public List<ListaResumo> listarPorUsuario(@RequestParam int usuarioId) {
        return listarPorUsuario.executar(usuarioId);
    }

    @DeleteMapping("/{listaId}")
    public ResponseEntity<Void> removerLista(@PathVariable int listaId,
            @RequestParam int usuarioId) {
        removerLista.executar(new RemoverListaComando(listaId, usuarioId));
        return ResponseEntity.noContent().build();
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

    @PatchMapping("/{listaId}/filmes/{filmeId}/assistido")
    public ResponseEntity<Void> registrarAssistido(@PathVariable int listaId,
            @PathVariable int filmeId,
            @RequestBody RegistrarAssistidoRequest body) {
        registrarAssistido.executar(new RegistrarFilmeAssistidoComando(listaId, body.usuarioId(), filmeId));
        return ResponseEntity.noContent().build();
    }

    public static record AdicionarFilmeRequest(int usuarioId, int filmeId, boolean filmeJaFoiAssistido) {}
    public static record ReordenarRequest(int usuarioId, int novaPosicao) {}
    public static record TornarColaborativaRequest(int usuarioId) {}
    public static record AdicionarColaboradorRequest(int donoId, int novoColaboradorId) {}
    public static record RegistrarAssistidoRequest(int usuarioId) {}
}
