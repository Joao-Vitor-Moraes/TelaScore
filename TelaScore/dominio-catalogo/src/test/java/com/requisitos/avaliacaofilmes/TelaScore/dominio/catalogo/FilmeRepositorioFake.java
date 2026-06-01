package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;

import java.util.HashMap;
import java.util.Map;

public class FilmeRepositorioFake implements FilmeRepositorio {

    private final Map<String, Filme> armazenamento = new HashMap<>();

    @Override
    public void salvar(Filme filme) {
        armazenamento.put(filme.getId().getCodigo(), filme);
    }

    @Override
    public Filme obter(FilmeId id) {
        return armazenamento.get(id.getCodigo());
    }

    @Override
    public void remover(FilmeId id) {
        armazenamento.remove(id.getCodigo());
    }

    @Override
    public boolean existeComTitulo(String titulo) {
        return armazenamento.values().stream()
                .anyMatch(f -> f.getTitulo().equalsIgnoreCase(titulo));
    }
}