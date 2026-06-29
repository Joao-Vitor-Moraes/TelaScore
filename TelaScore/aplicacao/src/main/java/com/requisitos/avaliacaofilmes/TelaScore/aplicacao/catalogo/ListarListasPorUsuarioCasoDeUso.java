package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Visibilidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListarListasPorUsuarioCasoDeUso {

    private final ListaRepositorio listaRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private final ListaAssistidosServico listaAssistidosServico;

    public ListarListasPorUsuarioCasoDeUso(ListaRepositorio listaRepositorio,
            FilmeRepositorio filmeRepositorio,
            ListaAssistidosServico listaAssistidosServico) {
        this.listaRepositorio = listaRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.listaAssistidosServico = listaAssistidosServico;
    }

    public List<ListaResumo> executar(int usuarioId, Integer quemPedeId) {
        boolean isProprioUsuario = quemPedeId != null && quemPedeId.equals(usuarioId);
        if (isProprioUsuario) {
            listaAssistidosServico.sincronizar(new UsuarioId(usuarioId));
        }
        List<Lista> listas = new ArrayList<>(listaRepositorio.pesquisarPorDono(new UsuarioId(usuarioId)));

        if (isProprioUsuario) {
            List<Lista> colaborando = listaRepositorio.pesquisarPorColaborador(new UsuarioId(usuarioId));
            for (Lista l : colaborando) {
                if (listas.stream().noneMatch(e -> e.getId().equals(l.getId()))) {
                    listas.add(l);
                }
            }
        }

        return listas.stream()
                .filter(l -> isProprioUsuario || l.getVisibilidade() != Visibilidade.PRIVADA)
                .map(l -> new ListaResumo(
                        l.getId().getId(),
                        l.getDonoId().getId(),
                        l.getTitulo(),
                        l.getDescricao(),
                        l.isRanqueada(),
                        l.getTipo().name(),
                        l.getVisibilidade().name(),
                        l.isColaborativa(),
                        l.getItens().size(),
                        obterCapaUrl(l),
                        l.getColaboradores().stream().map(c -> c.getId()).collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    private String obterCapaUrl(Lista lista) {
        return lista.getItens().stream()
                .map(item -> filmeRepositorio.obter(item.getFilmeId()))
                .filter(filme -> filme != null && filme.getImagemUrl() != null && !filme.getImagemUrl().isBlank())
                .map(filme -> filme.getImagemUrl())
                .findFirst()
                .orElse(null);
    }
}
