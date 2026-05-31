package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface ObservadorEstreia {

	void estreiaChegou(UsuarioId usuarioId, EntradaCalendario entrada);
}
