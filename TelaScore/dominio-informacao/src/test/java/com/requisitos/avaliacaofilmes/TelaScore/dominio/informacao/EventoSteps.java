package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

public class EventoSteps {

    private EventoRepositorio repositorio;
    private EventoServico servico;
    private Evento eventoAtual;
    private EventoId eventoId;

    private void prepararServico() {
        if (servico == null) {
            repositorio = mock(EventoRepositorio.class);
            servico = new EventoServico(repositorio);
        }
    }

    @Dado("que o usuário deseja agendar um novo evento com ID {int}")
    public void que_o_usuario_deseja_agendar_um_novo_evento_com_id(Integer id) {
        prepararServico();
        eventoId = new EventoId(id);
        UsuarioId criadorId = new UsuarioId(1); // ID genérico para o teste
        // A data precisa ser no futuro devido à validação de domínio
        eventoAtual = new Evento(eventoId, criadorId, "Sessão Especial de Cinema", LocalDateTime.now().plusDays(2));
    }

    @Quando("o usuário agenda o evento")
    public void o_usuario_agenda_o_evento() {
        servico.agendarEvento(eventoAtual);
    }

    @Então("o evento deve ser salvo com sucesso")
    public void o_evento_deve_ser_salvo_com_sucesso() {
        verify(repositorio).salvar(eventoAtual);
    }

    @Dado("que existe um evento agendado com ID {int}")
    public void que_existe_um_evento_agendado_com_id(Integer id) {
        prepararServico();
        eventoId = new EventoId(id);
    }

    @Quando("o usuário cancela o evento agendado")
    public void o_usuario_cancela_o_evento_agendado() {
        servico.cancelarEvento(eventoId);
    }

    @Então("o evento deve ser removido com sucesso")
    public void o_evento_deve_ser_removido_com_sucesso() {
        verify(repositorio).remover(eventoId);
    }
}
