package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Visibilidade;

public class ObterListaCasoDeUso {

    private final ListaRepositorio listaRepositorio;

    public ObterListaCasoDeUso(ListaRepositorio listaRepositorio) {
        this.listaRepositorio = listaRepositorio;
    }

    public ListaResumo executar(int listaId, Integer quemPedeId) {
        Lista lista = listaRepositorio.obter(new ListaId(listaId));
        if (lista == null) {
            throw new IllegalArgumentException("Lista não encontrada.");
        }
        boolean isPrivada = lista.getVisibilidade() == Visibilidade.PRIVADA;
        boolean isDono = quemPedeId != null && quemPedeId.equals(lista.getDonoId().getId());
        if (isPrivada && !isDono) {
            throw new IllegalArgumentException("Lista não encontrada.");
        }
        return new ListaResumo(
                lista.getId().getId(),
                lista.getDonoId().getId(),
                lista.getTitulo(),
                lista.getDescricao(),
                lista.isRanqueada(),
                lista.getTipo().name(),
                lista.getVisibilidade().name(),
                lista.isColaborativa(),
                lista.getItens().size()
        );
    }
}
