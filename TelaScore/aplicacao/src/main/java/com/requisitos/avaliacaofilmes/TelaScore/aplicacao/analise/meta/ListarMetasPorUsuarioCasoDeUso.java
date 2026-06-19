package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistema;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistemaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ListarMetasPorUsuarioCasoDeUso {
    private final MetaRepositorio repositorio;
    private final MetaSistemaRepositorio metasSistema;
    private final GeradorId geradorId;

    public ListarMetasPorUsuarioCasoDeUso(MetaRepositorio repositorio,
            MetaSistemaRepositorio metasSistema, GeradorId geradorId) {
        this.repositorio = repositorio;
        this.metasSistema = metasSistema;
        this.geradorId = geradorId;
    }

    public List<MetaResumo> executar(int usuarioId) {
        UsuarioId id = new UsuarioId(usuarioId);
        List<Meta> metas = repositorio.buscarPorUsuario(id);
        Set<Integer> sistemasExistentes = metas.stream()
                .map(Meta::getMetaSistemaId)
                .filter(valor -> valor != null)
                .collect(Collectors.toSet());

        boolean criou = false;
        for (MetaSistema sistema : metasSistema.listarAtivas()) {
            if (!sistemasExistentes.contains(sistema.getId())) {
                Meta existenteSemVinculo = metas.stream()
                        .filter(meta -> meta.getMetaSistemaId() == null)
                        .filter(meta -> meta.getTitulo().equals(sistema.getTitulo()))
                        .findFirst()
                        .orElse(null);
                if (existenteSemVinculo != null) {
                    existenteSemVinculo.vincularMetaSistema(sistema.getId());
                    repositorio.salvar(existenteSemVinculo);
                    sistemasExistentes.add(sistema.getId());
                    criou = true;
                    continue;
                }
                Meta meta = new Meta(
                        new MetaId(geradorId.gerarProximoIdMeta()),
                        id,
                        sistema.getTitulo(),
                        sistema.getQuantidadeAlvo(),
                        LocalDate.now().plusDays(sistema.getDuracaoDias()));
                meta.vincularMetaSistema(sistema.getId());
                repositorio.salvar(meta);
                criou = true;
            }
        }

        if (criou) metas = repositorio.buscarPorUsuario(id);
        return metas.stream().map(MetaResumo::de).toList();
    }
}
