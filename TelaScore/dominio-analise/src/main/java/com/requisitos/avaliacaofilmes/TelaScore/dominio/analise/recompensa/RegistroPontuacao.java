package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa;

import java.time.LocalDateTime;
import java.util.Objects;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class RegistroPontuacao {
    private final RegistroPontuacaoId id;
    private final UsuarioId usuarioId;
    private final Pontos pontosGanhos;
    private final AcaoPontuada acao;
    private final LocalDateTime dataRegistro;

    public RegistroPontuacao(RegistroPontuacaoId id, UsuarioId usuarioId, Pontos pontosGanhos, AcaoPontuada acao) {
        this(id, usuarioId, pontosGanhos, acao, LocalDateTime.now());
    }

    public RegistroPontuacao(RegistroPontuacaoId id, UsuarioId usuarioId, Pontos pontosGanhos, AcaoPontuada acao,
            LocalDateTime dataRegistro) {
        Objects.requireNonNull(id, "O id do registro nao pode ser nulo");
        Objects.requireNonNull(usuarioId, "O id do usuario nao pode ser nulo");
        Objects.requireNonNull(pontosGanhos, "Os pontos nao podem ser nulos");
        Objects.requireNonNull(acao, "A acao pontuada nao pode ser nula");
        Objects.requireNonNull(dataRegistro, "A data do registro nao pode ser nula");

        this.id = id;
        this.usuarioId = usuarioId;
        this.pontosGanhos = pontosGanhos;
        this.acao = acao;
        this.dataRegistro = dataRegistro;
    }

    public RegistroPontuacaoId getId() { return id; }
    public UsuarioId getUsuarioId() { return usuarioId; }
    public Pontos getPontosGanhos() { return pontosGanhos; }
    public AcaoPontuada getAcao() { return acao; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
}
