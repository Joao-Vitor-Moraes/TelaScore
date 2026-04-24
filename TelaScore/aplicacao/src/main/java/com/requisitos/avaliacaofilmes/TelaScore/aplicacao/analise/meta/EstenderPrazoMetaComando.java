package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import java.time.LocalDate;

public record EstenderPrazoMetaComando(
    int metaId,
    LocalDate novoPrazo
) {}