package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario;

public interface UsuarioRepositorio {
	
	void salvar(Usuario usuario);
	
	Usuario obter(UsuarioId id);
	
}