package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.EntradaCalendario;

public class RegistrarFilmeNoCalendarioCasoDeUso {

    private final CriarCalendarioCasoDeUso criarCalendario;
    private final CalendarioServico servico;

    public RegistrarFilmeNoCalendarioCasoDeUso(CriarCalendarioCasoDeUso criarCalendario, CalendarioServico servico) {
        this.criarCalendario = criarCalendario;
        this.servico = servico;
    }

    public void executar(RegistrarFilmeComando comando) {
        // Garante que o utilizador possua um calendário antes de registar o filme.
        criarCalendario.executar(comando.usuarioId());

        UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
        EntradaCalendario entrada = new EntradaCalendario(new FilmeId(comando.filmeId()), comando.dataEstreia());

        servico.registrarFilmeNoCalendario(usuarioId, entrada);
    }
}
