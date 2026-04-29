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
	private final LocalDateTime dataCriacao;
	private StatusDenuncia status;

	public Denuncia(DenunciaId id, UsuarioId denuncianteId, int alvoId, TipoAlvoDenuncia tipoAlvo,
			MotivoDenuncia motivo, String descricao) {
		notNull(id, "O id da denúncia não pode ser nulo");
		notNull(denuncianteId, "O denunciante não pode ser nulo");
		isTrue(alvoId > 0, "O alvo da denúncia deve ser positivo");
		notNull(tipoAlvo, "O tipo do alvo não pode ser nulo");
		notNull(motivo, "O motivo da denúncia não pode ser nulo");
		notBlank(descricao, "A descrição da denúncia não pode estar em branco");

		this.id = id;
		this.denuncianteId = denuncianteId;
		this.alvoId = alvoId;
		this.tipoAlvo = tipoAlvo;
		this.motivo = motivo;
		this.descricao = descricao;
		this.dataCriacao = LocalDateTime.now();
		this.status = StatusDenuncia.PENDENTE;
	}

	public DenunciaId getId() { return id; }
	public UsuarioId getDenuncianteId() { return denuncianteId; }
	public int getAlvoId() { return alvoId; }
	public TipoAlvoDenuncia getTipoAlvo() { return tipoAlvo; }
	public MotivoDenuncia getMotivo() { return motivo; }
	public String getDescricao() { return descricao; }
	public LocalDateTime getDataCriacao() { return dataCriacao; }
	public StatusDenuncia getStatus() { return status; }

	public void colocarEmAnalise() {
		isTrue(status == StatusDenuncia.PENDENTE, "Apenas denúncias pendentes podem entrar em análise");
		this.status = StatusDenuncia.EM_ANALISE;
	}

	public void aceitar() {
		isTrue(status == StatusDenuncia.PENDENTE || status == StatusDenuncia.EM_ANALISE,
				"Apenas denúncias pendentes ou em análise podem ser aceitas");
		this.status = StatusDenuncia.ACEITA;
	}

	public void rejeitar() {
		isTrue(status == StatusDenuncia.PENDENTE || status == StatusDenuncia.EM_ANALISE,
				"Apenas denúncias pendentes ou em análise podem ser rejeitadas");
		this.status = StatusDenuncia.REJEITADA;
	}
}
