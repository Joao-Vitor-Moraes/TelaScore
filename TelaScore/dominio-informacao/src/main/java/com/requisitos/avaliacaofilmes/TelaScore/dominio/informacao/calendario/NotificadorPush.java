package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class NotificadorPush implements ObservadorEstreia {

	@Override
	public void estreiaChegou(UsuarioId usuarioId, EntradaCalendario entrada) {
		System.out.println("[PUSH] Usuário " + usuarioId + ": o filme "
				+ entrada.getFilmeId() + " estreia em "
				+ entrada.getDataEstreiaPrevista() + "!");
	}
}
