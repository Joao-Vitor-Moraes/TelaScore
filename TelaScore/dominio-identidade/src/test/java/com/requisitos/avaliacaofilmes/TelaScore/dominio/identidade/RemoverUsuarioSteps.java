package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class RemoverUsuarioSteps {

    private UsuarioRepositorio usuarioRepositorio;
    private UsuarioServico usuarioServico;
    private Exception excecaoCapturada;

    @Dado("que existe um usuário cadastrado com ID {int}")
    public void que_existe_um_usuario_cadastrado_com_id(Integer id) {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);

        UsuarioId usuarioId = new UsuarioId(id);
        Usuario usuario = new Usuario(
            usuarioId,
            "Gabriel Reis",
            new Email("grmp@cesar.school"),
            PapelUsuario.CINEFILO
        );

        when(usuarioRepositorio.obter(usuarioId)).thenReturn(usuario);
        excecaoCapturada = null;
    }

    @Dado("que não existe usuário cadastrado com ID {int}")
    public void que_nao_existe_usuario_cadastrado_com_id(Integer id) {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);

        UsuarioId usuarioId = new UsuarioId(id);
        when(usuarioRepositorio.obter(usuarioId)).thenReturn(null);
        excecaoCapturada = null;
    }

    @Quando("solicito a remoção do usuário com ID {int}")
    public void solicito_a_remocao_do_usuario_com_id(Integer id) {
        try {
            usuarioServico.remover(new UsuarioId(id));
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o usuário deve ser removido com sucesso")
    public void o_usuario_deve_ser_removido_com_sucesso() {
        assertNull(excecaoCapturada, "Nenhuma exceção deveria ter sido lançada");
        verify(usuarioRepositorio, times(1)).remover(new UsuarioId(1));
    }

    @Então("o sistema deve bloquear a remoção informando que o usuário não existe")
    public void o_sistema_deve_bloquear_a_remocao_informando_que_o_usuario_nao_existe() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada");
        assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass());
        assertEquals("O usuário informado não existe", excecaoCapturada.getMessage());
        verify(usuarioRepositorio, never()).remover(new UsuarioId(99));
    }
}
