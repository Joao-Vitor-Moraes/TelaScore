package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.util.List;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;

public class ListarFilmesCasoDeUso {

    private final FilmeRepositorio filmeRepositorio;

    public ListarFilmesCasoDeUso(FilmeRepositorio filmeRepositorio) {
        this.filmeRepositorio = filmeRepositorio;
    }

    public List<FilmeResumo> executar() {
        return filmeRepositorio.listarTodos().stream()
                .map(f -> new FilmeResumo(
                        Integer.parseInt(f.getId().getCodigo()),
                        f.getTitulo(),
                        f.getAnoLancamento(),
                        "",
                        0.0,
                        f.getImagemUrl()
                ))
                .collect(Collectors.toList());
    }
}
