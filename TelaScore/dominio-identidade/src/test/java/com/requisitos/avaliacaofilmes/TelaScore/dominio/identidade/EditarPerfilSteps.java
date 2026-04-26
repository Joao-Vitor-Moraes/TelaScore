package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private PerfilId perfilId;
    private Perfil perfilAtualizado;
    private Exception excecaoCapturada;

    @Dado("que existe um perfil com ID {int} do usuário {int} com apelido {string}")
    public void que_existe_um_perfil_com_id_do_usuario_com_apelido(Integer idPerfil, Integer idUsuario, String apelido) {
        perfilRepositorio = mock(PerfilRepositorio.class);
        perfilServico = new PerfilServico(perfilRepositorio);

        perfilId = new PerfilId(idPerfil);

        Perfil perfil = new Perfil(
            perfilId,
            new UsuarioId(idUsuario),
            new Apelido(apelido)
        );

        when(perfilRepositorio.obter(perfilId)).thenReturn(perfil);

        perfilAtualizado = null;
        excecaoCapturada = null;
    }

    @Quando("o usuário {int} altera o perfil {int} com apelido {string}, biografia {string} e avatar {string}")
    public void o_usuario_altera_o_perfil_com_apelido_biografia_e_avatar(
        Integer idUsuario,
        Integer idPerfil,
        String apelido,
        String biografia,
        String avatar
    ) {
        try {
            perfilAtualizado = perfilServico.editar(
                new PerfilId(idPerfil),
                new UsuarioId(idUsuario),
                apelido,
                biografia,
                avatar
            );
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("o usuário {int} altera o perfil {int} sem apelido, com biografia {string} e avatar {string}")
    public void o_usuario_altera_o_perfil_sem_apelido_com_biografia_e_avatar(
        Integer idUsuario,
        Integer idPerfil,
        String biografia,
        String avatar
    ) {
        try {
            perfilAtualizado = perfilServico.editar(
                new PerfilId(idPerfil),
                new UsuarioId(idUsuario),
                null,
                biografia,
                avatar
            );
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Quando("o usuário {int} tenta alterar o perfil {int} com apelido {string}, biografia {string} e avatar {string}")
    public void o_usuario_tenta_alterar_o_perfil_com_apelido_biografia_e_avatar(
        Integer idUsuario,
        Integer idPerfil,
        String apelido,
        String biografia,
        String avatar
    ) {
        try {
            perfilAtualizado = perfilServico.editar(
                new PerfilId(idPerfil),
                new UsuarioId(idUsuario),
                apelido,
                biografia,
                avatar
            );
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o perfil deve ser atualizado com sucesso")
    public void o_perfil_deve_ser_atualizado_com_sucesso() {
        assertNull(excecaoCapturada);
        assertNotNull(perfilAtualizado);
        verify(perfilRepositorio, times(1)).salvar(perfilAtualizado);
    }

    @E("o apelido do perfil deve ser {string}")
    public void o_apelido_do_perfil_deve_ser(String apelidoEsperado) {
        assertEquals(apelidoEsperado, perfilAtualizado.getApelido().getValor());
    }

    @E("o apelido do perfil deve continuar sendo {string}")
    public void o_apelido_do_perfil_deve_continuar_sendo(String apelidoEsperado) {
        assertEquals(apelidoEsperado, perfilAtualizado.getApelido().getValor());
    }

    @E("a biografia do perfil deve ser {string}")
    public void a_biografia_do_perfil_deve_ser(String biografiaEsperada) {
        assertEquals(biografiaEsperada, perfilAtualizado.getBiografia());
    }

    @E("o avatar do perfil deve ser {string}")
    public void o_avatar_do_perfil_deve_ser(String avatarEsperado) {
        assertEquals(avatarEsperado, perfilAtualizado.getAvatarUrl());
    }

    @Então("o sistema deve bloquear a edição informando que o perfil não pertence ao usuário")
    public void o_sistema_deve_bloquear_a_edicao_informando_que_o_perfil_nao_pertence_ao_usuario() {
        assertNotNull(excecaoCapturada);
        assertEquals(IllegalStateException.class, excecaoCapturada.getClass());
        assertEquals("O perfil não pertence ao usuário", excecaoCapturada.getMessage());
    }
}
