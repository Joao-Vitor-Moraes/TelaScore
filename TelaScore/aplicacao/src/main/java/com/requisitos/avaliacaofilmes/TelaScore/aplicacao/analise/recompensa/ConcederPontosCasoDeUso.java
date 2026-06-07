package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.PontuacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ConcederPontosCasoDeUso {

    private final PontuacaoServico pontuacaoServico;
    private final EstrategiaPontuacao estrategia;

    public ConcederPontosCasoDeUso(PontuacaoServico pontuacaoServico,
                                    EstrategiaPontuacao estrategia) {
        this.pontuacaoServico = pontuacaoServico;
        this.estrategia = estrategia;
    }

    public void executar(ConcederPontosComando comando) {
        AcaoPontuada acao = comando.acao();
        pontuacaoServico.concederPontos(new UsuarioId(comando.usuarioId()), acao, estrategia);
    }
}