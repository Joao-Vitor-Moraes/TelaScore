package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface ConexaoRepositorio {
	void salvar(Conexao conexao);
	void remover(ConexaoId id);
	
	Conexao buscarConexao(UsuarioId seguidorId, UsuarioId seguidoId);
	
	List<Conexao> buscarSeguidoresDe(UsuarioId usuarioId);
	List<Conexao> buscarSeguidosPor(UsuarioId usuarioId); 
}