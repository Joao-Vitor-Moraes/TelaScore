package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Recomendacao {
	private final RecomendacaoId id;
	private final UsuarioId usuarioId;
	private final FilmeId filmeId;

	private double pontuacaoCompatibilidade;
	private final LocalDateTime dataGeracao;

	public Recomendacao(RecomendacaoId id, UsuarioId usuarioId, FilmeId filmeId, double pontuacaoCompatibilidade) {
		notNull(id, "O id da recomendação não pode ser nulo");
		notNull(usuarioId, "O id do utilizador não pode ser nulo");
		notNull(filmeId, "O id do filme não pode ser nulo");
		
		this.id = id;
		this.usuarioId = usuarioId;
		this.filmeId = filmeId;
		this.dataGeracao = LocalDateTime.now();
		
		setPontuacaoCompatibilidade(pontuacaoCompatibilidade);
	}

	public RecomendacaoId getId() { return id; }
	public UsuarioId getUsuarioId() { return usuarioId; }
	public FilmeId getFilmeId() { return filmeId; }
	public LocalDateTime getDataGeracao() { return dataGeracao; }

	public double getPontuacaoCompatibilidade() { return pontuacaoCompatibilidade; }

	public void setPontuacaoCompatibilidade(double pontuacaoCompatibilidade) {
		inclusiveBetween(0.0, 100.0, pontuacaoCompatibilidade, "A pontuação de compatibilidade deve estar entre 0 e 100");
		this.pontuacaoCompatibilidade = pontuacaoCompatibilidade;
	}
}