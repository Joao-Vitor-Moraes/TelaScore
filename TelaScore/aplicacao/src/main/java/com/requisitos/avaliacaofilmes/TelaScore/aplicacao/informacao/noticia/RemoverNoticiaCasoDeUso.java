package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaServico;

public class RemoverNoticiaCasoDeUso {
    private final NoticiaServico servico;

    public RemoverNoticiaCasoDeUso(NoticiaServico servico) {
        this.servico = servico;
    }

    public void executar(int id) {
        servico.excluirNoticia(new NoticiaId(id));
    }
}
