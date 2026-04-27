package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class CriarComunidadeComando {
    private final int idSugerido;
    private final String nome;
    private final String descricao;
    private final UsuarioId criadorId;

    public CriarComunidadeComando(int idSugerido, String nome, String descricao, UsuarioId criadorId) {
        this.idSugerido = idSugerido;
        this.nome = nome;
        this.descricao = descricao;
        this.criadorId = criadorId;
    }

    public int getIdSugerido() { return idSugerido; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public UsuarioId getCriadorId() { return criadorId; }
}