package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioEstreia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.EntradaCalendario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class CalendarioSteps {

    private CalendarioRepositorio repositorio;
    private CalendarioServico servico;
    private CalendarioEstreia calendario;
    private UsuarioId usuarioIdAtual;

    private void prepararServico() {
        repositorio = mock(CalendarioRepositorio.class);
        servico = new CalendarioServico(repositorio);
        calendario = null;
    }

    @Dado("que existe um calendário cadastrado para o usuário {int}")
    public void que_existe_um_calendario_cadastrado_para_o_usuario(Integer idUsuario) {
        prepararServico();
        usuarioIdAtual = new UsuarioId(idUsuario);
        calendario = new CalendarioEstreia(new CalendarioId(1), usuarioIdAtual);
        when(repositorio.obterPorUsuario(usuarioIdAtual)).thenReturn(calendario);
    }

    @Dado("que não existe calendário cadastrado para o usuário {int}")
    public void que_nao_existe_calendario_cadastrado_para_o_usuario(Integer idUsuario) {
        prepararServico();
        usuarioIdAtual = new UsuarioId(idUsuario);
        when(repositorio.obterPorUsuario(usuarioIdAtual)).thenReturn(null);
    }

    @Quando("o usuário registra o filme {int} no calendário para a data {string}")
    public void o_usuario_registra_o_filme_no_calendario_para_a_data(Integer idFilme, String data) {
        LocalDate dataEstreia = LocalDate.parse(data);
        EntradaCalendario entrada = new EntradaCalendario(new FilmeId(String.valueOf(idFilme)), dataEstreia);
        servico.registrarFilmeNoCalendario(usuarioIdAtual, entrada);
    }

    @Então("o filme {int} deve estar no calendário do usuário")
    public void o_filme_deve_estar_no_calendario_do_usuario(Integer idFilme) {
        boolean encontrado = false;
        if (calendario != null) {
            for (EntradaCalendario entrada : calendario.getEntradas()) {
                if (entrada.getFilmeId().equals(new FilmeId(String.valueOf(idFilme)))) {
                    encontrado = true;
                    break;
                }
            }
        }
        assertTrue(encontrado, "O filme deveria estar no calendário do usuário.");
    }

    @Então("o calendário do usuário não deve conter o filme {int}")
    public void o_calendario_do_usuario_nao_deve_conter_o_filme(Integer idFilme) {
        boolean encontrado = false;
        if (calendario != null) {
            for (EntradaCalendario entrada : calendario.getEntradas()) {
                if (entrada.getFilmeId().equals(new FilmeId(String.valueOf(idFilme)))) {
                    encontrado = true;
                    break;
                }
            }
        }
        assertFalse(encontrado, "O filme não deveria estar no calendário do usuário.");
    }
}
