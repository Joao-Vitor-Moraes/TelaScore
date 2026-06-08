package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import java.time.LocalDateTime;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.EnviarMensagemComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.MensagemComunidade;

public class EnviarMensagemCasoDeUso {
    private final ComunidadeRepositorio repositorio;

    public EnviarMensagemCasoDeUso(ComunidadeRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void executar(EnviarMensagemComando comando) {
        if (comando.conteudo() == null || comando.conteudo().trim().isEmpty()) {
            throw new IllegalArgumentException("O conteudo da mensagem nao pode ser vazio.");
        }

        ComunidadeId cid = new ComunidadeId(comando.comunidadeId());
        UsuarioId uid = new UsuarioId(comando.usuarioId());

        if (!repositorio.existeMembro(cid, uid)) {
            throw new IllegalArgumentException("Apenas membros ativos podem enviar mensagens nesta comunidade.");
        }

        MensagemComunidade novaMensagem = new MensagemComunidade(
                0,
                comando.comunidadeId(),
                comando.usuarioId(),
                comando.conteudo(),
                LocalDateTime.now()
        );

        repositorio.salvarMensagem(novaMensagem);
    }
}