package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.Mensagem;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemServico;

public class EnviarMensagemCasoDeUso {
    private final MensagemServico servico;
    private final GeradorId geradorId;

    public EnviarMensagemCasoDeUso(MensagemRepositorio repositorio, MensagemServico servico, GeradorId geradorId) {
        this.servico = servico;
        this.geradorId = geradorId;
    }

    public Mensagem executar(EnviarMensagemComando comando) {
        Mensagem mensagem = new Mensagem(
                new MensagemId(geradorId.gerarProximoIdMensagem()),
                new UsuarioId(comando.remetenteId()),
                new UsuarioId(comando.destinatarioId()),
                comando.texto()
        );

        servico.enviarMensagem(mensagem);
        return mensagem;
    }
}
