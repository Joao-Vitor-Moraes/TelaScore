package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.denuncia.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
		name = "denuncia",
		uniqueConstraints = @UniqueConstraint(
				name = "uk_denuncia_denunciante_alvo",
				columnNames = {"denunciante_id", "tipo_alvo", "alvo_id"}))
public class DenunciaEntity {

	@Id
	private Integer id;

	@Column(name = "denunciante_id", nullable = false)
	private Integer denuncianteId;

	@Column(name = "alvo_id", nullable = false)
	private Integer alvoId;

	@Column(name = "tipo_alvo", nullable = false)
	private String tipoAlvo;

	@Column(nullable = false)
	private String motivo;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String descricao;

	@Column(name = "link_ocorrencia", length = 500)
	private String linkOcorrencia;

	@Column(name = "data_criacao", nullable = false)
	private LocalDateTime dataCriacao;

	@Column(nullable = false)
	private String status;

	public Integer getId() { return id; }
	public void setId(Integer id) { this.id = id; }
	public Integer getDenuncianteId() { return denuncianteId; }
	public void setDenuncianteId(Integer denuncianteId) { this.denuncianteId = denuncianteId; }
	public Integer getAlvoId() { return alvoId; }
	public void setAlvoId(Integer alvoId) { this.alvoId = alvoId; }
	public String getTipoAlvo() { return tipoAlvo; }
	public void setTipoAlvo(String tipoAlvo) { this.tipoAlvo = tipoAlvo; }
	public String getMotivo() { return motivo; }
	public void setMotivo(String motivo) { this.motivo = motivo; }
	public String getDescricao() { return descricao; }
	public void setDescricao(String descricao) { this.descricao = descricao; }
	public String getLinkOcorrencia() { return linkOcorrencia; }
	public void setLinkOcorrencia(String linkOcorrencia) { this.linkOcorrencia = linkOcorrencia; }
	public LocalDateTime getDataCriacao() { return dataCriacao; }
	public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
