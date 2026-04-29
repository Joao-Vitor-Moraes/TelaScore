package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.TipoConteudo;

public class EnviarRecomendacaoComando {
    public final int remetenteId;
    public final int destinatarioId;
    public final String conteudoId; 
    public final TipoConteudo tipoConteudo; 
    public final String mensagem;

    public EnviarRecomendacaoComando(int remetenteId, int destinatarioId, String conteudoId, TipoConteudo tipoConteudo, String mensagem) {
        this.remetenteId = remetenteId;
        this.destinatarioId = destinatarioId;
        this.conteudoId = conteudoId;
        this.tipoConteudo = tipoConteudo;
        this.mensagem = mensagem;
    }
}