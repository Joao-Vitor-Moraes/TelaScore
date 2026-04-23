package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.servico;

public interface GeradorId {
	int gerarProximoIdUsuario();
	int gerarProximoIdPerfil();

	int gerarProximoIdFilme();
	int gerarProximoIdAvaliacao();
}