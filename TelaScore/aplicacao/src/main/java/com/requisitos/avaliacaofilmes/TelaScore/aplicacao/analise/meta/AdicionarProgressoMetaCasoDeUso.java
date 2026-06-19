package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta.template.AtualizadorMetaTemplate;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;

public class AdicionarProgressoMetaCasoDeUso {
    private final AtualizadorMetaTemplate atualizador;

    public AdicionarProgressoMetaCasoDeUso(AtualizadorMetaTemplate atualizador) {
        this.atualizador = atualizador;
    }

    public ResultadoAtualizacaoMeta executar(AdicionarProgressoMetaComando comando) {
        return atualizador.executarAtualizacao(
                new MetaId(comando.getMetaId()), comando.getQuantidade());
    }
}
