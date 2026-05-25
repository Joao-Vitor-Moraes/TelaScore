package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

class IteradorRanqueadoDeItens implements IteradorDeItens {

    private final List<ItemLista> itensPorPosicao;
    private int indice;

    IteradorRanqueadoDeItens(List<ItemLista> itens) {
        this.itensPorPosicao = itens.stream()
                .sorted(Comparator.comparingInt(item -> item.getPosicao() != null ? item.getPosicao() : Integer.MAX_VALUE))
                .toList();
        this.indice = 0;
    }

    @Override
    public boolean temProximo() {
        return indice < itensPorPosicao.size();
    }

    @Override
    public ItemLista proximo() {
        if (!temProximo()) {
            throw new NoSuchElementException("Não há mais itens ranqueados na lista");
        }
        return itensPorPosicao.get(indice++);
    }
}
