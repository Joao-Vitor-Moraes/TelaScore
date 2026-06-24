package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.meta;

import java.time.LocalDate;
import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistema;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaSistemaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.NotificacaoMetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.StatusMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.TipoMeta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.AcaoPontuada;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.EstrategiaPontuacaoProvider;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.Pontos;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recompensa.PontuacaoServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class ListarMetasPorUsuarioCasoDeUso {
    private final MetaRepositorio repositorio;
    private final MetaSistemaRepositorio metasSistema;
    private final GeradorId geradorId;
    private final AvaliacaoRepositorio avaliacaoRepositorio;
    private final FilmeRepositorio filmeRepositorio;
    private final NotificacaoMetaRepositorio notificacoes;
    private final PontuacaoServico pontuacao;
    private final EstrategiaPontuacaoProvider estrategias;

    public ListarMetasPorUsuarioCasoDeUso(MetaRepositorio repositorio,
            MetaSistemaRepositorio metasSistema, GeradorId geradorId,
            AvaliacaoRepositorio avaliacaoRepositorio, FilmeRepositorio filmeRepositorio,
            NotificacaoMetaRepositorio notificacoes,
            PontuacaoServico pontuacao,
            EstrategiaPontuacaoProvider estrategias) {
        this.repositorio = repositorio;
        this.metasSistema = metasSistema;
        this.geradorId = geradorId;
        this.avaliacaoRepositorio = avaliacaoRepositorio;
        this.filmeRepositorio = filmeRepositorio;
        this.notificacoes = notificacoes;
        this.pontuacao = pontuacao;
        this.estrategias = estrategias;
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
        sincronizarProgressoPorReviews(id, metas);
        return metas.stream().map(MetaResumo::de).toList();
    }

    private void sincronizarProgressoPorReviews(UsuarioId usuarioId, List<Meta> metas) {
        List<Avaliacao> avaliacoes = avaliacaoRepositorio.pesquisarPorUsuario(usuarioId);
        for (Meta meta : metas) {
            int progresso = calcularProgresso(meta, avaliacoes);
            boolean salvar = false;
            if (meta.getQuantidadeAtual() != progresso) {
                meta.redefinirProgresso(progresso);
                salvar = true;
            }
            if (!meta.isPontosConcedidos() && meta.getStatus() == StatusMeta.CONCLUIDA) {
                meta.marcarPontosConcedidos();
                Pontos pontos = pontuacao.concederPontos(
                        meta.getUsuarioId(),
                        AcaoPontuada.COMPLETAR_META,
                        estrategias.obter(AcaoPontuada.COMPLETAR_META));
                if (meta.isNotificacaoAtiva()) {
                    notificacoes.criar(meta.getUsuarioId(), meta.getId(), meta.getTitulo(), pontos.getQuantidade());
                }
                salvar = true;
            }
            if (salvar) {
                repositorio.salvar(meta);
            }
        }
    }

    private int calcularProgresso(Meta meta, List<Avaliacao> avaliacoes) {
        if (meta.getTipo() == TipoMeta.RESENHAS) {
            return (int) avaliacoes.stream()
                    .filter(avaliacao -> avaliacao.getResenha() != null && !avaliacao.getResenha().isBlank())
                    .map(avaliacao -> avaliacao.getFilmeId().getCodigo())
                    .distinct()
                    .count();
        }
        if (meta.getGeneroAlvo() != null && !meta.getGeneroAlvo().isBlank()) {
            String generoAlvo = normalizar(meta.getGeneroAlvo());
            if (generoAlvo.isBlank()) {
                return 0;
            }
            return (int) avaliacoes.stream()
                    .map(avaliacao -> filmeRepositorio.obter(avaliacao.getFilmeId()))
                    .filter(filme -> filmePossuiGenero(filme, generoAlvo))
                    .map(filme -> filme.getId().getCodigo())
                    .distinct()
                    .count();
        }
        return (int) avaliacoes.stream()
                .map(avaliacao -> avaliacao.getFilmeId().getCodigo())
                .distinct()
                .count();
    }

    private boolean filmePossuiGenero(Filme filme, String generoAlvo) {
        return filme != null && filme.getGeneros().stream()
                .map(this::normalizar)
                .anyMatch(genero -> genero.equals(generoAlvo));
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}
