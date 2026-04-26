package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class CadastrarUsuarioSteps {

    private UsuarioId usuarioId;
    private String nome;

    private Usuario usuarioCriado;
    private Exception excecaoCapturada;

    @Dado("que desejo cadastrar um usuário chamado {string}")
    public void que_desejo_cadastrar_um_usuario_chamado(String nome) {
        usuarioId = new UsuarioId(1);
        this.nome = nome;

        usuarioCriado = null;
        excecaoCapturada = null;
    }

    @Dado("que desejo cadastrar um usuário sem nome")
    public void que_desejo_cadastrar_um_usuario_sem_nome() {
        usuarioId = new UsuarioId(1);
        nome = "";

        usuarioCriado = null;
        excecaoCapturada = null;
    }

    @Quando("informo o e-mail {string}")
    public void informo_o_email(String enderecoEmail) {
        try {
            Email email = new Email(enderecoEmail);

            usuarioCriado = new Usuario(
                usuarioId,
                nome,
                email,
                PapelUsuario.CINEFILO
            );
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o usuário deve ser criado com sucesso")
    public void o_usuario_deve_ser_criado_com_sucesso() {
        assertEquals(null, excecaoCapturada, "Nenhuma exceção deveria ter sido lançada");
        assertNotNull(usuarioCriado, "A entidade Usuario deveria ter sido criada");
    }

    @Então("o sistema deve bloquear o cadastro informando que o e-mail é inválido")
    public void o_sistema_deve_bloquear_o_cadastro_informando_que_o_email_e_invalido() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada pela classe Email");
        assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass());
    }

    @Então("o sistema deve bloquear o cadastro informando que o nome não pode estar em branco")
    public void o_sistema_deve_bloquear_o_cadastro_informando_que_o_nome_nao_pode_estar_em_branco() {
        assertNotNull(excecaoCapturada, "Uma exceção deveria ter sido lançada pela classe Usuario");
        assertEquals(IllegalArgumentException.class, excecaoCapturada.getClass());
    }
}
