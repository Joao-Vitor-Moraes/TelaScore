package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Visibilidade;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class EditarListaCasoDeUso {

    private final ListaRepositorio listaRepositorio;
    private final ListaServico listaServico;

    public EditarListaCasoDeUso(ListaRepositorio listaRepositorio, ListaServico listaServico) {
        this.listaRepositorio = listaRepositorio;
        this.listaServico = listaServico;
    }

    public void executar(EditarListaComando comando) {
        Lista lista = listaRepositorio.obter(new ListaId(comando.listaId()));
        if (lista == null) {
            throw new IllegalArgumentException("Lista não encontrada.");
        }
        listaServico.editarLista(
                lista,
                comando.nome(),
                comando.descricao(),
                Visibilidade.valueOf(comando.visibilidade().toUpperCase()),
                comando.rankeada(),
                new UsuarioId(comando.usuarioId())
        );
    }
}
