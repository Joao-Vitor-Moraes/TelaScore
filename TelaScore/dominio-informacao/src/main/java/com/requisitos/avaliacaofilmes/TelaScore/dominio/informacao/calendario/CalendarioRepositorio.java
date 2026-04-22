package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface CalendarioRepositorio {
    void salvar(CalendarioEstreia calendario);
    
    CalendarioEstreia obterPorUsuario(UsuarioId usuarioId);
}