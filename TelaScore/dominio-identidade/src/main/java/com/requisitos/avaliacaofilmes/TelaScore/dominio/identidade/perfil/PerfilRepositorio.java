package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface PerfilRepositorio {
	void salvar(Perfil perfil);
	Perfil obter(PerfilId id);
	Perfil obterPorUsuario(UsuarioId usuarioId);
}