package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.conexao.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "conexao",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_conexao_seguidor_seguido",
        columnNames = {"seguidor_id", "seguido_id"}
    )
)
public class ConexaoEntity {

    @Id
    private Integer id;

    @Column(name = "seguidor_id", nullable = false)
    private Integer seguidorId;

    @Column(name = "seguido_id", nullable = false)
    private Integer seguidoId;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    public ConexaoEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSeguidorId() { return seguidorId; }
    public void setSeguidorId(Integer seguidorId) { this.seguidorId = seguidorId; }
    public Integer getSeguidoId() { return seguidoId; }
    public void setSeguidoId(Integer seguidoId) { this.seguidoId = seguidoId; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
