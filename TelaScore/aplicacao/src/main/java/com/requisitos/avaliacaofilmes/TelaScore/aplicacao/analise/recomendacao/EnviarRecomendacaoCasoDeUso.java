package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class EnviarRecomendacaoCasoDeUso {
    private final RecomendacaoServico recomendacaoServico;

    public EnviarRecomendacaoCasoDeUso(RecomendacaoServico recomendacaoServico) {
        this.recomendacaoServico = recomendacaoServico;
    }

    public void executar(EnviarRecomendacaoComando comando) {
        recomendacaoServico.enviarParaAmigo(
            new UsuarioId(comando.remetenteId),
            new UsuarioId(comando.destinatarioId),
            comando.conteudoId,
            comando.tipoConteudo,
            comando.mensagem
        );
    }
}