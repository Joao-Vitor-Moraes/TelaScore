package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import static org.apache.commons.lang3.Validate.notNull;

public class CalendarioServico {
	private final CalendarioRepositorio repositorio;

	public CalendarioServico(CalendarioRepositorio repositorio) {
		notNull(repositorio, "O repositório de calendário não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void registrarFilmeNoCalendario(UsuarioId usuarioId, EntradaCalendario entrada) {
		notNull(usuarioId, "O utilizador é obrigatório");
		notNull(entrada, "A entrada do filme é obrigatória");

		CalendarioEstreia calendario = repositorio.obterPorUsuario(usuarioId);
		
		if (calendario != null) {
			calendario.adicionarFilme(entrada);
			repositorio.salvar(calendario);
		}
	}
}