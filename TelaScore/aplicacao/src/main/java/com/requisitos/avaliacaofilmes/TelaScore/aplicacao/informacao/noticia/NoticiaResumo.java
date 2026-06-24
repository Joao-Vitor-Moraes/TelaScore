package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.noticia;

import java.time.LocalDateTime;

public record NoticiaResumo(
        int id,
        String titulo,
        String conteudo,
        int autorId,
        String autorApelido,
        LocalDateTime dataPublicacao,
        String categoria,
        String filmeId,
        String filmeTitulo,
        String filmeImagemUrl
) {}
