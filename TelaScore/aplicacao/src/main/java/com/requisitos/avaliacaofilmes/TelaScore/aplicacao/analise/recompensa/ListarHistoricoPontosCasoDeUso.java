package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recompensa;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.RegistroPontuacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ListarHistoricoPontosCasoDeUso {

    private final RegistroPontuacaoRepositorio repositorio;

    public ListarHistoricoPontosCasoDeUso(RegistroPontuacaoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<RegistroPontuacao> executar(int usuarioId) {
        return repositorio.buscarPorUsuario(new UsuarioId(usuarioId));
    }
}