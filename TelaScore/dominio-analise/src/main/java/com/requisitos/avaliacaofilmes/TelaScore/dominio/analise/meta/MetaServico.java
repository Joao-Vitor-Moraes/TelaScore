package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDate;

public class MetaServico {
    private final MetaRepositorio repositorio;

    public MetaServico(MetaRepositorio repositorio) {
        notNull(repositorio, "O repositório de metas não pode ser nulo");
        this.repositorio = repositorio;
    }

    public void criarMeta(Meta meta) {
        notNull(meta, "A meta não pode ser nula");
        repositorio.salvar(meta);
    }
    
    public void atualizarProgresso(Meta meta, int quantidade) {
        notNull(meta, "A meta não pode ser nula");
        
        meta.adicionarProgresso(quantidade);
        repositorio.salvar(meta);
    }

    public void removerProgresso(Meta meta, int quantidade) {
        notNull(meta, "A meta não pode ser nula");
        
        meta.removerProgresso(quantidade);
        
        repositorio.salvar(meta);
    }

    public void estenderPrazo(Meta meta, LocalDate novoPrazo) {
        notNull(meta, "A meta não pode ser nula");
        
        meta.estenderPrazo(novoPrazo);
        repositorio.salvar(meta);
    }

    public void cancelarMeta(Meta meta) {
        notNull(meta, "A meta não pode ser nula");
        
        meta.cancelar();
        repositorio.salvar(meta);
    }

    public void falharMeta(Meta meta) {
        notNull(meta, "A meta não pode ser nula");
        
        meta.marcarComoFalhada();
        repositorio.salvar(meta);
    }
}