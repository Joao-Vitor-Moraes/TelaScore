package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.ResultadoAtualizacaoMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.NotificacaoMetaRepositorio;

public class AtualizadorMetaComNotificacao extends AtualizadorMetaTemplate {
    private final NotificacaoMetaRepositorio notificacoes;

    public AtualizadorMetaComNotificacao(MetaRepositorio metaRepositorio,
            NotificacaoMetaRepositorio notificacoes) {
        super(metaRepositorio);
        this.notificacoes = notificacoes;
    }

    @Override
    protected ResultadoAtualizacaoMeta executarEfeitoColateral(Meta meta, boolean concluiuAgora) {
        String mensagem;

        if (concluiuAgora) {
            meta.marcarPontosConcedidos();
            notificacoes.criar(meta.getUsuarioId(), meta.getId(), meta.getTitulo());
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
                0,
                0);
    }
}
