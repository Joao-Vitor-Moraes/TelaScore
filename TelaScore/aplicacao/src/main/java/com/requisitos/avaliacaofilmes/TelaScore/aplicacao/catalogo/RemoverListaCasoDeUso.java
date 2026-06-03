package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.Lista;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.lista.ListaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class RemoverListaCasoDeUso {

    private final ListaRepositorio listaRepositorio;

    public RemoverListaCasoDeUso(ListaRepositorio listaRepositorio) {
        this.listaRepositorio = listaRepositorio;
    }

    public void executar(RemoverListaComando comando) {
        ListaId listaId = new ListaId(comando.listaId());
        Lista lista = listaRepositorio.obter(listaId);
        if (lista == null) {
            throw new IllegalArgumentException("Lista não encontrada.");
        }
        if (!lista.getDonoId().equals(new UsuarioId(comando.usuarioId()))) {
            throw new IllegalStateException("Apenas o dono pode remover a lista.");
        }
        listaRepositorio.remover(listaId);
    }
}
