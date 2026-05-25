package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.observer;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;

public interface AvaliacaoObserver {
    void aoRegistrarAvaliacao(Avaliacao avaliacao);
}