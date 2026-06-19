package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Denuncia {
	private final DenunciaId id;
	private final UsuarioId denuncianteId;
	private final int alvoId;
	private final TipoAlvoDenuncia tipoAlvo;
	private final MotivoDenuncia motivo;
	private final String descricao;
	private final String linkOcorrencia;
	private final LocalDateTime dataCriacao;
	private StatusDenuncia status;

	public Denuncia(DenunciaId id, UsuarioId denuncianteId, int alvoId, TipoAlvoDenuncia tipoAlvo,
			MotivoDenuncia motivo, String descricao) {
		this(id, denuncianteId, alvoId, tipoAlvo, motivo, descricao, null);
	}

	public Denuncia(DenunciaId id, UsuarioId denuncianteId, int alvoId, TipoAlvoDenuncia tipoAlvo,
			MotivoDenuncia motivo, String descricao, String linkOcorrencia) {
		this(id, denuncianteId, alvoId, tipoAlvo, motivo, descricao, linkOcorrencia,
				LocalDateTime.now(), StatusDenuncia.PENDENTE);
	}

	public Denuncia(DenunciaId id, UsuarioId denuncianteId, int alvoId, TipoAlvoDenuncia tipoAlvo,
			MotivoDenuncia motivo, String descricao, String linkOcorrencia,
			LocalDateTime dataCriacao, StatusDenuncia status) {
		notNull(id, "O id da denuncia nao pode ser nulo");
		notNull(denuncianteId, "O denunciante nao pode ser nulo");
		isTrue(alvoId > 0, "O alvo da denuncia deve ser positivo");
		notNull(tipoAlvo, "O tipo do alvo nao pode ser nulo");
		notNull(motivo, "O motivo da denuncia nao pode ser nulo");
		notBlank(descricao, "A descricao da denuncia nao pode estar em branco");
		notNull(dataCriacao, "A data de criacao da denuncia nao pode ser nula");
		notNull(status, "O status da denuncia nao pode ser nulo");

		this.id = id;
		this.denuncianteId = denuncianteId;
		this.alvoId = alvoId;
		this.tipoAlvo = tipoAlvo;
		this.motivo = motivo;
		this.descricao = descricao;
		this.linkOcorrencia = normalizarLink(linkOcorrencia);
		this.dataCriacao = dataCriacao;
		this.status = status;
	}

	public DenunciaId getId() { return id; }
	public UsuarioId getDenuncianteId() { return denuncianteId; }
	public int getAlvoId() { return alvoId; }
	public TipoAlvoDenuncia getTipoAlvo() { return tipoAlvo; }
	public MotivoDenuncia getMotivo() { return motivo; }
	public String getDescricao() { return descricao; }
	public String getLinkOcorrencia() { return linkOcorrencia; }
	public LocalDateTime getDataCriacao() { return dataCriacao; }
	public StatusDenuncia getStatus() { return status; }

	public void colocarEmAnalise() {
		isTrue(status == StatusDenuncia.PENDENTE, "Apenas denuncias pendentes podem entrar em analise");
		this.status = StatusDenuncia.EM_ANALISE;
	}

	public void aceitar() {
		isTrue(status == StatusDenuncia.PENDENTE || status == StatusDenuncia.EM_ANALISE,
				"Apenas denuncias pendentes ou em analise podem ser aceitas");
		this.status = StatusDenuncia.ACEITA;
	}

	public void rejeitar() {
		isTrue(status == StatusDenuncia.PENDENTE || status == StatusDenuncia.EM_ANALISE,
				"Apenas denuncias pendentes ou em analise podem ser rejeitadas");
		this.status = StatusDenuncia.REJEITADA;
	}

	private String normalizarLink(String linkOcorrencia) {
		if (linkOcorrencia == null || linkOcorrencia.isBlank()) {
			return null;
		}

		String link = linkOcorrencia.trim();
		isTrue(link.length() <= 500, "O link da ocorrencia deve ter no maximo 500 caracteres");
		return link;
	}
}
