package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.isTrue;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Recomendacao {
    private final RecomendacaoId id;
    private final UsuarioId usuarioId; 
    private final String conteudoId; 
    private final TipoConteudo tipoConteudo; 
    
    private final UsuarioId remetenteId; 
    private String mensagem;

    private double pontuacaoCompatibilidade;
    private final LocalDateTime dataGeracao;
    private StatusRecomendacao status;
    private String comentarioResposta;

    public Recomendacao(RecomendacaoId id, UsuarioId usuarioId, String conteudoId, TipoConteudo tipoConteudo, double pontuacaoCompatibilidade, UsuarioId remetenteId, String mensagem) {
        notNull(id, "O id da recomendação não pode ser nulo");
        notNull(usuarioId, "O id do utilizador não pode ser nulo");
        notNull(conteudoId, "O id do conteúdo não pode ser nulo");
        notNull(tipoConteudo, "O tipo de conteúdo não pode ser nulo");
        
        if (remetenteId != null) {
            isTrue(!remetenteId.equals(usuarioId), "O remetente não pode ser o mesmo que o destinatário");
        }
        
        this.id = id;
        this.usuarioId = usuarioId;
        this.conteudoId = conteudoId;
        this.tipoConteudo = tipoConteudo;
        this.remetenteId = remetenteId; 
        this.dataGeracao = LocalDateTime.now();
        this.status = StatusRecomendacao.PENDENTE;
        
        setPontuacaoCompatibilidade(pontuacaoCompatibilidade);
        setMensagem(mensagem);
    }

    public Recomendacao(RecomendacaoId id, UsuarioId usuarioId, String conteudoId, TipoConteudo tipoConteudo, double pontuacaoCompatibilidade) {
        this(id, usuarioId, conteudoId, tipoConteudo, pontuacaoCompatibilidade, null, null);
    }

    public Recomendacao(RecomendacaoId id, UsuarioId usuarioId, String conteudoId, TipoConteudo tipoConteudo,
                        Double pontuacaoCompatibilidade, UsuarioId remetenteId, String mensagem, LocalDateTime dataGeracao,
                        StatusRecomendacao status, String comentarioResposta) {
        notNull(id, "O id da recomendação não pode ser nulo");
        notNull(usuarioId, "O id do utilizador não pode ser nulo");
        notNull(conteudoId, "O id do conteúdo não pode ser nulo");
        notNull(tipoConteudo, "O tipo de conteúdo não pode ser nulo");
        notNull(dataGeracao, "A data de geração não pode ser nula");
        notNull(status, "O status não pode ser nulo");

        this.id = id;
        this.usuarioId = usuarioId;
        this.conteudoId = conteudoId;
        this.tipoConteudo = tipoConteudo;
        this.pontuacaoCompatibilidade = pontuacaoCompatibilidade;
        this.remetenteId = remetenteId;
        this.mensagem = mensagem;
        this.dataGeracao = dataGeracao;
        this.status = status;
        setComentarioResposta(comentarioResposta);
    }

	public void marcarComoVisualizada() {
        if (this.status == StatusRecomendacao.PENDENTE) {
            this.status = StatusRecomendacao.VISUALIZADA;
        }
    }

    public void aceitar() {
        if (this.status == StatusRecomendacao.REJEITADA) {
            throw new IllegalStateException("Não é possível aceitar uma recomendação que já foi rejeitada.");
        }
        this.status = StatusRecomendacao.ACEITA;
    }

    public void rejeitar() {
        this.status = StatusRecomendacao.REJEITADA;
    }

    public void responder(StatusRecomendacao resposta, String comentario) {
        notNull(resposta, "A resposta da recomendação não pode ser nula");
        isTrue(
            resposta == StatusRecomendacao.VOU_ASSISTIR
                || resposta == StatusRecomendacao.JA_ASSISTI
                || resposta == StatusRecomendacao.SEM_INTERESSE,
            "Resposta inválida para a recomendação"
        );
        this.status = resposta;
        setComentarioResposta(comentario);
    }

    public boolean ehSocial() {
        return remetenteId != null;
    }

    public RecomendacaoId getId() { return id; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public String getConteudoId() { return conteudoId; }
    public TipoConteudo getTipoConteudo() { return tipoConteudo; }
    public UsuarioId getRemetenteId() { return remetenteId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public String getMensagem() { return mensagem; }
    public StatusRecomendacao getStatus() { return status; }
    public String getComentarioResposta() { return comentarioResposta; }
    public double getPontuacaoCompatibilidade() { return pontuacaoCompatibilidade; }

    public void setPontuacaoCompatibilidade(double pontuacaoCompatibilidade) {
        inclusiveBetween(0.0, 100.0, pontuacaoCompatibilidade, "A pontuação de compatibilidade deve estar entre 0 e 100");
        this.pontuacaoCompatibilidade = pontuacaoCompatibilidade;
    }

    public void setMensagem(String mensagem) {
        if (mensagem != null) {
            inclusiveBetween(0, 255, mensagem.length(), "A mensagem deve ter no máximo 255 caracteres");
        }
        this.mensagem = mensagem;
    }

    public void setComentarioResposta(String comentarioResposta) {
        if (comentarioResposta != null) {
            inclusiveBetween(0, 255, comentarioResposta.length(), "O comentário da resposta deve ter no máximo 255 caracteres");
        }
        this.comentarioResposta = comentarioResposta == null || comentarioResposta.isBlank()
                ? null
                : comentarioResposta.trim();
    }
}
