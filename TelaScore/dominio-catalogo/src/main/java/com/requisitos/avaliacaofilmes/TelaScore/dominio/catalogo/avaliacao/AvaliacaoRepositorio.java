package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface AvaliacaoRepositorio {
	void salvar(Avaliacao avaliacao);
	
	Avaliacao obter(AvaliacaoId id);
	
	List<Avaliacao> pesquisarPorFilme(FilmeId filmeId);
	List<Avaliacao> pesquisarPorUsuario(UsuarioId usuarioId);
}