package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.IteradorDeItens;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;

import java.util.ArrayList;
import java.util.List;

public class ConsultarItensListaCasoDeUso {

    private final ListaRepositorio listaRepositorio;
    private final FilmeRepositorio filmeRepositorio;

    public ConsultarItensListaCasoDeUso(ListaRepositorio listaRepositorio, FilmeRepositorio filmeRepositorio) {
        this.listaRepositorio = listaRepositorio;
        this.filmeRepositorio = filmeRepositorio;
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
            int filmeIdInt = Integer.parseInt(item.getFilmeId().getCodigo());
            Filme filme = filmeRepositorio.obter(item.getFilmeId());
            String titulo = filme != null ? filme.getTitulo() : "";
            String imagemUrl = filme != null ? filme.getImagemUrl() : null;
            resultado.add(new ItemListaDetalhe(
                    filmeIdInt,
                    titulo,
                    imagemUrl,
                    item.getPosicao(),
                    item.getDataAdicao()
            ));
        }
        return resultado;
    }
}
