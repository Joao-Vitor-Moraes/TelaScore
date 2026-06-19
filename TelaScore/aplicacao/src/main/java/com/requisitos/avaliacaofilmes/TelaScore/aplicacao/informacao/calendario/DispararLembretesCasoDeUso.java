package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario;

import java.time.LocalDate;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioServico;

public class DispararLembretesCasoDeUso {

    private final CalendarioServico servico;

    public DispararLembretesCasoDeUso(CalendarioServico servico) {
        this.servico = servico;
    }

    public void executar(int usuarioId, LocalDate dataReferencia) {
        servico.dispararLembretes(new UsuarioId(usuarioId), dataReferencia);
    }
}
