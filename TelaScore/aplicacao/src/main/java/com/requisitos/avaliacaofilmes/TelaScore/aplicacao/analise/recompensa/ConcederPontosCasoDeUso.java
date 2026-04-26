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
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());

		RegistroPontuacaoId novoRegistroId = new RegistroPontuacaoId(geradorId.gerarProximoIdRegistroPontuacao());

		Pontos pontos = new Pontos(comando.quantidadePontos());

		RegistroPontuacao registro = new RegistroPontuacao(novoRegistroId, usuarioId, pontos, comando.acao());

		pontuacaoServico.concederPontos(registro);
	}
}