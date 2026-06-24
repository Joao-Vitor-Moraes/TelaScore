package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.ResultadoAtualizacaoMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.NotificacaoMetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacaoProvider;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.PontuacaoServico;

public class AtualizadorMetaComNotificacao extends AtualizadorMetaTemplate {
    private final NotificacaoMetaRepositorio notificacoes;
    private final PontuacaoServico pontuacao;
    private final EstrategiaPontuacaoProvider estrategias;

    public AtualizadorMetaComNotificacao(MetaRepositorio metaRepositorio,
            NotificacaoMetaRepositorio notificacoes,
            PontuacaoServico pontuacao,
            EstrategiaPontuacaoProvider estrategias) {
        super(metaRepositorio);
        this.notificacoes = notificacoes;
        this.pontuacao = pontuacao;
        this.estrategias = estrategias;
    }

    @Override
    protected ResultadoAtualizacaoMeta executarEfeitoColateral(Meta meta, boolean concluiuAgora) {
        String mensagem;
        int pontosGanhos = 0;

        if (concluiuAgora) {
            meta.marcarPontosConcedidos();
            if (meta.getMetaSistemaId() != null) {
                Pontos pontos = pontuacao.concederPontos(
                        meta.getUsuarioId(),
                        AcaoPontuada.COMPLETAR_META,
                        estrategias.obter(AcaoPontuada.COMPLETAR_META));
                pontosGanhos = pontos.getQuantidade();
                if (meta.isNotificacaoAtiva()) {
                    notificacoes.criar(meta.getUsuarioId(), meta.getId(), meta.getTitulo(), pontosGanhos);
                }
            }
            mensagem = "Meta concluida: " + meta.getTitulo() + ".";
        } else {
            int faltam = meta.getQuantidadeAlvo() - meta.getQuantidadeAtual();
            mensagem = faltam == 1
                    ? "Falta apenas 1 filme para concluir esta meta."
                    : "Faltam " + faltam + " filmes para concluir esta meta.";
        }

        return new ResultadoAtualizacaoMeta(
                meta.getId().getId(),
                meta.getQuantidadeAtual(),
                meta.getQuantidadeAlvo(),
                meta.getStatus().name(),
                mensagem,
                pontosGanhos,
                0);
    }
}
