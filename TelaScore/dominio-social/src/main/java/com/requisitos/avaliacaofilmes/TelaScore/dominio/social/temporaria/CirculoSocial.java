package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.temporaria;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import java.util.Set;
import java.util.HashSet;

public class CirculoSocial {
    private final UsuarioId usuarioId;
    private final Set<UsuarioId> seguindo;
    private final Set<UsuarioId> seguidores;

    public CirculoSocial(UsuarioId usuarioId) {
        this.usuarioId = usuarioId;
        this.seguindo = new HashSet<>();
        this.seguidores = new HashSet<>();
    }

    public void seguir(UsuarioId outroUsuario) {
        if (this.usuarioId.equals(outroUsuario)) throw new SocialException("Não pode seguir a si mesmo");
        this.seguindo.add(outroUsuario);
    }
    
    public void deixarDeSeguir(UsuarioId outroUsuario) {
        this.seguindo.remove(outroUsuario);
    }
}