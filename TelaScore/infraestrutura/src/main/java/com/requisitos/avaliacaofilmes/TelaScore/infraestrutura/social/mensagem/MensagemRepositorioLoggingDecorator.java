package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.mensagem;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.mensagem.*;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import java.util.List;

public class MensagemRepositorioLoggingDecorator implements MensagemRepositorio {
    private final MensagemRepositorio decorado;

    public MensagemRepositorioLoggingDecorator(MensagemRepositorio decorado) {
        this.decorado = decorado;
    }

    @Override
    public void salvar(Mensagem mensagem) {
        System.out.println("[DECORATOR LOG] Salvando mensagem ID: " + mensagem.getId().getId());
        decorado.salvar(mensagem);
    }

    @Override
    public void remover(MensagemId id) {
        System.out.println("[DECORATOR WARN] Removendo mensagem ID: " + id.getId());
        decorado.remover(id);
    }

    @Override
    public Mensagem obter(MensagemId id) { return decorado.obter(id); }
    @Override
    public List<Mensagem> buscarConversa(UsuarioId a, UsuarioId b) { return decorado.buscarConversa(a, b); }
    @Override
    public int contarMensagensNaoLidas(UsuarioId id) { return decorado.contarMensagensNaoLidas(id); }
}