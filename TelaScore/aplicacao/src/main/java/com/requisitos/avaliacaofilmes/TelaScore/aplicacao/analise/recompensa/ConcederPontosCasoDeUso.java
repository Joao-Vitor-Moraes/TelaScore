package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.PontuacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ConcederPontosCasoDeUso {

	private final PontuacaoServico pontuacaoServico;
	private final GeradorId geradorId;

	public ConcederPontosCasoDeUso(RegistroPontuacaoRepositorio repositorio, GeradorId geradorId) {
		this.pontuacaoServico = new PontuacaoServico(repositorio);
		this.geradorId = geradorId;
	}

	public void executar(ConcederPontosComando comando) {
		// 1. Converte o ID simples que veio da tela para o Objeto de Valor do Domínio
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());

		// 2. Pede um novo ID para o registro de pontuação
		RegistroPontuacaoId novoRegistroId = new RegistroPontuacaoId(geradorId.gerarProximoIdRegistroPontuacao());

		// 3. Cria os objetos de valor (as regras de negócio são garantidas aqui)
		Pontos pontos = new Pontos(comando.quantidadePontos());

		// 4. Cria o registro de pontuação
		RegistroPontuacao registro = new RegistroPontuacao(novoRegistroId, usuarioId, pontos, comando.acao());

		// 5. Delega ao serviço de domínio que salva no banco de dados
		pontuacaoServico.concederPontos(registro);
	}
}