package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.noticia;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface NoticiaRepositorio {
	void salvar(Noticia noticia);
	Noticia obter(NoticiaId id);
	void remover(NoticiaId id);
	
	List<Noticia> buscarRecentes(int limite);
	
	List<Noticia> buscarPorAutor(UsuarioId autorId);
}