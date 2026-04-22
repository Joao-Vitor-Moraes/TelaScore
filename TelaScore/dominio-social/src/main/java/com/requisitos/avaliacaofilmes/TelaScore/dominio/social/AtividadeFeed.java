package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.feed;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import java.time.LocalDateTime;

public record AtividadeFeed(
    UsuarioId autorId,
    TipoAtividade tipo, 
    String descricaoAmigavel, 
    String linkReferencia,
    LocalDateTime momentoAcontecimento
) {}