package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class ListarUsuarioSteps {

    private UsuarioRepositorio usuarioRepositorio;
    private UsuarioServico usuarioServico;
    private ListarUsuariosCasoDeUso listarUsuariosCasoDeUso;

    private List<Usuario> usuariosRetornados;
    private Exception excecaoCapturada;

    private void prepararCasoDeUso() {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);
        listarUsuariosCasoDeUso = new ListarUsuariosCasoDeUso(usuarioServico);
        usuariosRetornados = null;
        excecaoCapturada = null;
    }

    @Dado("que o usuário com ID {int} é um administrador")
    public void que_o_usuario_com_id_e_um_administrador(Integer id) {
        prepararCasoDeUso();

        UsuarioId administradorId = new UsuarioId(id);
        Usuario administrador = new Usuario(
            administradorId,
            "Administrador",
            new Email("admin@admin.com"),
            PapelUsuario.ADMIN
        );

        when(usuarioRepositorio.obter(administradorId)).thenReturn(administrador);
    }

    @Dado("que o usuário com ID {int} é um cinefilo")
    public void que_o_usuario_com_id_e_um_cinefilo(Integer id) {
        prepararCasoDeUso();

        UsuarioId usuarioId = new UsuarioId(id);
        Usuario usuario = new Usuario(
            usuarioId,
            "Gabriel Reis",
            new Email("grmp@cesar.school"),
            PapelUsuario.CINEFILO
        );

        when(usuarioRepositorio.obter(usuarioId)).thenReturn(usuario);
    }

    @Dado("que não existe usuário cadastrado com ID {int}")
    public void que_nao_existe_usuario_cadastrado_com_id(Integer id) {
        prepararCasoDeUso();

        UsuarioId usuarioId = new UsuarioId(id);
        when(usuarioRepositorio.obter(usuarioId)).thenReturn(null);
    }

    @E("existem usuários cadastrados na plataforma")
    public void existem_usuarios_cadastrados_na_plataforma() {
        Usuario usuario1 = new Usuario(
            new UsuarioId(1),
            "Gabriel Pires",
            new Email("gp@gmail.com"),
            PapelUsuario.CINEFILO
        );

        Usuario usuario2 = new Usuario(
            new UsuarioId(2),
            "Pires",
            new Email("Pires@gmail.com"),
            PapelUsuario.CINEFILO
        );

        when(usuarioRepositorio.listarTodos()).thenReturn(List.of(usuario1, usuario2));
    }

    @Quando("o administrador com ID {int} solicita a listagem de usuários")
    public void o_administrador_com_id_solicita_a_listagem_de_usuarios(Integer administradorId) {
        solicitarListagem(administradorId);
    }

    @Quando("o usuário com ID {int} solicita a listagem de usuários")
    public void o_usuario_com_id_solicita_a_listagem_de_usuarios(Integer usuarioId) {
        solicitarListagem(usuarioId);
    }

    private void solicitarListagem(Integer administradorId) {
        try {
            ListarUsuariosComando comando = new ListarUsuariosComando(administradorId);
            usuariosRetornados = listarUsuariosCasoDeUso.executar(comando);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("a listagem deve ser realizada com sucesso")
    public void a_listagem_deve_ser_realizada_com_sucesso() {
        assertNull(excecaoCapturada);
        assertNotNull(usuariosRetornados);
    }

    @E("devem ser retornados {int} usuários")
    public void devem_ser_retornados_usuarios(Integer quantidadeEsperada) {
        assertNotNull(usuariosRetornados);
        assertEquals(quantidadeEsperada, usuariosRetornados.size());
    }

    @Então("a listagem deve ser rejeitada")
    public void a_listagem_deve_ser_rejeitada() {
        assertNotNull(excecaoCapturada);
    }

    @E("deve retornar o erro da listagem {string}")
    public void deve_retornar_o_erro_da_listagem(String mensagemErro) {
        assertNotNull(excecaoCapturada);
        assertEquals(mensagemErro, excecaoCapturada.getMessage());
    }
}
