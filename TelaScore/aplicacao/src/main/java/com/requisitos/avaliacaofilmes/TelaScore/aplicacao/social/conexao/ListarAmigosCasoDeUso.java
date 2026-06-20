package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.conexao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.conexao.ConexaoRepositorio;

/**
 * Lista os amigos (amizade mútua: os dois se seguem) de um utilizador,
 * usado para convidar pessoas para eventos.
 */
public class ListarAmigosCasoDeUso {

    private final ConexaoRepositorio conexaoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;

    public ListarAmigosCasoDeUso(ConexaoRepositorio conexaoRepositorio,
                                 UsuarioRepositorio usuarioRepositorio) {
        this.conexaoRepositorio = conexaoRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public List<AmigoResumo> executar(int usuarioIdInformado) {
        UsuarioId eu = new UsuarioId(usuarioIdInformado);

        Set<Integer> segue = conexaoRepositorio.buscarSeguidosPor(eu).stream()
                .map(c -> c.getSeguidoId().getId())
                .collect(Collectors.toSet());
        Set<Integer> seguidoPor = conexaoRepositorio.buscarSeguidoresDe(eu).stream()
                .map(c -> c.getSeguidorId().getId())
                .collect(Collectors.toSet());
        segue.retainAll(seguidoPor);

        List<AmigoResumo> amigos = new ArrayList<>();
        for (Integer id : segue) {
            Usuario usuario = usuarioRepositorio.obter(new UsuarioId(id));
            if (usuario != null) {
                amigos.add(new AmigoResumo(
                        usuario.getId().getId(),
                        usuario.getNome(),
                        usuario.getApelido() != null ? usuario.getApelido().getValor() : null,
                        usuario.getAvatarUrl()));
            }
        }
        return amigos;
    }
}
