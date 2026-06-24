package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.CategoriaNoticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaServico;

public class AdicionarNoticiaCasoDeUso {
    private final NoticiaServico servico;
    private final GeradorId geradorId;

    public AdicionarNoticiaCasoDeUso(NoticiaServico servico, GeradorId geradorId) {
        this.servico = servico;
        this.geradorId = geradorId;
    }

    public void executar(AdicionarNoticiaComando comando) {
        NoticiaId novoId = new NoticiaId(geradorId.gerarProximoIdNoticia());
        UsuarioId autorId = new UsuarioId(comando.autorId());
        CategoriaNoticia categoria = CategoriaNoticia.valueOf(comando.categoria().toUpperCase());

        Noticia noticia = new Noticia(novoId, autorId, comando.titulo(), comando.conteudo(), categoria, comando.filmeId());

        servico.publicarNoticia(noticia);
    }
}
