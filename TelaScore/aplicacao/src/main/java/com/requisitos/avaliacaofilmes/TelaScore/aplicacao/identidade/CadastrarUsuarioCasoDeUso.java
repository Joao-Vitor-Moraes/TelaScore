package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Perfil;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Senha;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

public class CadastrarUsuarioCasoDeUso {

    private final UsuarioServico usuarioServico;
    private final PerfilServico perfilServico;
    private final GeradorId geradorId;

    public CadastrarUsuarioCasoDeUso(
        UsuarioServico usuarioServico,
        PerfilServico perfilServico,
        GeradorId geradorId
    ) {
        this.usuarioServico = usuarioServico;
        this.perfilServico = perfilServico;
        this.geradorId = geradorId;
    }

    public void executar(CadastrarUsuarioComando comando) {
        Email email = new Email(comando.email());

        if (usuarioServico.obterPorEmail(email) != null) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail");
        }

        UsuarioId usuarioId = new UsuarioId(geradorId.gerarProximoIdUsuario());

        Usuario usuario = new Usuario(
            usuarioId,
            comando.nome(),
            email,
            new Senha(comando.senha()),
            PapelUsuario.CINEFILO
        );

        usuarioServico.salvar(usuario);

        Perfil perfil = new Perfil(
            new PerfilId(geradorId.gerarProximoIdPerfil()),
            usuarioId,
            new Apelido(usuario.getNome())
        );

        perfilServico.salvar(perfil);
    }
}
