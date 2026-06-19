package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.strategy.PontuacaoPorCompra;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.strategy.PontuacaoPorIndicacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.strategy.PontuacaoPorAniversario;
import java.util.HashMap;

@Component
public class EstrategiaPontuacaoFactory {

    private final Map<AcaoPontuada, EstrategiaPontuacao> estrategias;

    public EstrategiaPontuacaoFactory() {
    this.estrategias = new HashMap<>();
    this.estrategias.put(AcaoPontuada.AVALIAR_FILME,  new PontuacaoPorCompra());
    this.estrategias.put(AcaoPontuada.ACERTAR_QUIZ,   new PontuacaoPorAniversario());
    this.estrategias.put(AcaoPontuada.CRIAR_LISTA,    new PontuacaoPorCompra());
    this.estrategias.put(AcaoPontuada.COMPLETAR_META, new PontuacaoPorCompra());
    this.estrategias.put(AcaoPontuada.CONVIDAR_AMIGO, new PontuacaoPorIndicacao());
}

    public EstrategiaPontuacao obter(AcaoPontuada acao) {
        EstrategiaPontuacao estrategia = estrategias.get(acao);
        if (estrategia == null) {
            throw new IllegalArgumentException("Estratégia não encontrada para: " + acao);
        }
        return estrategia;
    }
}
