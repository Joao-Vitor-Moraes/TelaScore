package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import java.util.List;
import java.util.stream.Collectors;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;

public class ListarTodasComunidadesCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public ListarTodasComunidadesCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<ComunidadeResumo> executar() {
        return repositorio.listarTodas()
                .stream()
                .map(c -> new ComunidadeResumo(c.getId().getId(), c.getNome(), c.getDescricao()))
                .collect(Collectors.toList());
    }
}