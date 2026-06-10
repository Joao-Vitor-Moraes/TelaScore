package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.informacao.evento;

import static org.junit.jupiter.api.Assertions.*;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

public class EventoRepositorioImplTest {

    private final EventoRepositorio repositorio = new EventoRepositorioImpl();

    @Test
    public void deveSalvarObterBuscarFuturosERemoverUmEvento() {
        EventoId eventoId = new EventoId(777);
        UsuarioId criadorId = new UsuarioId(123);
        LocalDateTime dataFutura = LocalDateTime.now().plusDays(30);

        Evento evento = new Evento(eventoId, criadorId, "Maratona Cult de Terror", dataFutura);
        evento.setDescricao("Sessão dupla com clássicos do horror.");

        repositorio.salvar(evento);

        Evento buscado = repositorio.obter(eventoId);
        assertNotNull(buscado);
        assertEquals("Maratona Cult de Terror", buscado.getTitulo());
        assertEquals("Sessão dupla com clássicos do horror.", buscado.getDescricao());
        assertEquals(123, buscado.getCriadorId().getId());

        List<Evento> futuros = repositorio.buscarEventosFuturos(LocalDateTime.now());
        assertTrue(futuros.stream().anyMatch(e -> e.getId().equals(eventoId)));

        repositorio.remover(eventoId);
        assertNull(repositorio.obter(eventoId));
    }
}
