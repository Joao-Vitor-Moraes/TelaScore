package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.temporaria;

import static org.apache.commons.lang3.Validate.notNull;

public class MensagemServico {
    private final MensagemRepositorio repositorio;

    public MensagemServico(MensagemRepositorio repositorio) {
        notNull(repositorio, "Repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public void enviar(Mensagem mensagem) {
        notNull(mensagem, "A mensagem não pode ser nula");
        repositorio.salvar(mensagem);
    }

    public void marcarComoLida(Mensagem mensagem) {
        mensagem.marcarComoLida();
        repositorio.salvar(mensagem);
    }
}