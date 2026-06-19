package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario;

import java.time.LocalDate;

public record RegistrarFilmeComando(int usuarioId, String filmeId, LocalDate dataEstreia) {}
