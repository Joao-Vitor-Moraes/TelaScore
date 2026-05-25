package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.IteradorDeItens;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;

import java.util.ArrayList;
import java.util.List;

public class ConsultarItensListaCasoDeUso {

    private final ListaRepositorio listaRepositorio;

    public ConsultarItensListaCasoDeUso(ListaRepositorio listaRepositorio) {
        this.listaRepositorio = listaRepositorio;
    }

    public List<ItemListaDetalhe> executar(int listaId) {
        Lista lista = listaRepositorio.obter(new ListaId(listaId));
        if (lista == null) {
            throw new IllegalArgumentException("A lista informada não existe.");
        }

        List<ItemListaDetalhe> resultado = new ArrayList<>();
        IteradorDeItens iterador = lista.criarIterador();
        while (iterador.temProximo()) {
            ItemLista item = iterador.proximo();
            resultado.add(new ItemListaDetalhe(
                    item.getFilmeId().getCodigo(),
                    item.getPosicao(),
                    item.getDataAdicao()
            ));
        }
        return resultado;
    }
}
