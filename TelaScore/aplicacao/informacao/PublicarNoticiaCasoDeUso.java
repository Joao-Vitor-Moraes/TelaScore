package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.servico;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.dto.PublicarNoticiaComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.servico.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.Noticia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia.NoticiaRepositorio;

public class PublicarNoticiaCasoDeUso {

	private final NoticiaRepositorio noticiaRepositorio;
	private final GeradorId geradorId;

	public PublicarNoticiaCasoDeUso(NoticiaRepositorio noticiaRepositorio, GeradorId geradorId) {
		this.noticiaRepositorio = noticiaRepositorio;
		this.geradorId = geradorId;
	}

	public void executar(PublicarNoticiaComando comando) {
		UsuarioId autorId = new UsuarioId(comando.autorId());
		NoticiaId novaNoticiaId = new NoticiaId(geradorId.gerarProximoIdNoticia());
		
		Noticia noticia = new Noticia(novaNoticiaId, autorId, comando.titulo(), comando.conteudo());
		
		noticiaRepositorio.salvar(noticia);
	}
}