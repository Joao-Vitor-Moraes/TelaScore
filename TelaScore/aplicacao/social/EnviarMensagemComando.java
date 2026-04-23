package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.dto;

public record EnviarMensagemComando(
		int remetenteId,
		int destinatarioId,
		String conteudo
) {}