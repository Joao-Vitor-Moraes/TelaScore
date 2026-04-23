package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.temporaria;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Comunidade {
    private final ComunidadeId id;
    private final String nome;
    private final String descricao;
    private final UsuarioId criadorId;
    private final LocalDateTime dataFundacao;
    private final Set<UsuarioId> membros; 

    public Comunidade(ComunidadeId id, String nome, String descricao, UsuarioId criadorId) {
        notNull(id, "ID da comunidade não pode ser nulo");
        notBlank(nome, "O nome da comunidade é obrigatório");
        notNull(criadorId, "O criador é obrigatório");

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.criadorId = criadorId;
        this.dataFundacao = LocalDateTime.now();
        this.membros = new HashSet<>();
        this.membros.add(criadorId); 
    }

    public void adicionarMembro(UsuarioId novoMembro) {
        notNull(novoMembro, "Utilizador não pode ser nulo");
        this.membros.add(novoMembro);
    }

    public void removerMembro(UsuarioId membro, UsuarioId solicitante) {
        if (!solicitante.equals(this.criadorId) && !solicitante.equals(membro)) {
            throw new SocialException("Não tem permissão para remover este membro");
        }
        this.membros.remove(membro);
    }

    ComunidadeId getId() {
        return id;
    }

    String getNome() {
        return nome;
    }

    String getDescricao() {
        return descricao;
    }

    UsuarioId getCriadorId() {
        return criadorId;
    }

    LocalDateTime getDataFundacao() {
        return dataFundacao;
    }

    Set<UsuarioId> getMembros() {
        return Collections.unmodifiableSet(membros);
    }
}