package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.Comunidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeServico;

public class CriarComunidadeCasoDeUso {

	private final ComunidadeServico comunidadeServico;
	private final GeradorId geradorId;

	public CriarComunidadeCasoDeUso(ComunidadeServico comunidadeServico, GeradorId geradorId) {
		this.comunidadeServico = comunidadeServico;
		this.geradorId = geradorId;
	}

	public void executar(CriarComunidadeComando comando) {
		UsuarioId criadorId = new UsuarioId(comando.criadorId());
		ComunidadeId novaComunidadeId = new ComunidadeId(geradorId.gerarProximoIdComunidade());
		
		Comunidade comunidade = new Comunidade(novaComunidadeId, comando.nome(), comando.descricao());
		
		// O serviço de domínio salva a comunidade E vincula o criador como "CRIADOR"
		comunidadeServico.criarComunidade(comunidade, criadorId);
	}
}