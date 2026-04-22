package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.rede;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface CirculoSocialRepositorio {
    void salvar(CirculoSocial circulo);
    CirculoSocial obterPorUsuario(UsuarioId usuarioId);
}