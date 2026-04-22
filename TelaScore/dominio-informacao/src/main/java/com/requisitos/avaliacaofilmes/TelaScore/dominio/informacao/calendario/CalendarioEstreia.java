package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class CalendarioEstreia {
	private final CalendarioId id;
	private final UsuarioId usuarioId;
	private final List<EntradaCalendario> entradas;

	public CalendarioEstreia(CalendarioId id, UsuarioId usuarioId) {
		notNull(id, "O id do calendário não pode ser nulo");
		notNull(usuarioId, "O id do utilizador não pode ser nulo");
		
		this.id = id;
		this.usuarioId = usuarioId;
		this.entradas = new ArrayList<>();
	}

	public CalendarioId getId() { return id; }
	public UsuarioId getUsuarioId() { return usuarioId; }

	public List<EntradaCalendario> getEntradas() {
		return Collections.unmodifiableList(entradas);
	}

	public void adicionarFilme(EntradaCalendario novaEntrada) {
		notNull(novaEntrada, "A entrada não pode ser nula");

		boolean jaExiste = false;
		for (EntradaCalendario entrada : entradas) {
			if (entrada.getFilmeId().equals(novaEntrada.getFilmeId())) {
				jaExiste = true;
				break;
			}
		}

		if (!jaExiste) {
			this.entradas.add(novaEntrada);
		}
	}

	public void removerFilme(FilmeId filmeId) {
		for (int i = entradas.size() - 1; i >= 0; i--) {
			if (entradas.get(i).getFilmeId().equals(filmeId)) {
				entradas.remove(i);
			}
		}
	}
}