package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemRepositorio;

public class RemoverMensagemCasoDeUso {
    private final MensagemRepositorio repositorio;

    public RemoverMensagemCasoDeUso(MensagemRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(RemoverMensagemComando comando) {
        repositorio.remover(new MensagemId(comando.mensagemId()));
    }
}