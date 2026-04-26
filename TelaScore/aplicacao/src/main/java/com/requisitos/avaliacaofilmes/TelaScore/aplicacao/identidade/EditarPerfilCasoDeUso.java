package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class EditarPerfilCasoDeUso {

    private final PerfilServico perfilServico;

    public EditarPerfilCasoDeUso(PerfilServico perfilServico) {
        this.perfilServico = perfilServico;
    }

    public void executar(EditarPerfilComando comando) {
        perfilServico.editar(
            new PerfilId(comando.perfilId()),
            new UsuarioId(comando.usuarioId()),
            comando.apelido(),
            comando.biografia(),
            comando.avatarUrl()
        );
    }
}
