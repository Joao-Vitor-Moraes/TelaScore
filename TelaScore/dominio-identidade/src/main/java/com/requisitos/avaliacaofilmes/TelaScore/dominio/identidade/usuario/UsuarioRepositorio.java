package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

import java.util.List;

public interface UsuarioRepositorio {
	
	void salvar(Usuario usuario);
	
	Usuario obter(UsuarioId id);
	
	Usuario obterPorEmail(Email email);

	void remover(UsuarioId id);

	List<Usuario> listarTodos();

}