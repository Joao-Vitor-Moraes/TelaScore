package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import java.util.List;
import java.util.stream.Collectors;

public class ListarSolicitacoesPorSolicitanteCasoDeUso {

    private final SolicitacaoRepositorio solicitacaoRepositorio;

    public ListarSolicitacoesPorSolicitanteCasoDeUso(SolicitacaoRepositorio solicitacaoRepositorio) {
        this.solicitacaoRepositorio = solicitacaoRepositorio;
    }

    public List<SolicitacaoResumo> executar(int solicitanteId) {
        List<SolicitacaoFilme> solicitacoes = solicitacaoRepositorio.pesquisarPorSolicitante(new UsuarioId(solicitanteId));
        return solicitacoes.stream()
                .map(s -> new SolicitacaoResumo(
                        s.getId().getId(),
                        s.getSolicitanteId().getId(),
                        s.getTituloSugerido(),
                        s.getJustificativa(),
                        s.getPais(),
                        s.getAno(),
                        s.getFotoUrl(),
                        s.getStatus().name(),
                        s.getDataCriacao(),
                        s.getFeedbackAdmin()))
                .collect(Collectors.toList());
    }
}
