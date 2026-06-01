package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.observer;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;

public interface FilmeObserver {
    void aoRemoverFilme(FilmeId filmeId);
}