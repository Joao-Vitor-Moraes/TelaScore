package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recompensa.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registro_pontuacao")
public class RegistroPontuacaoEntity {

    @Id
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private int usuarioId;  // ← era String, agora é int

    @Column(nullable = false)
    private Integer pontos;

    @Column(name = "tipo_acao", nullable = false)
    private String tipoAcao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    public RegistroPontuacaoEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }  // ← era String

    public Integer getPontos() { return pontos; }
    public void setPontos(Integer pontos) { this.pontos = pontos; }

    public String getTipoAcao() { return tipoAcao; }
    public void setTipoAcao(String tipoAcao) { this.tipoAcao = tipoAcao; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}