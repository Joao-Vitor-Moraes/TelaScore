package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class CalendarioServico {
	private final CalendarioRepositorio repositorio;
	private final List<ObservadorEstreia> observadores;

	public CalendarioServico(CalendarioRepositorio repositorio, ObservadorEstreia... observadores) {
		notNull(repositorio, "O repositório de calendário não pode ser nulo");
		this.repositorio = repositorio;
		this.observadores = Arrays.asList(observadores);
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

	public void dispararLembretes(UsuarioId usuarioId, LocalDate dataReferencia) {
		notNull(usuarioId, "O utilizador é obrigatório");
		notNull(dataReferencia, "A data de referência não pode ser nula");

		CalendarioEstreia calendario = repositorio.obterPorUsuario(usuarioId);

		if (calendario != null) {
			for (ObservadorEstreia observador : observadores) {
				calendario.adicionarObservador(observador);
			}
			calendario.dispararLembretesDoDia(dataReferencia);
		}
	}
}
