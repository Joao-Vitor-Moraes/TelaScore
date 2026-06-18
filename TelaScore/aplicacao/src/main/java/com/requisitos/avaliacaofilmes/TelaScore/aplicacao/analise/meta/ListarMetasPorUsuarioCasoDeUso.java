package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ListarMetasPorUsuarioCasoDeUso {
    private final MetaRepositorio repositorio;

    public ListarMetasPorUsuarioCasoDeUso(MetaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<MetaResumo> executar(int usuarioId) {
        return repositorio.buscarPorUsuario(new UsuarioId(usuarioId)).stream()
                .map(MetaResumo::de)
                .toList();
    }
}
