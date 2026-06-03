package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
public class MensagemEntity {
    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    // Gerador comentado para rodar caso de test de infraestrutura !!

    @Column(name = "remetente_id", nullable = false)
    private int remetenteId;
    
    @Column(name = "destinatario_id", nullable = false)
    private int destinatarioId;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;
    
    @Column(nullable = false)
    private boolean lida;
    
    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    public MensagemEntity() {}

    public MensagemEntity(int id, int remetenteId, int destinatarioId, String conteudo, boolean lida, LocalDateTime dataEnvio) {
        this.id = id;
        this.remetenteId = remetenteId;
        this.destinatarioId = destinatarioId;
        this.conteudo = conteudo;
        this.lida = lida;
        this.dataEnvio = dataEnvio;
    }

    // Getters e Setters
    public int getId() { return id; }
    public int getRemetenteId() { return remetenteId; }
    public int getDestinatarioId() { return destinatarioId; }
    public String getConteudo() { return conteudo; }
    public boolean isLida() { return lida; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
}
