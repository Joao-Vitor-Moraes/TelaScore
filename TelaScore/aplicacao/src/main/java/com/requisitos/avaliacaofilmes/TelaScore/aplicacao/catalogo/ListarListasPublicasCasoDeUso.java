package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;

public class ListarListasPublicasCasoDeUso {
    private final ListaRepositorio repositorio;

    public ListarListasPublicasCasoDeUso(ListaRepositorio repositorio) {
        this.repositorio = repositorio;
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
                        lista.getColaboradores().stream().map(c -> c.getId()).toList()))
                .toList();
    }
}
