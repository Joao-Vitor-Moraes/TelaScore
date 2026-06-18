package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ListarMetasPorUsuarioCasoDeUso {
    private final MetaRepositorio repositorio;
    private final GeradorId geradorId;

    public ListarMetasPorUsuarioCasoDeUso(MetaRepositorio repositorio, GeradorId geradorId) {
        this.repositorio = repositorio;
        this.geradorId = geradorId;
    }

    public List<MetaResumo> executar(int usuarioId) {
        UsuarioId id = new UsuarioId(usuarioId);
        List<Meta> metas = repositorio.buscarPorUsuario(id);

        if (garantirMetasIniciais(id, metas)) {
            metas = repositorio.buscarPorUsuario(id);
        }

        return metas.stream()
                .map(MetaResumo::de)
                .toList();
    }

    private boolean garantirMetasIniciais(UsuarioId usuarioId, List<Meta> metasAtuais) {
        Set<String> titulosExistentes = metasAtuais.stream()
                .map(Meta::getTitulo)
                .collect(Collectors.toSet());
        LocalDate hoje = LocalDate.now();
        boolean criou = false;

        criou |= criarSeAusente(titulosExistentes, usuarioId,
                "Começar minha jornada no cinema", 5, hoje.plusDays(30));
        criou |= criarSeAusente(titulosExistentes, usuarioId,
                "Descobrir novos favoritos", 10, hoje.plusDays(90));
        criou |= criarSeAusente(titulosExistentes, usuarioId,
                "Desafio cinéfilo do ano", 25, LocalDate.of(hoje.getYear() + 1, 1, 31));

        return criou;
    }

    private boolean criarSeAusente(
            Set<String> titulosExistentes,
            UsuarioId usuarioId,
            String titulo,
            int quantidadeAlvo,
            LocalDate prazo) {
        if (titulosExistentes.contains(titulo)) {
            return false;
        }
        criar(usuarioId, titulo, quantidadeAlvo, prazo);
        titulosExistentes.add(titulo);
        return true;
    }

    private void criar(UsuarioId usuarioId, String titulo, int quantidadeAlvo, LocalDate prazo) {
        Meta meta = new Meta(
                new MetaId(geradorId.gerarProximoIdMeta()),
                usuarioId,
                titulo,
                quantidadeAlvo,
                prazo);
        repositorio.salvar(meta);
    }
}
