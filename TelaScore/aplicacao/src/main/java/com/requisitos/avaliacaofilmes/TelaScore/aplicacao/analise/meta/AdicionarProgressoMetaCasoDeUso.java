package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template.AtualizadorMetaTemplate;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template.AtualizadorMetaComNotificacao;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template.AtualizadorMetaSilencioso;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;

public class AdicionarProgressoMetaCasoDeUso {
    private final AtualizadorMetaTemplate atualizadorComFeedback;
    private final AtualizadorMetaTemplate atualizadorSilencioso;

    public AdicionarProgressoMetaCasoDeUso(AtualizadorMetaTemplate atualizador) {
        this.atualizadorComFeedback = atualizador;
        this.atualizadorSilencioso = atualizador;
    }

    public AdicionarProgressoMetaCasoDeUso(
            AtualizadorMetaComNotificacao atualizadorComFeedback,
            AtualizadorMetaSilencioso atualizadorSilencioso) {
        this.atualizadorComFeedback = atualizadorComFeedback;
        this.atualizadorSilencioso = atualizadorSilencioso;
    }

    public ResultadoAtualizacaoMeta executar(AdicionarProgressoMetaComando comando) {
        AtualizadorMetaTemplate atualizador = comando.isSilencioso()
                ? atualizadorSilencioso
                : atualizadorComFeedback;
        return atualizador.executarAtualizacao(
                new MetaId(comando.getMetaId()), comando.getQuantidade());
    }
}
