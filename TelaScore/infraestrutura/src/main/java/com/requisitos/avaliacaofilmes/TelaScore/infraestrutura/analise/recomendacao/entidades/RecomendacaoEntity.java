package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.analise.recomendacao.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "recomendacao")
public class RecomendacaoEntity {

    @Id
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "conteudo_id", nullable = false)
    private String conteudoId;

    @Column(name = "tipo_conteudo", nullable = false)
    private String tipoConteudo;

    @Column(name = "pontuacao_compatibilidade")
    private Double pontuacaoCompatibilidade;

    @Column(name = "remetente_id")
    private Integer remetenteId;

    @Column(columnDefinition = "TEXT")
    private String mensagem;

    @Column(name = "data_geracao", nullable = false)
    private LocalDateTime dataGeracao;

    @Column(nullable = false)
    private String status;

    @Column(name = "comentario_resposta", length = 255)
    private String comentarioResposta;

    @Column(name = "nota_posterior")
    private Integer notaPosterior;

    @Column(name = "avaliacao_posterior", length = 500)
    private String avaliacaoPosterior;

    public RecomendacaoEntity() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getConteudoId() { return conteudoId; }
    public void setConteudoId(String conteudoId) { this.conteudoId = conteudoId; }
    public String getTipoConteudo() { return tipoConteudo; }
    public void setTipoConteudo(String tipoConteudo) { this.tipoConteudo = tipoConteudo; }
    public Double getPontuacaoCompatibilidade() { return pontuacaoCompatibilidade; }
    public void setPontuacaoCompatibilidade(Double pc) { this.pontuacaoCompatibilidade = pc; }
    public Integer getRemetenteId() { return remetenteId; }
    public void setRemetenteId(Integer remetenteId) { this.remetenteId = remetenteId; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComentarioResposta() { return comentarioResposta; }
    public void setComentarioResposta(String comentarioResposta) { this.comentarioResposta = comentarioResposta; }
    public Integer getNotaPosterior() { return notaPosterior; }
    public void setNotaPosterior(Integer notaPosterior) { this.notaPosterior = notaPosterior; }
    public String getAvaliacaoPosterior() { return avaliacaoPosterior; }
    public void setAvaliacaoPosterior(String avaliacaoPosterior) { this.avaliacaoPosterior = avaliacaoPosterior; }
}
