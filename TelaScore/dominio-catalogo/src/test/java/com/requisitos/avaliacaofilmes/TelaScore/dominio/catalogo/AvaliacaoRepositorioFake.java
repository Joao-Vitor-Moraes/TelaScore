package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvaliacaoRepositorioFake implements AvaliacaoRepositorio {

    private final Map<Integer, Avaliacao> armazenamento = new HashMap<>();

    @Override
    public void salvar(Avaliacao avaliacao) {
        armazenamento.put(avaliacao.getId().getId(), avaliacao);
    }

    @Override
    public Avaliacao obter(AvaliacaoId id) {
        return armazenamento.get(id.getId());
    }

    @Override
    public void remover(AvaliacaoId id) {
        armazenamento.remove(id.getId());
    }

    @Override
    public List<Avaliacao> pesquisarPorFilme(FilmeId filmeId) {
        return armazenamento.values().stream()
                .filter(a -> a.getFilmeId().equals(filmeId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Avaliacao> pesquisarPorUsuario(UsuarioId usuarioId) {
        return armazenamento.values().stream()
                .filter(a -> a.getUsuarioId().equals(usuarioId))
                .collect(Collectors.toList());
    }
    @Override
    public List<Avaliacao> pesquisarPublicasPorFilme(FilmeId filmeId) {
        return armazenamento.values().stream()
                .filter(a -> a.getFilmeId().equals(filmeId) && "PUBLICA".equals(a.getVisibilidade()))
                .collect(Collectors.toList());
    }
}