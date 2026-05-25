package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.observer;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;

public class AvaliacaoLogObserver implements AvaliacaoObserver {

    @Override
    public void aoRegistrarAvaliacao(Avaliacao avaliacao) {
        System.out.println(
            "[LOG] Nova avaliação registrada:" +
            " | AvaliacaoId: " + avaliacao.getId().getId() +
            " | FilmeId: "     + avaliacao.getFilmeId().getCodigo() +
            " | UsuarioId: "   + avaliacao.getUsuarioId().getId() +
            " | Nota: "        + avaliacao.getNota().getValor() + " estrela(s)" +
            " | Data: "        + avaliacao.getDataAvaliacao()
        );
    }
}