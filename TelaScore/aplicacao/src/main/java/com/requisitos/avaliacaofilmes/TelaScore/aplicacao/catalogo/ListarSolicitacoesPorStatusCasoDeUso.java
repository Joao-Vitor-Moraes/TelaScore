package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.EntradaInvalidaException;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoFilme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.SolicitacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao.StatusSolicitacao;

import java.util.List;
import java.util.stream.Collectors;

public class ListarSolicitacoesPorStatusCasoDeUso {

    private final SolicitacaoRepositorio solicitacaoRepositorio;

    public ListarSolicitacoesPorStatusCasoDeUso(SolicitacaoRepositorio solicitacaoRepositorio) {
        this.solicitacaoRepositorio = solicitacaoRepositorio;
    }

    public List<SolicitacaoResumo> executar(String status) {
        StatusSolicitacao statusEnum;
        try {
            statusEnum = StatusSolicitacao.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EntradaInvalidaException("Status inválido: " + status);
        }
        List<SolicitacaoFilme> solicitacoes = solicitacaoRepositorio.pesquisarPorStatus(statusEnum);
        return solicitacoes.stream()
                .map(s -> new SolicitacaoResumo(
                        s.getId().getId(),
                        s.getSolicitanteId().getId(),
                        s.getTituloSugerido(),
                        s.getJustificativa(),
                        s.getStatus().name(),
                        s.getDataCriacao()))
                .collect(Collectors.toList());
    }
}
