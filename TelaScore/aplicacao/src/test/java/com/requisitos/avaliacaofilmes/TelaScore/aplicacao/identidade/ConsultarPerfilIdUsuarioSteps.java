package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade;

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
    private ConsultarPerfilIdUsuarioCasoDeUso consultarPerfilCasoDeUso;

    private Perfil perfilRetornado;

    private void prepararCasoDeUso() {
        perfilRepositorio = mock(PerfilRepositorio.class);
        perfilServico = new PerfilServico(perfilRepositorio);
        consultarPerfilCasoDeUso = new ConsultarPerfilIdUsuarioCasoDeUso(perfilServico);
        perfilRetornado = null;
    }

    @Dado("que existe um perfil cadastrado para o usuário {int}")
    public void que_existe_um_perfil_cadastrado_para_o_usuario(Integer idUsuario) {
        prepararCasoDeUso();

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
        prepararCasoDeUso();

        UsuarioId usuarioId = new UsuarioId(idUsuario);
        when(perfilRepositorio.obterPorUsuario(usuarioId)).thenReturn(null);
    }

    @Quando("solicito a consulta do perfil do usuário {int}")
    public void solicito_a_consulta_do_perfil_do_usuario(Integer idUsuario) {
        ConsultarPerfilIdUsuarioComando comando = new ConsultarPerfilIdUsuarioComando(idUsuario);
        perfilRetornado = consultarPerfilCasoDeUso.executar(comando);
    }

    @Então("a consulta do perfil deve ser realizada com sucesso")
    public void a_consulta_do_perfil_deve_ser_realizada_com_sucesso() {
        assertNotNull(perfilRetornado);
    }

    @E("o perfil retornado deve pertencer ao usuário {int}")
    public void o_perfil_retornado_deve_pertencer_ao_usuario(Integer idUsuarioEsperado) {
        assertEquals(idUsuarioEsperado, perfilRetornado.getUsuarioId().getId());
    }

    @Então("a consulta do perfil deve retornar nenhum resultado")
    public void a_consulta_do_perfil_deve_retornar_nenhum_resultado() {
        assertNull(perfilRetornado);
    }
}
