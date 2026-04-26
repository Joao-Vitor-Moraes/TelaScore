package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Perfil;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ConsultarPerfilIdUsuarioCasoDeUso {

    private final PerfilServico perfilServico;

    public ConsultarPerfilIdUsuarioCasoDeUso(PerfilServico perfilServico) {
        this.perfilServico = perfilServico;
    }

    public Perfil executar(ConsultarPerfilIdUsuarioComando comando) {
        return perfilServico.obterPorUsuario(new UsuarioId(comando.usuarioId()));
    }
}
