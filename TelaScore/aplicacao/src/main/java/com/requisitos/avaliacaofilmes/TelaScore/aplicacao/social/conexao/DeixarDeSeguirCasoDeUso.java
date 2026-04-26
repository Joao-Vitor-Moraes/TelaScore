package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.Conexao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class DeixarDeSeguirCasoDeUso {

	private final ConexaoServico conexaoServico;
	private final ConexaoRepositorio repositorio;

	public DeixarDeSeguirCasoDeUso(ConexaoRepositorio repositorio) {
		this.conexaoServico = new ConexaoServico(repositorio);
		this.repositorio = repositorio;
	}

	public void executar(DeixarDeSeguirComando comando) {
		// 1. Converte os IDs simples para Objetos de Valor do Domínio
		UsuarioId seguidorId = new UsuarioId(comando.seguidorId());
		UsuarioId seguidoId = new UsuarioId(comando.seguidoId());

		// 2. Busca a conexão existente entre os dois usuários
		Conexao conexao = repositorio.buscarConexao(seguidorId, seguidoId);

		if (conexao == null) {
			throw new IllegalStateException("Não existe conexão entre estes utilizadores.");
		}

		// 3. Delega ao serviço de domínio que remove a conexão
		conexaoServico.deixarDeSeguir(conexao.getId());
	}
}