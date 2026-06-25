package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.AdicionarNoticiaCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.AdicionarNoticiaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.NoticiaResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.PesquisarNoticiasCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia.RemoverNoticiaCasoDeUso;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/noticias")
public class NoticiaController {

    private final AdicionarNoticiaCasoDeUso adicionarNoticia;
    private final PesquisarNoticiasCasoDeUso pesquisarNoticias;
    private final RemoverNoticiaCasoDeUso removerNoticia;

    public NoticiaController(AdicionarNoticiaCasoDeUso adicionarNoticia,
                             PesquisarNoticiasCasoDeUso pesquisarNoticias,
                             RemoverNoticiaCasoDeUso removerNoticia) {
        this.adicionarNoticia = adicionarNoticia;
        this.pesquisarNoticias = pesquisarNoticias;
        this.removerNoticia = removerNoticia;
    }

    @PostMapping
    public ResponseEntity<Void> adicionarNoticia(@RequestBody CriarNoticiaRequest body) {
        AdicionarNoticiaComando comando = new AdicionarNoticiaComando(
                body.autorId(),
                body.titulo(),
                body.conteudo(),
                body.categoria(),
                body.filmeId()
        );

        adicionarNoticia.executar(comando);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<NoticiaResumo>> pesquisarNoticias(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) String categoria) {

        List<NoticiaResumo> noticias = pesquisarNoticias.executar(termo, categoria);
        return ResponseEntity.ok(noticias);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerNoticia(@PathVariable int id) {
        removerNoticia.executar(id);
        return ResponseEntity.noContent().build();
    }

    public static record CriarNoticiaRequest(
            int autorId,
            String titulo,
            String conteudo,
            String categoria,
            String filmeId
    ) {}
}
