package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.Recomendacao;

public record RecomendacaoResumo(
        int id,
        int usuarioId,
        String conteudoId,
        String tipoConteudo,
        Integer remetenteId,
        String mensagem,
        double pontuacaoCompatibilidade,
        LocalDateTime dataGeracao,
        String status) {

    public static RecomendacaoResumo de(Recomendacao recomendacao) {
        return new RecomendacaoResumo(
                recomendacao.getId().getId(),
                recomendacao.getUsuarioId().getId(),
                recomendacao.getConteudoId(),
                recomendacao.getTipoConteudo().name(),
                recomendacao.getRemetenteId() == null ? null : recomendacao.getRemetenteId().getId(),
                recomendacao.getMensagem(),
                recomendacao.getPontuacaoCompatibilidade(),
                recomendacao.getDataGeracao(),
                recomendacao.getStatus().name());
    }
}
