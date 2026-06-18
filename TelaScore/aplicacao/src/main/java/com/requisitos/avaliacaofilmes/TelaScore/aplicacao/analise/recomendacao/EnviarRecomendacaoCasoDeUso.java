package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;

public class EnviarRecomendacaoCasoDeUso {
    private final RecomendacaoServico recomendacaoServico;
    private final GeradorId geradorId;

    public EnviarRecomendacaoCasoDeUso(RecomendacaoServico recomendacaoServico, GeradorId geradorId) {
        this.recomendacaoServico = recomendacaoServico;
        this.geradorId = geradorId;
    }

    public void executar(EnviarRecomendacaoComando comando) {
        recomendacaoServico.enviarParaAmigo(
            new RecomendacaoId(geradorId.gerarProximoIdRecomendacao()),
            new UsuarioId(comando.remetenteId),
            new UsuarioId(comando.destinatarioId),
            comando.conteudoId,
            comando.tipoConteudo,
            comando.mensagem
        );
    }
}
