package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Senha;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class RemoverUsuariosSteps {

    private UsuarioRepositorio usuarioRepositorio;
    private UsuarioServico usuarioServico;
    private PerfilRepositorio perfilRepositorio;
    private PerfilServico perfilServico;
    private RemoverUsuarioCasoDeUso removerUsuarioCasoDeUso;

    private UsuarioId usuarioIdSolicitado;
    private Exception excecaoCapturada;

    private void prepararCasoDeUso() {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);
        perfilRepositorio = mock(PerfilRepositorio.class);
        perfilServico = new PerfilServico(perfilRepositorio);
        removerUsuarioCasoDeUso = new RemoverUsuarioCasoDeUso(usuarioServico, perfilServico);
        usuarioIdSolicitado = null;
        excecaoCapturada = null;
    }

    @Dado("que o administrador da remoção tem ID {int}")
    public void que_o_administrador_da_remocao_tem_id(Integer id) {
        prepararCasoDeUso();

        UsuarioId administradorId = new UsuarioId(id);
        Usuario administrador = new Usuario(
            administradorId,
            "Administrador",
            new Email("admin@admin.com"),
            new Senha("123456"),
            PapelUsuario.ADMIN
        );

        when(usuarioRepositorio.obter(administradorId)).thenReturn(administrador);
    }

    @Dado("que o usuário comum da remoção tem ID {int}")
    public void que_o_usuario_comum_da_remocao_tem_id(Integer id) {
        prepararCasoDeUso();

        UsuarioId usuarioId = new UsuarioId(id);
        Usuario usuario = new Usuario(
            usuarioId,
            "Gabriel Reis",
            new Email("grmp@cesar.school"),
            new Senha("123456"),
            PapelUsuario.CINEFILO
        );

        when(usuarioRepositorio.obter(usuarioId)).thenReturn(usuario);
    }

    @Dado("que não existe administrador da remoção com ID {int}")
    public void que_nao_existe_administrador_da_remocao_com_id(Integer id) {
        prepararCasoDeUso();

        UsuarioId administradorId = new UsuarioId(id);
        when(usuarioRepositorio.obter(administradorId)).thenReturn(null);
    }

    @E("existe um usuário alvo cadastrado com ID {int}")
    public void existe_um_usuario_alvo_cadastrado_com_id(Integer id) {
        UsuarioId usuarioId = new UsuarioId(id);
        Usuario usuario = new Usuario(
            usuarioId,
            "Gabriel Pires",
            new Email("gp@gmail.com"),
            new Senha("123456"),
            PapelUsuario.CINEFILO
        );

        when(usuarioRepositorio.obter(usuarioId)).thenReturn(usuario);
    }

    @E("não existe usuário alvo cadastrado com ID {int}")
    public void nao_existe_usuario_alvo_cadastrado_com_id(Integer id) {
        UsuarioId usuarioId = new UsuarioId(id);
        when(usuarioRepositorio.obter(usuarioId)).thenReturn(null);
    }

    @Quando("o administrador da remoção com ID {int} solicita remover o usuário com ID {int}")
    public void o_administrador_da_remocao_com_id_solicita_remover_o_usuario_com_id(Integer administradorId, Integer usuarioId) {
        solicitarRemocao(administradorId, usuarioId);
    }

    @Quando("o usuário da remoção com ID {int} solicita remover o usuário com ID {int}")
    public void o_usuario_da_remocao_com_id_solicita_remover_o_usuario_com_id(Integer usuarioExecutorId, Integer usuarioId) {
        solicitarRemocao(usuarioExecutorId, usuarioId);
    }

    private void solicitarRemocao(Integer administradorId, Integer usuarioId) {
        try {
            usuarioIdSolicitado = new UsuarioId(usuarioId);
            RemoverUsuarioComando comando = new RemoverUsuarioComando(usuarioId, administradorId);
            removerUsuarioCasoDeUso.executar(comando);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("a remoção deve ser realizada com sucesso")
    public void a_remocao_deve_ser_realizada_com_sucesso() {
        assertNull(excecaoCapturada);
        verify(usuarioRepositorio, times(1)).remover(usuarioIdSolicitado);
    }

    @E("o perfil do usuário removido também deve ser removido")
    public void o_perfil_do_usuario_removido_tambem_deve_ser_removido() {
        verify(perfilRepositorio, times(1)).removerPorUsuario(usuarioIdSolicitado);
    }

    @Então("a remoção deve ser rejeitada")
    public void a_remocao_deve_ser_rejeitada() {
        assertNotNull(excecaoCapturada);
        verify(usuarioRepositorio, never()).remover(usuarioIdSolicitado);
        verify(perfilRepositorio, never()).removerPorUsuario(usuarioIdSolicitado);
    }

    @E("deve retornar o erro da remoção {string}")
    public void deve_retornar_o_erro_da_remocao(String mensagemErro) {
        assertNotNull(excecaoCapturada);
        assertEquals(mensagemErro, excecaoCapturada.getMessage());
    }
}
