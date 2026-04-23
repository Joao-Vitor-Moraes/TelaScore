package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem;

import static org.apache.commons.lang3.Validate.notNull;

public class MensagemServico {
	private final MensagemRepositorio repositorio;

	public MensagemServico(MensagemRepositorio repositorio) {
		notNull(repositorio, "O repositório de mensagens não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void enviarMensagem(Mensagem mensagem) {
		notNull(mensagem, "A mensagem não pode ser nula");
		
		repositorio.salvar(mensagem);
	}
	
	public void lerMensagem(Mensagem mensagem) {
		notNull(mensagem, "A mensagem não pode ser nula");
		mensagem.marcarComoLida();
		repositorio.salvar(mensagem);
	}
}