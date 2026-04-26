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

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Perfil;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class EditarPerfilSteps {

    private PerfilRepositorio perfilRepositorio;
    private PerfilServico perfilServico;
    private EditarPerfilCasoDeUso editarPerfilCasoDeUso;

    private Exception excecaoCapturada;

    private void prepararCasoDeUso() {
        perfilRepositorio = mock(PerfilRepositorio.class);
        perfilServico = new PerfilServico(perfilRepositorio);
        editarPerfilCasoDeUso = new EditarPerfilCasoDeUso(perfilServico);
        excecaoCapturada = null;
    }

    @Dado("que existe um perfil com ID {int} do usuário {int} com apelido {string}")
    public void que_existe_um_perfil_com_id_do_usuario_com_apelido(Integer idPerfil, Integer idUsuario, String apelido) {
        prepararCasoDeUso();

        PerfilId perfilId = new PerfilId(idPerfil);
        Perfil perfil = new Perfil(
            perfilId,
            new UsuarioId(idUsuario),
            new Apelido(apelido)
        );

        when(perfilRepositorio.obter(perfilId)).thenReturn(perfil);
    }

    @Quando("o usuário {int} solicita a edição do perfil {int} com apelido {string}, biografia {string} e avatar {string}")
    public void o_usuario_solicita_a_edicao_do_perfil_com_apelido_biografia_e_avatar(
        Integer idUsuario,
        Integer idPerfil,
        String apelido,
        String biografia,
        String avatar
    ) {
        try {
            EditarPerfilComando comando = new EditarPerfilComando(
                idPerfil,
                idUsuario,
                apelido,
                biografia,
                avatar
            );

            editarPerfilCasoDeUso.executar(comando);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("o usuário {int} solicita a edição do perfil {int} sem apelido, com biografia {string} e avatar {string}")
    public void o_usuario_solicita_a_edicao_do_perfil_sem_apelido_com_biografia_e_avatar(
        Integer idUsuario,
        Integer idPerfil,
        String biografia,
        String avatar
    ) {
        try {
            EditarPerfilComando comando = new EditarPerfilComando(
                idPerfil,
                idUsuario,
                null,
                biografia,
                avatar
            );

            editarPerfilCasoDeUso.executar(comando);
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("a edição do perfil deve ser realizada com sucesso")
    public void a_edicao_do_perfil_deve_ser_realizada_com_sucesso() {
        assertNull(excecaoCapturada);
        verify(perfilRepositorio, times(1)).salvar(any(Perfil.class));
    }

    @Então("a edição do perfil deve ser rejeitada")
    public void a_edicao_do_perfil_deve_ser_rejeitada() {
        assertNotNull(excecaoCapturada);
        verify(perfilRepositorio, never()).salvar(any(Perfil.class));
    }

    @E("deve retornar o erro da edição de perfil {string}")
    public void deve_retornar_o_erro_da_edicao_de_perfil(String mensagemErro) {
        assertNotNull(excecaoCapturada);
        assertEquals(mensagemErro, excecaoCapturada.getMessage());
    }

    @E("o apelido editado do perfil deve ser {string}")
    public void o_apelido_editado_do_perfil_deve_ser(String apelidoEsperado) {
        Perfil perfilSalvo = capturarPerfilSalvo();
        assertEquals(apelidoEsperado, perfilSalvo.getApelido().getValor());
    }

    @E("a biografia editada do perfil deve ser {string}")
    public void a_biografia_editada_do_perfil_deve_ser(String biografiaEsperada) {
        Perfil perfilSalvo = capturarPerfilSalvo();
        assertEquals(biografiaEsperada, perfilSalvo.getBiografia());
    }

    @E("o avatar editado do perfil deve ser {string}")
    public void o_avatar_editado_do_perfil_deve_ser(String avatarEsperado) {
        Perfil perfilSalvo = capturarPerfilSalvo();
        assertEquals(avatarEsperado, perfilSalvo.getAvatarUrl());
    }

    private Perfil capturarPerfilSalvo() {
        ArgumentCaptor<Perfil> captor = ArgumentCaptor.forClass(Perfil.class);
        verify(perfilRepositorio).salvar(captor.capture());
        return captor.getValue();
    }
}
