package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import java.time.LocalDate;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistema;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistemaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;

public class CriarMetaSistemaCasoDeUso {
    private final MetaSistemaRepositorio metasSistema;
    private final MetaRepositorio metas;
    private final UsuarioRepositorio usuarios;
    private final GeradorId geradorId;

    public CriarMetaSistemaCasoDeUso(MetaSistemaRepositorio metasSistema,
            MetaRepositorio metas, UsuarioRepositorio usuarios, GeradorId geradorId) {
        this.metasSistema = metasSistema;
        this.metas = metas;
        this.usuarios = usuarios;
        this.geradorId = geradorId;
    }

    public MetaSistemaResumo executar(CriarMetaSistemaComando comando) {
        MetaSistema sistema = new MetaSistema(
                metasSistema.proximoId(),
                comando.titulo(),
                comando.quantidadeAlvo(),
                comando.duracaoDias(),
                comando.adminId());
        metasSistema.salvar(sistema);

        for (Usuario usuario : usuarios.listarTodos()) {
            Meta individual = new Meta(
                    new MetaId(geradorId.gerarProximoIdMeta()),
                    usuario.getId(),
                    sistema.getTitulo(),
                    sistema.getQuantidadeAlvo(),
                    LocalDate.now().plusDays(sistema.getDuracaoDias()));
            individual.vincularMetaSistema(sistema.getId());
            metas.salvar(individual);
        }
        return MetaSistemaResumo.de(sistema);
    }
}
