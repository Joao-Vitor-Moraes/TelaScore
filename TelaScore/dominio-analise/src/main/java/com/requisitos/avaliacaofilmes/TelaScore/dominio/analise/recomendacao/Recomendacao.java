package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.isTrue;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Recomendacao {
    private final RecomendacaoId id;
    private final UsuarioId usuarioId; 
    private final FilmeId filmeId;
    
    private final UsuarioId remetenteId; 
    private String mensagem;

    private double pontuacaoCompatibilidade;
    private final LocalDateTime dataGeracao;
    private StatusRecomendacao status;

    public Recomendacao(RecomendacaoId id, UsuarioId usuarioId, FilmeId filmeId, double pontuacaoCompatibilidade, UsuarioId remetenteId, String mensagem) {
        notNull(id, "O id da recomendação não pode ser nulo");
        notNull(usuarioId, "O id do utilizador não pode ser nulo");
        notNull(filmeId, "O id do filme não pode ser nulo");
        
        if (remetenteId != null) {
            isTrue(!remetenteId.equals(usuarioId), "O remetente não pode ser o mesmo que o destinatário");
        }
        
        this.id = id;
        this.usuarioId = usuarioId;
        this.filmeId = filmeId;
        this.remetenteId = remetenteId; 
        this.dataGeracao = LocalDateTime.now();
        this.status = StatusRecomendacao.PENDENTE;
        
        setPontuacaoCompatibilidade(pontuacaoCompatibilidade);
        setMensagem(mensagem);
    }

    public Recomendacao(RecomendacaoId id, UsuarioId usuarioId, FilmeId filmeId, double pontuacaoCompatibilidade) {
        this(id, usuarioId, filmeId, pontuacaoCompatibilidade, null, null);
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

    public boolean ehSocial() {
        return remetenteId != null;
    }

    public RecomendacaoId getId() { return id; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public FilmeId getFilmeId() { return filmeId; }
    public UsuarioId getRemetenteId() { return remetenteId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public String getMensagem() { return mensagem; }
    public StatusRecomendacao getStatus() { return status; }
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
}