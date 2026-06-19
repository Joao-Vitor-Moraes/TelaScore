package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import java.util.List;

public interface MetaSistemaRepositorio {
    void salvar(MetaSistema meta);
    List<MetaSistema> listarAtivas();
    int proximoId();
}
