package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao;

import static org.apache.commons.lang3.Validate.notNull;

public class ConexaoServico {
	private final ConexaoRepositorio repositorio;

	public ConexaoServico(ConexaoRepositorio repositorio) {
		notNull(repositorio, "O repositório de conexões não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void seguirUsuario(Conexao novaConexao) {
		notNull(novaConexao, "A conexão não pode ser nula");
		
		Conexao conexaoExistente = repositorio.buscarConexao(novaConexao.getSeguidorId(), novaConexao.getSeguidoId());
		
		if (conexaoExistente != null) {
			throw new IllegalStateException("O utilizador já segue este perfil.");
		}
		
		repositorio.salvar(novaConexao);
	}
	
	public void deixarDeSeguir(ConexaoId id) {
		notNull(id, "O id da conexão não pode ser nulo");
		repositorio.remover(id);
	}
}