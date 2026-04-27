package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;

import java.util.List;

public class PesquisarNoticiasCasoDeUso {
    private final NoticiaRepositorio repositorio;

    public PesquisarNoticiasCasoDeUso(NoticiaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<NoticiaResumo> executar(String termo, String categoriaNome) {
        CategoriaNoticia categoria = (categoriaNome != null && !categoriaNome.isBlank())
                ? CategoriaNoticia.valueOf(categoriaNome.toUpperCase())
                : null;

        return repositorio.buscarPorFiltros(termo, categoria).stream()
                .map(n -> new NoticiaResumo(
                        n.getId().getId(),
                        n.getTitulo(),
                        n.getCategoria().getDescricao(),
                        n.getDataPublicacao().toString()))
                .toList();
    }
}