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
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
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
    private SessaoUsuario sessaoUsuario;
    private RemoverUsuarioCasoDeUso removerUsuarioCasoDeUso;

    private UsuarioId usuarioIdSolicitado;
    private Exception excecaoCapturada;

    private void prepararCasoDeUso() {
        usuarioRepositorio = mock(UsuarioRepositorio.class);
        usuarioServico = new UsuarioServico(usuarioRepositorio);
        perfilRepositorio = mock(PerfilRepositorio.class);
        perfilServico = new PerfilServico(perfilRepositorio);
        sessaoUsuario = new SessaoUsuario();
        removerUsuarioCasoDeUso = new RemoverUsuarioCasoDeUso(usuarioServico, perfilServico, sessaoUsuario);
        usuarioIdSolicitado = null;
        excecaoCapturada = null;
    }

    @Dado("que existe um administrador logado para remoção com ID {int}")
    public void que_existe_um_administrador_logado_para_remocao_com_id(Integer id) {
        prepararCasoDeUso();
        sessaoUsuario.iniciar(new UsuarioLogado(new UsuarioId(id), PapelUsuario.ADMIN));
    }

    @Dado("que existe um usuário comum logado para remoção com ID {int}")
    public void que_existe_um_usuario_comum_logado_para_remocao_com_id(Integer id) {
        prepararCasoDeUso();
        sessaoUsuario.iniciar(new UsuarioLogado(new UsuarioId(id), PapelUsuario.CINEFILO));
    }

    @Dado("que não há usuário logado para remoção")
    public void que_nao_ha_usuario_logado_para_remocao() {
        prepararCasoDeUso();
    }

    @E("existe um usuário alvo cadastrado para remoção com ID {int}")
    public void existe_um_usuario_alvo_cadastrado_para_remocao_com_id(Integer id) {
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

    @E("não existe usuário alvo cadastrado para remoção com ID {int}")
    public void nao_existe_usuario_alvo_cadastrado_para_remocao_com_id(Integer id) {
        UsuarioId usuarioId = new UsuarioId(id);
        when(usuarioRepositorio.obter(usuarioId)).thenReturn(null);
    }

    @Quando("solicito remover o usuário com ID {int}")
    public void solicito_remover_o_usuario_com_id(Integer usuarioId) {
        try {
            usuarioIdSolicitado = new UsuarioId(usuarioId);
            RemoverUsuarioComando comando = new RemoverUsuarioComando(usuarioId);
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
