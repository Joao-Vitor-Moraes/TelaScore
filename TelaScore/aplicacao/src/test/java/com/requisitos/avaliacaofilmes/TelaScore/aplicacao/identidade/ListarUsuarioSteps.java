package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Senha;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioServico;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class ListarUsuarioSteps {

    private UsuarioRepositorio usuarioRepositorio;
    private UsuarioServico usuarioServico;
    private SessaoUsuario sessaoUsuario;
    private ListarUsuariosCasoDeUso listarUsuariosCasoDeUso;

    private List<Usuario> usuariosRetornados;
    private Exception excecaoCapturada;

    private void prepararCasoDeUso() {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);
        sessaoUsuario = new SessaoUsuario();
        listarUsuariosCasoDeUso = new ListarUsuariosCasoDeUso(usuarioServico, sessaoUsuario);
        usuariosRetornados = null;
        excecaoCapturada = null;
    }

    @Dado("que existe um administrador logado para listagem com ID {int}")
    public void que_existe_um_administrador_logado_para_listagem_com_id(Integer id) {
        prepararCasoDeUso();
        sessaoUsuario.iniciar(new UsuarioLogado(new UsuarioId(id), PapelUsuario.ADMIN));
    }

    @Dado("que existe um usuário comum logado para listagem com ID {int}")
    public void que_existe_um_usuario_comum_logado_para_listagem_com_id(Integer id) {
        prepararCasoDeUso();
        sessaoUsuario.iniciar(new UsuarioLogado(new UsuarioId(id), PapelUsuario.CINEFILO));
    }

    @Dado("que não há usuário logado para listagem")
    public void que_nao_ha_usuario_logado_para_listagem() {
        prepararCasoDeUso();
    }

    @E("existem usuários cadastrados na plataforma para listagem")
    public void existem_usuarios_cadastrados_na_plataforma_para_listagem() {
        Usuario usuario1 = new Usuario(
            new UsuarioId(1),
            "Gabriel Pires",
            new Email("gp@gmail.com"),
            new Senha("123456"),
            PapelUsuario.CINEFILO
        );

        Usuario usuario2 = new Usuario(
            new UsuarioId(2),
            "Pires",
            new Email("pires@gmail.com"),
            new Senha("123456"),
            PapelUsuario.CINEFILO
        );

        when(usuarioRepositorio.listarTodos()).thenReturn(List.of(usuario1, usuario2));
    }

    @Quando("solicito a listagem de usuários")
    public void solicito_a_listagem_de_usuarios() {
        try {
            usuariosRetornados = listarUsuariosCasoDeUso.executar(new ListarUsuariosComando());
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
