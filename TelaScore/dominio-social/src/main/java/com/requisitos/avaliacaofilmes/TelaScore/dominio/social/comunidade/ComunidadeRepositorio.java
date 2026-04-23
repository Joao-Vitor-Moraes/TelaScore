package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface ComunidadeRepositorio {
	void salvarComunidade(Comunidade comunidade);
	Comunidade obterComunidade(ComunidadeId id);
	
	void salvarMembro(MembroComunidade membro);
	void removerMembro(ComunidadeId comunidadeId, UsuarioId usuarioId);
	
	List<Comunidade> buscarComunidadesDoUsuario(UsuarioId usuarioId);
	
	List<MembroComunidade> buscarMembrosDaComunidade(ComunidadeId comunidadeId);
}