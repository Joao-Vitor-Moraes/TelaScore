package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaServico;

public class AdicionarNoticiaCasoDeUso {
    private final NoticiaServico servico;

    public AdicionarNoticiaCasoDeUso(NoticiaServico servico) {
        this.servico = servico;
    }

    public void executar(AdicionarNoticiaComando comando) {
        NoticiaId novoId = new NoticiaId((int) (Math.random() * 10000)); // Simulação de ID
        UsuarioId autorId = new UsuarioId(comando.autorId());
        CategoriaNoticia categoria = CategoriaNoticia.valueOf(comando.categoria().toUpperCase());

        Noticia noticia = new Noticia(novoId, autorId, comando.titulo(), comando.conteudo(), categoria);

        servico.publicarNoticia(noticia);
    }
}