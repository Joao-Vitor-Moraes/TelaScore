package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import java.util.List;
import java.util.NoSuchElementException;

class IteradorSequencialDeItens implements IteradorDeItens {

    private final List<ItemLista> itens;
    private int indice;

    IteradorSequencialDeItens(List<ItemLista> itens) {
        this.itens = List.copyOf(itens);
        this.indice = 0;
    }

    @Override
    public boolean temProximo() {
        return indice < itens.size();
    }

    @Override
    public ItemLista proximo() {
        if (!temProximo()) {
            throw new NoSuchElementException("Não há mais itens na lista");
        }
        return itens.get(indice++);
    }
}
