package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade;

import java.time.LocalDateTime;

public record MensagemComunidade(int id, int comunidadeId, int usuarioId, String conteudo, LocalDateTime enviadoEm) {}