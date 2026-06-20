package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ConsultarTotalPontosCasoDeUso {

    private final RegistroPontuacaoRepositorio repositorio;

    public ConsultarTotalPontosCasoDeUso(RegistroPontuacaoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Integer executar(int usuarioId) {
        return repositorio.calcularTotalPontos(new UsuarioId(usuarioId));
    }
}