package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface MensagemRepositorio {
	void salvar(Mensagem mensagem);
	Mensagem obter(MensagemId id);
	void remover(MensagemId id);
	
	List<Mensagem> buscarConversa(UsuarioId usuarioA, UsuarioId usuarioB);
	
	int contarMensagensNaoLidas(UsuarioId destinatarioId);
}