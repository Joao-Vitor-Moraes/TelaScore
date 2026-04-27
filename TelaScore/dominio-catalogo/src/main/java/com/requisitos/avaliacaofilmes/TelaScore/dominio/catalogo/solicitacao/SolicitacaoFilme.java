package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.solicitacao;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class SolicitacaoFilme {
    private final SolicitacaoId id;
    private final UsuarioId solicitanteId;
    
    private String tituloSugerido;
    private String justificativa;
    private StatusSolicitacao status;
    private final LocalDateTime dataCriacao;
    private String feedbackAdmin;

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
    public String getFeedbackAdmin() { return feedbackAdmin; }

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

    public void cancelar(UsuarioId usuarioQueTentouCancelar) {
        if (!this.solicitanteId.equals(usuarioQueTentouCancelar)) {
            throw new IllegalStateException("Apenas o criador pode cancelar esta solicitação");
        }
        if (this.status != StatusSolicitacao.PENDENTE && this.status != StatusSolicitacao.AGUARDANDO_AJUSTES) {
            throw new IllegalStateException("Apenas solicitações pendentes ou aguardando ajustes podem ser canceladas");
        }
        this.status = StatusSolicitacao.CANCELADA;
    }

    public void solicitarAjustes(String feedback) {
        if (this.status != StatusSolicitacao.PENDENTE) {
            throw new IllegalStateException("Apenas solicitações pendentes podem receber pedido de ajustes");
        }
        notBlank(feedback, "O feedback para o usuário não pode estar vazio");
        this.feedbackAdmin = feedback;
        this.status = StatusSolicitacao.AGUARDANDO_AJUSTES;
    }
}