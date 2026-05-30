package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;

public class AtualizadorMetaComNotificacao extends AtualizadorMetaTemplate {

    public AtualizadorMetaComNotificacao(MetaRepositorio metaRepositorio) {
        super(metaRepositorio);
    }

    @Override
    protected void executarEfeitoColateral(Meta meta, boolean concluiuAgora) {
        if (concluiuAgora) {
            System.out.println("🎉 [GAMIFICAÇÃO] PARABÉNS! O Usuário " + meta.getUsuarioId().getId() + 
                               " acabou de concluir a meta '" + meta.getTitulo() + "'!");
        } else {
            int faltam = meta.getQuantidadeAlvo() - meta.getQuantidadeAtual();
            System.out.println("📈 [GAMIFICAÇÃO] Continua o bom trabalho! Faltam apenas " + faltam + 
                               " filmes para a meta '" + meta.getTitulo() + "'.");
        }
    }
}