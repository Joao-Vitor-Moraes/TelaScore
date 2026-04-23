package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.servico;

public interface GeradorId {
	int gerarProximoIdUsuario();
	int gerarProximoIdPerfil();

	int gerarProximoIdFilme();
	int gerarProximoIdAvaliacao();

	int gerarProximoIdMeta();
	int gerarProximoIdRegistroPontuacao();

	int gerarProximoIdEvento();
	int gerarProximoIdNoticia();
}