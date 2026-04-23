package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.temporaria;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface MensagemRepositorio {
    void salvar(Mensagem mensagem);
    Mensagem obter(MensagemId id);
    List<Mensagem> buscarConversa(UsuarioId usuario1, UsuarioId usuario2);
    List<Mensagem> buscarNaoLidasPorUsuario(UsuarioId usuarioId);
}