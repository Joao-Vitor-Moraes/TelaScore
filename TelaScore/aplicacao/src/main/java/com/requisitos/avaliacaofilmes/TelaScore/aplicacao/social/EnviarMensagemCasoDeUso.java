package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.MensagemServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.Mensagem;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class EnviarMensagemCasoDeUso {
    private final MensagemRepositorio repositorio;
    private final MensagemServico servico;
    private final GeradorId geradorId;

    public EnviarMensagemCasoDeUso(MensagemRepositorio repositorio, MensagemServico servico, GeradorId geradorId) {
        this.repositorio = repositorio;
        this.servico = servico;
        this.geradorId = geradorId;
    }

    public void executar(EnviarMensagemComando comando) {
        // 1. Gera o ID (Você precisará adicionar gerarProximoIdMensagem() no GeradorId)
        MensagemId novoId = new MensagemId(geradorId.gerarProximoIdMensagem());
        
        // 2. Cria a entidade de domínio (Aqui disparam as validações de "si mesmo" e "vazio")
        Mensagem mensagem = new Mensagem(
            novoId, 
            new UsuarioId(comando.remetenteId()), 
            new UsuarioId(comando.destinatarioId()), 
            comando.texto()
        );

        // 3. Usa o serviço para enviar e o repositório para salvar
        servico.enviarMensagem(mensagem);
    }
}