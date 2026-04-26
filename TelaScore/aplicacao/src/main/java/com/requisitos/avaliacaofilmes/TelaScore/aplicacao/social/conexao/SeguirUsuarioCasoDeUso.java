package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.Conexao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class SeguirUsuarioCasoDeUso {

	private final ConexaoServico conexaoServico;
	private final GeradorId geradorId;

	public SeguirUsuarioCasoDeUso(ConexaoRepositorio repositorio, GeradorId geradorId) {
		this.conexaoServico = new ConexaoServico(repositorio);
		this.geradorId = geradorId;
	}

	public void executar(SeguirUsuarioComando comando) {
		// 1. Converte os IDs simples para Objetos de Valor do Domínio
		UsuarioId seguidorId = new UsuarioId(comando.seguidorId());
		UsuarioId seguidoId = new UsuarioId(comando.seguidoId());

		// 2. Gera um novo ID para a conexão
		ConexaoId novaConexaoId = new ConexaoId(geradorId.gerarProximoIdConexao());

		// 3. Cria a Conexão (regras como "não pode seguir a si mesmo" são garantidas aqui)
		Conexao novaConexao = new Conexao(novaConexaoId, seguidorId, seguidoId);

		// 4. Delega ao serviço de domínio que valida duplicata e salva
		conexaoServico.seguirUsuario(novaConexao);
	}
}