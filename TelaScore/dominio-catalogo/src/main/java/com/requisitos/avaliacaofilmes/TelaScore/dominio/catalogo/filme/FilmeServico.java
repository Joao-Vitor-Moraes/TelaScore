package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.observer.FilmeObserver;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class FilmeServico {

    private final FilmeRepositorio filmeRepositorio;
    private final List<FilmeObserver> observers = new ArrayList<>();

    public FilmeServico(FilmeRepositorio filmeRepositorio) {
        notNull(filmeRepositorio, "O repositório de filmes não pode ser nulo");
        this.filmeRepositorio = filmeRepositorio;
    }

    public void adicionarObserver(FilmeObserver observer) {
        notNull(observer, "O observer não pode ser nulo");
        observers.add(observer);
    }

    public void salvar(Filme filme) {
        notNull(filme, "O filme não pode ser nulo");
        filmeRepositorio.salvar(filme);
    }

    public void remover(FilmeId filmeId) {
        notNull(filmeId, "O id do filme não pode ser nulo");

        if (filmeRepositorio.obter(filmeId) == null) {
            throw new IllegalArgumentException("Filme não encontrado: " + filmeId.getCodigo());
        }

        filmeRepositorio.remover(filmeId);

        for (FilmeObserver observer : observers) {
            observer.aoRemoverFilme(filmeId);
        }
    }
}