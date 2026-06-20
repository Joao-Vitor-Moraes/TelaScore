package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.evento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade.ComunidadeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;

public class ListarEventosVisiveisCasoDeUso {

    private final EventoRepositorio eventoRepositorio;
    private final ConexaoRepositorio conexaoRepositorio;
    private final ComunidadeRepositorio comunidadeRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public ListarEventosVisiveisCasoDeUso(EventoRepositorio eventoRepositorio,
                                          ConexaoRepositorio conexaoRepositorio,
                                          ComunidadeRepositorio comunidadeRepositorio,
                                          UsuarioRepositorio usuarioRepositorio) {
        this.eventoRepositorio = eventoRepositorio;
        this.conexaoRepositorio = conexaoRepositorio;
        this.comunidadeRepositorio = comunidadeRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public List<EventoResumo> executar(int usuarioIdInformado) {
        UsuarioId eu = new UsuarioId(usuarioIdInformado);
        Set<Integer> amigos = amigosMutuos(eu);

        List<Evento> eventos = eventoRepositorio.buscarEventosFuturos(LocalDateTime.now());
        List<EventoResumo> resultado = new ArrayList<>();
        for (Evento evento : eventos) {
            if (podeVer(evento, usuarioIdInformado, eu, amigos)) {
                resultado.add(EventoResumoMapper.map(evento, usuarioIdInformado, usuarioRepositorio));
            }
        }
        return resultado;
    }

    private boolean podeVer(Evento evento, int meuId, UsuarioId eu, Set<Integer> amigos) {
        if (evento.getCriadorId().getId() == meuId) {
            return true;
        }
        if (evento.getConvidados().contains(meuId)) {
            return true;
        }
        for (Integer comunidadeId : evento.getComunidadesConvidadas()) {
            if (comunidadeRepositorio.existeMembro(new ComunidadeId(comunidadeId), eu)) {
                return true;
            }
        }
        switch (evento.getVisibilidade()) {
            case PUBLICO:
                return true;
            case AMIGOS:
                return amigos.contains(evento.getCriadorId().getId());
            case PRIVADO:
            default:
                return false;
        }
    }

    private Set<Integer> amigosMutuos(UsuarioId eu) {
        Set<Integer> segue = conexaoRepositorio.buscarSeguidosPor(eu).stream()
                .map(c -> c.getSeguidoId().getId())
                .collect(Collectors.toSet());
        Set<Integer> seguidoPor = conexaoRepositorio.buscarSeguidoresDe(eu).stream()
                .map(c -> c.getSeguidorId().getId())
                .collect(Collectors.toSet());
        segue.retainAll(seguidoPor);
        return segue;
    }
}
