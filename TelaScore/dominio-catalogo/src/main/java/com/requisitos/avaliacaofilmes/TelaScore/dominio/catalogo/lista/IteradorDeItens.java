package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import java.util.Iterator;

public interface IteradorDeItens extends Iterator<ItemLista> {

    boolean temProximo();

    ItemLista proximo();

    @Override
    default boolean hasNext() {
        return temProximo();
    }

    @Override
    default ItemLista next() {
        return proximo();
    }
}
