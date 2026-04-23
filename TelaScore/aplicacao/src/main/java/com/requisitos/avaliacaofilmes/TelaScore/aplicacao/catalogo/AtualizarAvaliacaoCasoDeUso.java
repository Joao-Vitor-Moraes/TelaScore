package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Nota;


public class AtualizarAvaliacaoCasoDeUso {

    private final AvaliacaoRepositorio avaliacaoRepositorio;

    public AtualizarAvaliacaoCasoDeUso(AvaliacaoRepositorio avaliacaoRepositorio) {
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    public void executar(AtualizarAvaliacaoComando comando) {
        AvaliacaoId avaliacaoId = new AvaliacaoId(comando.avaliacaoId());

        Avaliacao avaliacao = avaliacaoRepositorio.obter(avaliacaoId);
        if (avaliacao == null) {
            throw new IllegalArgumentException("Avaliação não encontrada: " + comando.avaliacaoId());
        }

        if (comando.valorNota() != null) {
            avaliacao.setNota(new Nota(comando.valorNota()));
        }
        if (comando.resenha() != null) {
            avaliacao.setResenha(comando.resenha());
        }

        avaliacaoRepositorio.salvar(avaliacao);
    }
}