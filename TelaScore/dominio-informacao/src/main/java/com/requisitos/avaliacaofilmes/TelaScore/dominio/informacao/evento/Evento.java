package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Evento {
	private final EventoId id;
	private final UsuarioId criadorId;

	private String titulo;
	private String descricao;
	private LocalDateTime dataHora;
	private Visibilidade visibilidade;

	private final Set<Integer> comunidadesConvidadas = new HashSet<>();
	private final Set<Integer> convidados = new HashSet<>();
	private final Map<Integer, RespostaEvento> respostas = new HashMap<>();

	public Evento(EventoId id, UsuarioId criadorId, String titulo, LocalDateTime dataHora) {
		notNull(id, "O id do evento não pode ser nulo");
		notNull(criadorId, "O id do criador não pode ser nulo");

		this.id = id;
		this.criadorId = criadorId;
		this.visibilidade = Visibilidade.PUBLICO;

		setTitulo(titulo);
		setDataHora(dataHora);
	}

	private Evento(EventoId id, UsuarioId criadorId, String titulo, String descricao,
			LocalDateTime dataHora, Visibilidade visibilidade) {
		this.id = id;
		this.criadorId = criadorId;
		this.titulo = titulo;
		this.descricao = descricao;
		this.dataHora = dataHora;
		this.visibilidade = visibilidade != null ? visibilidade : Visibilidade.PUBLICO;
	}

	public static Evento restaurar(EventoId id, UsuarioId criadorId, String titulo, String descricao,
			LocalDateTime dataHora, Visibilidade visibilidade) {
		return new Evento(id, criadorId, titulo, descricao, dataHora, visibilidade);
	}

	public EventoId getId() { return id; }
	public UsuarioId getCriadorId() { return criadorId; }

	public void setTitulo(String titulo) {
		notNull(titulo, "O título do evento não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");
		this.titulo = titulo;
	}
	public String getTitulo() { return titulo; }

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getDescricao() { return descricao; }

	public void setDataHora(LocalDateTime dataHora) {
		notNull(dataHora, "A data e hora do evento não podem ser nulas");
		isTrue(dataHora.isAfter(LocalDateTime.now()), "A data e hora do evento devem ser no futuro");
		this.dataHora = dataHora;
	}
	public LocalDateTime getDataHora() { return dataHora; }

	public void setVisibilidade(Visibilidade visibilidade) {
		notNull(visibilidade, "A visibilidade do evento não pode ser nula");
		this.visibilidade = visibilidade;
	}
	public Visibilidade getVisibilidade() { return visibilidade; }

	public void convidarComunidade(int comunidadeId) {
		comunidadesConvidadas.add(comunidadeId);
	}
	public Set<Integer> getComunidadesConvidadas() {
		return Collections.unmodifiableSet(comunidadesConvidadas);
	}

	public void convidarUsuario(int usuarioId) {
		convidados.add(usuarioId);
	}
	public Set<Integer> getConvidados() {
		return Collections.unmodifiableSet(convidados);
	}

	public void responder(UsuarioId usuario, RespostaEvento resposta) {
		notNull(usuario, "O utilizador não pode ser nulo");
		notNull(resposta, "A resposta não pode ser nula");
		respostas.put(usuario.getId(), resposta);
	}
	public Map<Integer, RespostaEvento> getRespostas() {
		return Collections.unmodifiableMap(respostas);
	}

	public long totalConfirmados() {
		return respostas.values().stream().filter(r -> r == RespostaEvento.VAI).count();
	}
	public long totalRecusados() {
		return respostas.values().stream().filter(r -> r == RespostaEvento.NAO_VAI).count();
	}
}
