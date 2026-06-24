package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao;

import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;

public class ListarConexoesCasoDeUso {

    private final ConexaoRepositorio conexaoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public ListarConexoesCasoDeUso(ConexaoRepositorio conexaoRepositorio,
                                   UsuarioRepositorio usuarioRepositorio) {
        this.conexaoRepositorio = conexaoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public List<AmigoResumo> listarSeguindo(int usuarioIdInformado) {
        UsuarioId usuarioId = new UsuarioId(usuarioIdInformado);
        return conexaoRepositorio.buscarSeguidosPor(usuarioId).stream()
                .map(conexao -> usuarioRepositorio.obter(conexao.getSeguidoId()))
                .filter(usuario -> usuario != null)
                .map(this::resumir)
                .toList();
    }

    public List<AmigoResumo> listarSeguidores(int usuarioIdInformado) {
        UsuarioId usuarioId = new UsuarioId(usuarioIdInformado);
        return conexaoRepositorio.buscarSeguidoresDe(usuarioId).stream()
                .map(conexao -> usuarioRepositorio.obter(conexao.getSeguidorId()))
                .filter(usuario -> usuario != null)
                .map(this::resumir)
                .toList();
    }

    private AmigoResumo resumir(Usuario usuario) {
        return new AmigoResumo(
                usuario.getId().getId(),
                usuario.getNome(),
                usuario.getApelido() != null ? usuario.getApelido().getValor() : null,
                usuario.getAvatarUrl());
    }
}
