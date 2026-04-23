package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.servico;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.servico.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.dto.SeguirUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.Conexao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoServico;

public class SeguirUsuarioCasoDeUso {

	private final ConexaoServico conexaoServico;
	private final GeradorId geradorId;

	public SeguirUsuarioCasoDeUso(ConexaoServico conexaoServico, GeradorId geradorId) {
		this.conexaoServico = conexaoServico;
		this.geradorId = geradorId;
	}

	public void executar(SeguirUsuarioComando comando) {
		UsuarioId seguidorId = new UsuarioId(comando.seguidorId());
		UsuarioId seguidoId = new UsuarioId(comando.seguidoId());
		
		ConexaoId novaConexaoId = new ConexaoId(geradorId.gerarProximoIdConexao());
		
		// A regra de não seguir a si próprio é validada dentro do "new Conexao"
		Conexao conexao = new Conexao(novaConexaoId, seguidorId, seguidoId);
		
		// O serviço de domínio verifica se o seguidor já segue o seguido antes de salvar
		conexaoServico.seguirUsuario(conexao);
	}
}