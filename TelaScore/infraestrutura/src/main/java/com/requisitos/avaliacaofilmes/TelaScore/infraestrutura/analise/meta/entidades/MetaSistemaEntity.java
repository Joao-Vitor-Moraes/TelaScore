package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.meta.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "meta_sistema")
public class MetaSistemaEntity {
    @Id
    private Integer id;
    @Column(nullable = false, unique = true)
    private String titulo;
    @Column(name = "quantidade_alvo", nullable = false)
    private Integer quantidadeAlvo;
    @Column(name = "duracao_dias", nullable = false)
    private Integer duracaoDias;
    @Column(name = "criada_por_usuario_id", nullable = false)
    private Integer criadaPorUsuarioId;
    @Column(nullable = false)
    private Boolean ativa;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Integer getQuantidadeAlvo() { return quantidadeAlvo; }
    public void setQuantidadeAlvo(Integer quantidadeAlvo) { this.quantidadeAlvo = quantidadeAlvo; }
    public Integer getDuracaoDias() { return duracaoDias; }
    public void setDuracaoDias(Integer duracaoDias) { this.duracaoDias = duracaoDias; }
    public Integer getCriadaPorUsuarioId() { return criadaPorUsuarioId; }
    public void setCriadaPorUsuarioId(Integer criadaPorUsuarioId) { this.criadaPorUsuarioId = criadaPorUsuarioId; }
    public Boolean getAtiva() { return ativa; }
    public void setAtiva(Boolean ativa) { this.ativa = ativa; }
}
