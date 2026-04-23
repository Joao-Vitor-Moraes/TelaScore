package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

public interface GeradorId {
	int gerarProximoIdUsuario();
	int gerarProximoIdPerfil();

	int gerarProximoIdFilme();
	int gerarProximoIdAvaliacao();

	int gerarProximoIdMeta();
	int gerarProximoIdRegistroPontuacao();

	int gerarProximoIdEvento();
	int gerarProximoIdNoticia();

	int gerarProximoIdConexao();
	int gerarProximoIdComunidade();
	int gerarProximoIdMensagem();
}