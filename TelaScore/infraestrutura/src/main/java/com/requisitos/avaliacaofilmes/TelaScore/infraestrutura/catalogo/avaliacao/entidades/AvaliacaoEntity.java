package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.catalogo.avaliacao.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "avaliacao")
public class AvaliacaoEntity {

    @Id
    private Integer id;

    @Column(name = "filme_id", nullable = false)
    private String filmeId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    // Nota armazenada como inteiro — o VO Nota é reconstruído no mapeamento
    @Column(nullable = false)
    private Integer nota;

    // Resenha é opcional no domínio
    @Column(columnDefinition = "TEXT")
    private String resenha;

    @Column(name = "data_avaliacao", nullable = false)
    private LocalDate dataAvaliacao;

    public AvaliacaoEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFilmeId() { return filmeId; }
    public void setFilmeId(String filmeId) { this.filmeId = filmeId; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getResenha() { return resenha; }
    public void setResenha(String resenha) { this.resenha = resenha; }

    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }

    @Column(nullable = false)
    private String visibilidade = "PUBLICA";

    public String getVisibilidade() { return visibilidade; }
    public void setVisibilidade(String visibilidade) { this.visibilidade = visibilidade; }
}