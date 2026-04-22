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
    
    private final UsuarioId remetenteId; 
    private String mensagem;

    private double pontuacaoCompatibilidade;
    private final LocalDateTime dataGeracao;

    public Recomendacao(RecomendacaoId id, UsuarioId usuarioId, FilmeId filmeId, double pontuacaoCompatibilidade, UsuarioId remetenteId, String mensagem) {
        notNull(id, "O id da recomendação não pode ser nulo");
        notNull(usuarioId, "O id do utilizador não pode ser nulo");
        notNull(filmeId, "O id do filme não pode ser nulo");
        
        this.id = id;
        this.usuarioId = usuarioId;
        this.filmeId = filmeId;
        this.remetenteId = remetenteId; 
        this.dataGeracao = LocalDateTime.now();
        
        setPontuacaoCompatibilidade(pontuacaoCompatibilidade);
        setMensagem(mensagem);
    }

    public Recomendacao(RecomendacaoId id, UsuarioId usuarioId, FilmeId filmeId, double pontuacaoCompatibilidade) {
        this(id, usuarioId, filmeId, pontuacaoCompatibilidade, null, null);
    }

    public RecomendacaoId getId() { return id; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public FilmeId getFilmeId() { return filmeId; }
    public UsuarioId getRemetenteId() { return remetenteId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public String getMensagem() { return mensagem; }

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

    public boolean ehSocial() {
        return remetenteId != null;
    }
}