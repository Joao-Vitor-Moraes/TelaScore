package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import java.time.LocalDateTime;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;

public class ListarEventosFuturosCasoDeUso {

    private final EventoRepositorio repositorio;

    public ListarEventosFuturosCasoDeUso(EventoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<EventoResumo> executar(LocalDateTime aPartirDe) {
        LocalDateTime referencia = (aPartirDe != null) ? aPartirDe : LocalDateTime.now();

        return repositorio.buscarEventosFuturos(referencia).stream()
                .map(evento -> new EventoResumo(
                        evento.getId().getId(),
                        evento.getCriadorId().getId(),
                        evento.getTitulo(),
                        evento.getDescricao(),
                        evento.getDataHora()))
                .toList();
    }
}
