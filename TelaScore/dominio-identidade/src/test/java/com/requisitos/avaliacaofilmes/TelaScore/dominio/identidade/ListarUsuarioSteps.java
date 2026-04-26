package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class ListarUsuarioSteps {

    private UsuarioRepositorio usuarioRepositorio;
    private UsuarioServico usuarioServico;

    private List<Usuario> usuariosRetornados;

    @Dado("que existem usuários cadastrados no repositório")
    public void que_existem_usuarios_cadastrados_no_repositorio() {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);

        Usuario usuario1 = new Usuario(
            new UsuarioId(1),
            "Gabriel Reis",
            new Email("grmp@cesar.school"),
            PapelUsuario.CINEFILO
        );

        Usuario usuario2 = new Usuario(
            new UsuarioId(2),
            "Gabriel Pires",
            new Email("13grmp@gmail.com"),
            PapelUsuario.CINEFILO
        );

        when(usuarioRepositorio.listarTodos()).thenReturn(List.of(usuario1, usuario2));
    }

    @Dado("que não existem usuários cadastrados no repositório")
    public void que_nao_existem_usuarios_cadastrados_no_repositorio() {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);

        when(usuarioRepositorio.listarTodos()).thenReturn(List.of());
    }

    @Quando("solicito a listagem de usuários")
    public void solicito_a_listagem_de_usuarios() {
        usuariosRetornados = usuarioServico.listarTodos();
    }

    @Então("devem ser retornados {int} usuários cadastrados")
    public void devem_ser_retornados_usuarios_cadastrados(Integer quantidadeEsperada) {
        assertNotNull(usuariosRetornados);
        assertEquals(quantidadeEsperada, usuariosRetornados.size());
    }

    @Então("deve ser retornada uma lista vazia")
    public void deve_ser_retornada_uma_lista_vazia() {
        assertNotNull(usuariosRetornados);
        assertEquals(0, usuariosRetornados.size());
    }
}
