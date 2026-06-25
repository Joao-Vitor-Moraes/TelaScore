package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacaoProvider;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.PontuacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ConcederPontosCasoDeUso {

    private final PontuacaoServico pontuacaoServico;
    private final EstrategiaPontuacaoProvider estrategias;

    public ConcederPontosCasoDeUso(PontuacaoServico pontuacaoServico,
                                    EstrategiaPontuacaoProvider estrategias) {
        this.pontuacaoServico = pontuacaoServico;
        this.estrategias = estrategias;
    }

    public void executar(ConcederPontosComando comando) {
        AcaoPontuada acao = comando.acao();
        pontuacaoServico.concederPontos(new UsuarioId(comando.usuarioId()), acao, estrategias.obter(acao));
    }
}
