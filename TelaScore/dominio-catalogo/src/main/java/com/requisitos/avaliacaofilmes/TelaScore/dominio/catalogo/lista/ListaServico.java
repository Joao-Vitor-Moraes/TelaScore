package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista;

import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ListaServico {
    private final ListaRepositorio listaRepositorio;

    public ListaServico(ListaRepositorio listaRepositorio) {
        notNull(listaRepositorio, "O repositório de listas não pode ser nulo");
        this.listaRepositorio = listaRepositorio;
    }

    public void salvar(Lista lista) {
        notNull(lista, "A lista não pode ser nula");
        listaRepositorio.salvar(lista);
    }

    public void adicionarFilme(Lista lista, ItemLista item, UsuarioId usuarioId, boolean filmeJaFoiAssistido) {
        notNull(lista, "A lista não pode ser nula");
        lista.adicionarItem(item, usuarioId, filmeJaFoiAssistido);
        listaRepositorio.salvar(lista);
    }

    public void removerFilme(Lista lista, FilmeId filmeId, UsuarioId usuarioId) {
        notNull(lista, "A lista não pode ser nula");
        lista.removerItemPorFilme(filmeId, usuarioId);
        listaRepositorio.salvar(lista);
    }

    public void registrarFilmeAssistido(Lista lista, FilmeId filmeId, UsuarioId usuarioId) {
        notNull(lista, "A lista não pode ser nula");
        lista.processarFilmeAssistido(filmeId, usuarioId);
        listaRepositorio.salvar(lista);
    }

    public void tornarColaborativa(Lista lista, UsuarioId donoAcao) {
        notNull(lista, "A lista não pode ser nula");
        if (!lista.getDonoId().equals(donoAcao)) {
            throw new IllegalStateException("Apenas o dono pode tornar a lista colaborativa");
        }
        lista.tornarColaborativa();
        listaRepositorio.salvar(lista);
    }

    public void adicionarColaborador(Lista lista, UsuarioId donoAcao, UsuarioId novoColaborador) {
        notNull(lista, "A lista não pode ser nula");
        lista.adicionarColaborador(donoAcao, novoColaborador);
        listaRepositorio.salvar(lista);
    }

    public void reordenarFilme(Lista lista, FilmeId filmeId, int novaPosicao, UsuarioId usuarioId) {
        notNull(lista, "A lista não pode ser nula");
        lista.moverFilmeParaPosicao(filmeId, novaPosicao, usuarioId);
        listaRepositorio.salvar(lista);
    }
}