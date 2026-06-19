package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.ResultadoAtualizacaoMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.PontuacaoServico;

public class AtualizadorMetaComNotificacao extends AtualizadorMetaTemplate {
    private final PontuacaoServico pontuacaoServico;
    private final EstrategiaPontuacao estrategia;

    public AtualizadorMetaComNotificacao(MetaRepositorio metaRepositorio,
            PontuacaoServico pontuacaoServico, EstrategiaPontuacao estrategia) {
        super(metaRepositorio);
        this.pontuacaoServico = pontuacaoServico;
        this.estrategia = estrategia;
    }

    @Override
    protected ResultadoAtualizacaoMeta executarEfeitoColateral(Meta meta, boolean concluiuAgora) {
        int pontosGanhos = 0;
        String mensagem;

        if (concluiuAgora && !meta.isPontosConcedidos()) {
            Pontos pontos = pontuacaoServico.concederPontos(
                    meta.getUsuarioId(), AcaoPontuada.COMPLETAR_META, estrategia);
            pontosGanhos = pontos.getQuantidade();
            meta.marcarPontosConcedidos();
            mensagem = "Meta concluída! Você ganhou " + pontosGanhos + " pontos.";
        } else if (concluiuAgora) {
            mensagem = "Meta concluída!";
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
                pontuacaoServico.calcularTotal(meta.getUsuarioId()));
    }
}
