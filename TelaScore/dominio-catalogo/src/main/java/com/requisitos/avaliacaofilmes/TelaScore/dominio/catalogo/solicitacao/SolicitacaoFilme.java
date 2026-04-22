package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class SolicitacaoFilme {
	private final SolicitacaoId id;
	private final UsuarioId solicitanteId;
	
	private String tituloSugerido;
	private String justificativa;
	private StatusSolicitacao status;
	private final LocalDateTime dataCriacao;

	public SolicitacaoFilme(SolicitacaoId id, UsuarioId solicitanteId, String tituloSugerido) {
		notNull(id, "O id da solicitação não pode ser nulo");
		notNull(solicitanteId, "O id do solicitante não pode ser nulo");
		notNull(tituloSugerido, "O título sugerido não pode ser nulo");
		notBlank(tituloSugerido, "O título sugerido não pode estar em branco");
		
		this.id = id;
		this.solicitanteId = solicitanteId;
		this.status = StatusSolicitacao.PENDENTE;
		this.dataCriacao = LocalDateTime.now();
		this.tituloSugerido = tituloSugerido;
	}

	public SolicitacaoId getId() { return id; }
	public UsuarioId getSolicitanteId() { return solicitanteId; }
	public StatusSolicitacao getStatus() { return status; }
	public LocalDateTime getDataCriacao() { return dataCriacao; }

	public void setTituloSugerido(String titulo) {
		notNull(titulo, "O título sugerido não pode ser nulo");
		notBlank(titulo, "O título sugerido não pode estar em branco");
		this.tituloSugerido = titulo;
	}

	public String getTituloSugerido() { return tituloSugerido; }

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public String getJustificativa() { return justificativa; }

	public void aprovar() {
		this.status = StatusSolicitacao.APROVADA;
	}

	public void rejeitar() {
		this.status = StatusSolicitacao.REJEITADA;
	}
}