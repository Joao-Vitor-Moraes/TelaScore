package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.strategy;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;

public class PontuacaoPorAtividade implements EstrategiaPontuacao {
    @Override
    public Pontos calcular(AcaoPontuada acao) {
        return new Pontos(switch (acao) {
            case AVALIAR_FILME -> 25;
            case CRIAR_LISTA -> 40;
            case ACERTAR_QUIZ -> 80;
            case CONVIDAR_AMIGO -> 60;
            case COMPLETAR_META -> 150;
        });
    }
}
