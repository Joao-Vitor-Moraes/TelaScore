package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.evento;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class EventoSocial {
    private final EventoId id;
    private final String titulo;
    private final FilmeId filmeEmDestaque; 
    private final UsuarioId organizadorId;
    private final LocalDateTime dataHoraEvento;
    private final Set<UsuarioId> confirmados;

    public EventoSocial(EventoId id, String titulo, FilmeId filmeEmDestaque, UsuarioId organizadorId, LocalDateTime dataHoraEvento) {
        notNull(id, "ID do evento é obrigatório");
        notNull(titulo, "Título do evento é obrigatório");
        notNull(dataHoraEvento, "A data e hora do evento são obrigatórias");
        
        if (dataHoraEvento.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível criar um evento no passado.");
        }

        this.id = id;
        this.titulo = titulo;
        this.filmeEmDestaque = filmeEmDestaque;
        this.organizadorId = organizadorId;
        this.dataHoraEvento = dataHoraEvento;
        this.confirmados = new HashSet<>();
        this.confirmados.add(organizadorId);
    }

    public void confirmarPresenca(UsuarioId usuarioId) {
        this.confirmados.add(usuarioId);
    }
}