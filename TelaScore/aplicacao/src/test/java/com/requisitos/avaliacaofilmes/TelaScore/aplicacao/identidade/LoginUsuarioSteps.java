package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

public class LoginUsuarioSteps {

    private UsuarioRepositorio usuarioRepositorio;
    private UsuarioServico usuarioServico;
    private SessaoUsuario sessaoUsuario;
    private LoginUsuarioCasoDeUso loginUsuarioCasoDeUso;

    private UsuarioLogado usuarioLogado;
    private Exception excecaoCapturada;

    private void prepararCasoDeUso() {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);
        sessaoUsuario = new SessaoUsuario();
        loginUsuarioCasoDeUso = new LoginUsuarioCasoDeUso(usuarioServico, sessaoUsuario);
        usuarioLogado = null;
        excecaoCapturada = null;
    }

    @Dado("que existe um usuário {string} cadastrado com e-mail {string} e senha {string}")
    public void que_existe_um_usuario_cadastrado_com_email_e_senha(
        String papel,
        String enderecoEmail,
        String valorSenha
    ) {
        prepararCasoDeUso();

        Usuario usuario = new Usuario(
            new UsuarioId(1),
            "Gabriel Reis",
            new Email(enderecoEmail),
            new Senha(valorSenha),
            PapelUsuario.valueOf(papel)
        );

        when(usuarioRepositorio.obterPorEmail(any(Email.class))).thenReturn(usuario);
    }

    @Dado("que não existe usuário cadastrado com e-mail {string}")
    public void que_nao_existe_usuario_cadastrado_com_email(String enderecoEmail) {
        prepararCasoDeUso();
        when(usuarioRepositorio.obterPorEmail(any(Email.class))).thenReturn(null);
    }

    @Quando("solicito login com e-mail {string} e senha {string}")
    public void solicito_login_com_email_e_senha(String enderecoEmail, String valorSenha) {
        try {
            LoginUsuarioComando comando = new LoginUsuarioComando(enderecoEmail, valorSenha);
            usuarioLogado = loginUsuarioCasoDeUso.executar(comando);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o login deve ser realizado com sucesso")
    public void o_login_deve_ser_realizado_com_sucesso() {
        assertNull(excecaoCapturada);
        assertNotNull(usuarioLogado);
    }

    @E("a sessão do usuário deve estar ativa")
    public void a_sessao_do_usuario_deve_estar_ativa() {
        assertTrue(sessaoUsuario.estaLogado());
        assertNotNull(sessaoUsuario.obterUsuarioLogado());
    }

    @E("o usuário logado deve ter ID {int}")
    public void o_usuario_logado_deve_ter_id(Integer idEsperado) {
        assertNotNull(usuarioLogado);
        assertEquals(idEsperado, usuarioLogado.getId().getId());
    }

    @E("o usuário logado deve ter papel {string}")
    public void o_usuario_logado_deve_ter_papel(String papelEsperado) {
        assertNotNull(usuarioLogado);
        assertEquals(papelEsperado, usuarioLogado.getPapel().name());
    }

    @Então("o login deve ser rejeitado")
    public void o_login_deve_ser_rejeitado() {
        assertNotNull(excecaoCapturada);
        assertNull(usuarioLogado);
    }

    @E("deve retornar o erro do login {string}")
    public void deve_retornar_o_erro_do_login(String mensagemErro) {
        assertNotNull(excecaoCapturada);
        assertEquals(mensagemErro, excecaoCapturada.getMessage());
    }
}
