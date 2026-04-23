package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade;

import static org.apache.commons.lang3.Validate.notNull;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ComunidadeServico {
	private final ComunidadeRepositorio repositorio;

	public ComunidadeServico(ComunidadeRepositorio repositorio) {
		notNull(repositorio, "O repositório de comunidade não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void criarComunidade(Comunidade comunidade, UsuarioId criadorId) {
		notNull(comunidade, "A comunidade não pode ser nula");
		notNull(criadorId, "O id do criador não pode ser nulo");
		
		repositorio.salvarComunidade(comunidade);
		
		MembroComunidade dono = new MembroComunidade(comunidade.getId(), criadorId, PapelComunidade.CRIADOR);
		repositorio.salvarMembro(dono);
	}
	
	public void entrarNaComunidade(ComunidadeId comunidadeId, UsuarioId usuarioId) {
		notNull(comunidadeId, "O id da comunidade não pode ser nulo");
		notNull(usuarioId, "O id do utilizador não pode ser nulo");
		
		MembroComunidade novoMembro = new MembroComunidade(comunidadeId, usuarioId, PapelComunidade.MEMBRO);
		repositorio.salvarMembro(novoMembro);
	}
}