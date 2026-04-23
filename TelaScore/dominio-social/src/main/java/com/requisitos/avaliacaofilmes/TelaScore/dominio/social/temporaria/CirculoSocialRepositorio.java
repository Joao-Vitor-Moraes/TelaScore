package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.temporaria;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface CirculoSocialRepositorio {
    void salvar(CirculoSocial circulo);
    CirculoSocial obterPorUsuario(UsuarioId usuarioId);
}