package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario;

import java.time.LocalDate;
import java.util.List;

public record CalendarioResumo(int usuarioId, List<EntradaCalendarioResumo> entradas) {

    public record EntradaCalendarioResumo(String filmeId, LocalDate dataEstreiaPrevista, boolean lembreteAtivo) {}
}
