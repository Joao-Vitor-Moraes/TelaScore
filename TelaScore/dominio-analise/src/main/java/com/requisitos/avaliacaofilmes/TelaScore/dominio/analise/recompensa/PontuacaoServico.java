package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa;

import static org.apache.commons.lang3.Validate.notNull;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class PontuacaoServico {

    private final RegistroPontuacaoRepositorio repositorio;

    public PontuacaoServico(RegistroPontuacaoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Pontos concederPontos(UsuarioId usuarioId, AcaoPontuada acao, EstrategiaPontuacao estrategia) {
        Pontos pontos = estrategia.calcular(acao);

        RegistroPontuacao registro = new RegistroPontuacao(
            new RegistroPontuacaoId(repositorio.proximoId()),
            usuarioId,
            pontos,
            acao
        );

        repositorio.salvar(registro);
        return pontos;
    }

    public int calcularTotal(UsuarioId usuarioId) {
        return repositorio.calcularTotalPontos(usuarioId);
    }
}
