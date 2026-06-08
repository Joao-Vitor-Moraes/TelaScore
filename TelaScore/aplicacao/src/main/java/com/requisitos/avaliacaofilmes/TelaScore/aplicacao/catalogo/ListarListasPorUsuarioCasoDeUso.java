package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import java.util.List;
import java.util.stream.Collectors;

public class ListarListasPorUsuarioCasoDeUso {

    private final ListaRepositorio listaRepositorio;

    public ListarListasPorUsuarioCasoDeUso(ListaRepositorio listaRepositorio) {
        this.listaRepositorio = listaRepositorio;
    }

    public List<ListaResumo> executar(int usuarioId) {
        List<Lista> listas = listaRepositorio.pesquisarPorDono(new UsuarioId(usuarioId));
        return listas.stream()
                .map(l -> new ListaResumo(
                        l.getId().getId(),
                        l.getDonoId().getId(),
                        l.getTitulo(),
                        l.getDescricao(),
                        l.isRanqueada(),
                        l.getTipo().name(),
                        l.getVisibilidade().name(),
                        l.isColaborativa(),
                        l.getItens().size()))
                .collect(Collectors.toList());
    }
}
