package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ItemLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.TipoLista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Visibilidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ListaAssistidosServico {

    private static final String TITULO = "Filmes vistos";
    private static final String DESCRICAO = "Filmes que voce ja avaliou e marcou como assistidos.";

    private final ListaRepositorio listaRepositorio;
    private final ListaServico listaServico;
    private final GeradorId geradorId;
    private final AvaliacaoRepositorio avaliacaoRepositorio;

    public ListaAssistidosServico(ListaRepositorio listaRepositorio,
            ListaServico listaServico,
            GeradorId geradorId,
            AvaliacaoRepositorio avaliacaoRepositorio) {
        this.listaRepositorio = listaRepositorio;
        this.listaServico = listaServico;
        this.geradorId = geradorId;
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    public Lista garantirLista(UsuarioId usuarioId) {
        return listaRepositorio.pesquisarPorDono(usuarioId).stream()
                .filter(lista -> lista.getTipo() == TipoLista.ASSISTIDOS)
                .findFirst()
                .orElseGet(() -> criarLista(usuarioId));
    }

    public void sincronizar(UsuarioId usuarioId) {
        Lista lista = garantirLista(usuarioId);
        for (Avaliacao avaliacao : avaliacaoRepositorio.pesquisarPorUsuario(usuarioId)) {
            adicionarSeAindaNaoExiste(lista, avaliacao.getFilmeId(), usuarioId);
        }
    }

    public void registrarAssistido(UsuarioId usuarioId, FilmeId filmeId) {
        Lista lista = garantirLista(usuarioId);
        adicionarSeAindaNaoExiste(lista, filmeId, usuarioId);
    }

    private Lista criarLista(UsuarioId usuarioId) {
        Lista lista = new Lista(
                new ListaId(geradorId.gerarProximoIdLista()),
                usuarioId,
                TITULO,
                DESCRICAO,
                false,
                Visibilidade.PUBLICA,
                TipoLista.ASSISTIDOS
        );
        listaServico.salvar(lista);
        return lista;
    }

    private void adicionarSeAindaNaoExiste(Lista lista, FilmeId filmeId, UsuarioId usuarioId) {
        boolean jaExiste = lista.getItens().stream()
                .anyMatch(item -> item.getFilmeId().equals(filmeId));
        if (!jaExiste) {
            listaServico.adicionarFilme(lista, new ItemLista(filmeId, null), usuarioId, true);
        }
    }
}
