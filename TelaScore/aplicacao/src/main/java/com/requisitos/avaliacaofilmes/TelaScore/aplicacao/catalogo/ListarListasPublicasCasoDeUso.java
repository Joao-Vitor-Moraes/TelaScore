package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;

public class ListarListasPublicasCasoDeUso {
    private final ListaRepositorio repositorio;
    private final FilmeRepositorio filmeRepositorio;

    public ListarListasPublicasCasoDeUso(ListaRepositorio repositorio, FilmeRepositorio filmeRepositorio) {
        this.repositorio = repositorio;
        this.filmeRepositorio = filmeRepositorio;
    }

    public List<ListaResumo> executar() {
        return repositorio.listarPublicas().stream()
                .map(lista -> new ListaResumo(
                        lista.getId().getId(),
                        lista.getDonoId().getId(),
                        lista.getTitulo(),
                        lista.getDescricao(),
                        lista.isRanqueada(),
                        lista.getTipo().name(),
                        lista.getVisibilidade().name(),
                        lista.isColaborativa(),
                        lista.getItens().size(),
                        obterCapaUrl(lista),
                        lista.getColaboradores().stream().map(c -> c.getId()).toList()))
                .toList();
    }

    private String obterCapaUrl(Lista lista) {
        return lista.getItens().stream()
                .map(item -> filmeRepositorio.obter(item.getFilmeId()))
                .filter(filme -> filme != null && filme.getImagemUrl() != null && !filme.getImagemUrl().isBlank())
                .map(filme -> filme.getImagemUrl())
                .findFirst()
                .orElse(null);
    }
}
