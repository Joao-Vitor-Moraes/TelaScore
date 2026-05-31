package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioEstreia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.CalendarioServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.EntradaCalendario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario.ObservadorEstreia;
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

    private final List<FilmeId> filmesNotificados = new ArrayList<>();

    private void prepararServico() {
        repositorio = mock(CalendarioRepositorio.class);
        servico = new CalendarioServico(repositorio);
        calendario = null;
        filmesNotificados.clear();
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

    @Quando("o usuário remove o filme {int} do calendário")
    public void o_usuario_remove_o_filme_do_calendario(Integer idFilme) {
        if (calendario != null) {
            calendario.removerFilme(new FilmeId(String.valueOf(idFilme)));
        }
    }

    @Dado("um observador de lembretes está inscrito no calendário do usuário")
    public void um_observador_de_lembretes_esta_inscrito_no_calendario() {
        ObservadorEstreia observador = (usuarioId, entrada) -> filmesNotificados.add(entrada.getFilmeId());
        calendario.adicionarObservador(observador);
    }

    @Dado("o lembrete do filme {int} está desativado")
    public void o_lembrete_do_filme_esta_desativado(Integer idFilme) {
        for (EntradaCalendario entrada : calendario.getEntradas()) {
            if (entrada.getFilmeId().equals(new FilmeId(String.valueOf(idFilme))) && entrada.isLembreteAtivo()) {
                entrada.alternarLembrete();
            }
        }
    }

    @Quando("o sistema dispara os lembretes do dia {string}")
    public void o_sistema_dispara_os_lembretes_do_dia(String data) {
        calendario.dispararLembretesDoDia(LocalDate.parse(data));
    }

    @Então("o observador deve ser notificado sobre o filme {int}")
    public void o_observador_deve_ser_notificado_sobre_o_filme(Integer idFilme) {
        assertTrue(filmesNotificados.contains(new FilmeId(String.valueOf(idFilme))),
                "O observador deveria ter sido notificado sobre o filme.");
    }

    @Então("o observador não deve ser notificado sobre o filme {int}")
    public void o_observador_nao_deve_ser_notificado_sobre_o_filme(Integer idFilme) {
        assertFalse(filmesNotificados.contains(new FilmeId(String.valueOf(idFilme))),
                "O observador não deveria ter sido notificado sobre o filme.");
    }
}
