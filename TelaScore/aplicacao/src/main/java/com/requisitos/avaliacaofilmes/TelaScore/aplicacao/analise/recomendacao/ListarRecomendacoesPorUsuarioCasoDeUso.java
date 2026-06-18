package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ListarRecomendacoesPorUsuarioCasoDeUso {
    private final RecomendacaoRepositorio repositorio;

    public ListarRecomendacoesPorUsuarioCasoDeUso(RecomendacaoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<RecomendacaoResumo> executar(int usuarioId) {
        return repositorio.buscarTopRecomendacoesPorUsuario(new UsuarioId(usuarioId), 100).stream()
                .map(RecomendacaoResumo::de)
                .toList();
    }
}
