package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;

public class EditarPerfilCasoDeUso {

    private final PerfilServico perfilServico;
    private final SessaoUsuario sessaoUsuario;

    public EditarPerfilCasoDeUso(PerfilServico perfilServico, SessaoUsuario sessaoUsuario) {
        this.perfilServico = perfilServico;
        this.sessaoUsuario = sessaoUsuario;
    }

    public void executar(EditarPerfilComando comando) {
        UsuarioLogado usuarioLogado = sessaoUsuario.obterUsuarioLogado();

        if (usuarioLogado == null) {
            throw new IllegalStateException("Usuário não está logado");
        }

        perfilServico.editar(
            new PerfilId(comando.perfilId()),
            usuarioLogado.getId(),
            comando.apelido(),
            comando.biografia(),
            comando.avatarUrl()
        );
    }
}
