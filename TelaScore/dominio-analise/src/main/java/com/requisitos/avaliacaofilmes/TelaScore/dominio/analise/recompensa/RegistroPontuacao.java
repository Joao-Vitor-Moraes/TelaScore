package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa;

import java.time.LocalDateTime;
import java.util.Objects;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class RegistroPontuacao {
	private final RegistroPontuacaoId id;
	private final UsuarioId usuarioId;
	
	private final Pontos pontosGanhos;
	private final AcaoPontuada acao;
	private final LocalDateTime dataRegistro;

	public RegistroPontuacao(RegistroPontuacaoId id, UsuarioId usuarioId, Pontos pontosGanhos, AcaoPontuada acao) {
		Objects.requireNonNull(id, "O id do registo não pode ser nulo");
		Objects.requireNonNull(usuarioId, "O id do utilizador não pode ser nulo");
		Objects.requireNonNull(pontosGanhos, "Os pontos não podem ser nulos");
		Objects.requireNonNull(acao, "A ação pontuada não pode ser nula");
		
		this.id = id;
		this.usuarioId = usuarioId;
		this.pontosGanhos = pontosGanhos;
		this.acao = acao;
		this.dataRegistro = LocalDateTime.now();
	}

	public RegistroPontuacaoId getId() { return id; }
	public UsuarioId getUsuarioId() { return usuarioId; }
	public Pontos getPontosGanhos() { return pontosGanhos; }
	public AcaoPontuada getAcao() { return acao; }
	public LocalDateTime getDataRegistro() { return dataRegistro; }
}