package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa;

import static org.apache.commons.lang3.Validate.notNull;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class PontuacaoServico {

    private final RegistroPontuacaoRepositorio repositorio;

    public PontuacaoServico(RegistroPontuacaoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void concederPontos(UsuarioId usuarioId, AcaoPontuada acao, EstrategiaPontuacao estrategia) {
        Pontos pontos = estrategia.calcular(acao);

        RegistroPontuacao registro = new RegistroPontuacao(
            new RegistroPontuacaoId(gerarId()),
            usuarioId,
            pontos,
            acao
        );

        repositorio.salvar(registro);
    }

    private int gerarId() {
        return (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
    }
}