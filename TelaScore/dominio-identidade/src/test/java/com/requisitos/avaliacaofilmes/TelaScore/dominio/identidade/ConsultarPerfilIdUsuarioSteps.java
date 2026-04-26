package com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
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

public class ConsultarPerfilIdUsuarioSteps {

    private PerfilRepositorio perfilRepositorio;
    private PerfilServico perfilServico;

    private Perfil perfilEncontrado;

    private void prepararServico() {
        perfilRepositorio = mock(PerfilRepositorio.class);
        perfilServico = new PerfilServico(perfilRepositorio);
        perfilEncontrado = null;
    }

    @Dado("que existe um perfil cadastrado para o usuário {int}")
    public void que_existe_um_perfil_cadastrado_para_o_usuario(Integer idUsuario) {
        prepararServico();

        UsuarioId usuarioId = new UsuarioId(idUsuario);

        Perfil perfil = new Perfil(
            new PerfilId(1),
            usuarioId,
            new Apelido("Gabriel Reis")
        );

        when(perfilRepositorio.obterPorUsuario(usuarioId)).thenReturn(perfil);
    }

    @Dado("que não existe perfil cadastrado para o usuário {int}")
    public void que_nao_existe_perfil_cadastrado_para_o_usuario(Integer idUsuario) {
        prepararServico();

        UsuarioId usuarioId = new UsuarioId(idUsuario);
        when(perfilRepositorio.obterPorUsuario(usuarioId)).thenReturn(null);
    }

    @Quando("pesquiso o perfil do usuário {int}")
    public void pesquiso_o_perfil_do_usuario(Integer idUsuario) {
        perfilEncontrado = perfilServico.obterPorUsuario(new UsuarioId(idUsuario));
    }

    @Então("o perfil deve ser encontrado com sucesso")
    public void o_perfil_deve_ser_encontrado_com_sucesso() {
        assertNotNull(perfilEncontrado);
    }

    @E("o ID do usuário dono do perfil encontrado deve ser {int}")
    public void o_id_do_usuario_dono_do_perfil_encontrado_deve_ser(Integer idUsuarioEsperado) {
        assertEquals(idUsuarioEsperado, perfilEncontrado.getUsuarioId().getId());
    }

    @Então("o sistema deve informar que o perfil não foi encontrado")
    public void o_sistema_deve_informar_que_o_perfil_nao_foi_encontrado() {
        assertNull(perfilEncontrado);
    }
}
