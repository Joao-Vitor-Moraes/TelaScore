package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.temporaria;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDateTime;

public class Mensagem {
    private final MensagemId id;
    private final UsuarioId remetenteId;
    private final UsuarioId destinatarioId;
    private final String conteudo;
    private final LocalDateTime enviadaEm;
    private boolean lida;

    public Mensagem(MensagemId id, UsuarioId remetenteId, UsuarioId destinatarioId, String conteudo) {
        notNull(id, "ID da mensagem é obrigatório");
        notNull(remetenteId, "Remetente é obrigatório");
        notNull(destinatarioId, "Destinatário é obrigatório");
        notBlank(conteudo, "O conteúdo da mensagem não pode ser vazio");
        
        this.id = id;
        this.remetenteId = remetenteId;
        this.destinatarioId = destinatarioId;
        this.conteudo = conteudo;
        this.enviadaEm = LocalDateTime.now();
        this.lida = false;
    }

    MensagemId getId() {
        return id;
    }

    UsuarioId getRemetenteId() {
        return remetenteId;
    }

    UsuarioId getDestinatarioId() {
        return destinatarioId;
    }

    String getConteudo() {
        return conteudo;
    }

    LocalDateTime getEnviadaEm() {
        return enviadaEm;
    }

    boolean isLida() {
        return lida;
    }

    void setLida(boolean lida) {
        this.lida = lida;
    }

    public void marcarComoLida() {
        this.lida = true;
    }
}