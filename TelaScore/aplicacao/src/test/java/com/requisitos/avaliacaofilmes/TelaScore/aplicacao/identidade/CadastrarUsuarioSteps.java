package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;

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

public class CadastrarUsuarioSteps {

    private GeradorId geradorId;
    private UsuarioRepositorio usuarioRepositorio;
    private UsuarioServico usuarioServico;
    private CadastrarUsuarioCasoDeUso cadastrarUsuarioCasoDeUso;

    private String nome;
    private String email;
    private Exception excecaoCapturada;

    private void prepararCasoDeUso() {
        geradorId = mock(GeradorId.class);
        when(geradorId.gerarProximoIdUsuario()).thenReturn(1);

        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);
        cadastrarUsuarioCasoDeUso = new CadastrarUsuarioCasoDeUso(usuarioServico, geradorId);
    }

    @Dado("que desejo criar uma conta com o nome {string}")
    public void que_desejo_criar_uma_conta_com_o_nome(String nome) {
        prepararCasoDeUso();
        this.nome = nome;
        excecaoCapturada = null;
    }

    @E("desejo criar uma conta com o nome {string}")
    public void desejo_criar_uma_conta_com_o_nome(String nome) {
        this.nome = nome;
    }

    @Dado("que desejo criar uma conta sem informar o nome")
    public void que_desejo_criar_uma_conta_sem_informar_o_nome() {
        prepararCasoDeUso();
        nome = "";
        excecaoCapturada = null;
    }

    @Dado("que já existe um usuário cadastrado com o e-mail {string}")
    public void que_ja_existe_um_usuario_cadastrado_com_o_email(String emailExistente) {
        prepararCasoDeUso();

        Email email = new Email(emailExistente);
        Usuario usuarioExistente = new Usuario(
            new UsuarioId(99),
            "Usuário Existente",
            email,
            PapelUsuario.CINEFILO
        );

        when(usuarioRepositorio.obterPorEmail(any(Email.class))).thenReturn(usuarioExistente);
    }

    @E("informo o e-mail {string}")
    public void informo_o_email(String email) {
        this.email = email;
    }

    @Quando("solicito o cadastro do usuário")
    public void solicito_o_cadastro_do_usuario() {
        try {
            CadastrarUsuarioComando comando = new CadastrarUsuarioComando(nome, email);
            cadastrarUsuarioCasoDeUso.executar(comando);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o cadastro deve ser realizado com sucesso")
    public void o_cadastro_deve_ser_realizado_com_sucesso() {
        assertNull(excecaoCapturada);
        verify(usuarioRepositorio, times(1)).salvar(any(Usuario.class));
    }

    @E("o usuário deve receber o papel {string}")
    public void o_usuario_deve_receber_o_papel(String papel) {
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepositorio).salvar(captor.capture());

        Usuario usuarioSalvo = captor.getValue();
        assertEquals(papel, usuarioSalvo.getPapel().name());
    }

    @Então("o cadastro deve ser rejeitado")
    public void o_cadastro_deve_ser_rejeitado() {
        assertNotNull(excecaoCapturada);
        verify(usuarioRepositorio, never()).salvar(any(Usuario.class));
    }

    @E("deve retornar o erro {string}")
    public void deve_retornar_o_erro(String mensagemErro) {
        assertNotNull(excecaoCapturada);
        assertEquals(mensagemErro, excecaoCapturada.getMessage());
    }
}
