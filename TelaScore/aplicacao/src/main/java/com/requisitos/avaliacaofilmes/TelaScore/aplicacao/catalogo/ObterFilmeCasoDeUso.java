package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.Diretor;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.Filme;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.Avaliacao;

import java.util.List;

public class ObterFilmeCasoDeUso {

    private final FilmeRepositorio filmeRepositorio;
    private final DiretorRepositorio diretorRepositorio;
    private final AvaliacaoRepositorio avaliacaoRepositorio;

    public ObterFilmeCasoDeUso(FilmeRepositorio filmeRepositorio,
                                DiretorRepositorio diretorRepositorio,
                                AvaliacaoRepositorio avaliacaoRepositorio) {
        this.filmeRepositorio = filmeRepositorio;
        this.diretorRepositorio = diretorRepositorio;
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    public FilmeResumo executar(String filmeIdCodigo) {
        FilmeId filmeId = new FilmeId(filmeIdCodigo);

        Filme filme = filmeRepositorio.obter(filmeId);
        if (filme == null) {
            throw new IllegalArgumentException("Filme não encontrado: " + filmeIdCodigo);
        }

        DiretorId primeiroDiretorId = filme.getDiretores().iterator().next();
        Diretor diretor = diretorRepositorio.obter(primeiroDiretorId);
        String nomeDiretor = diretor != null ? diretor.getNome() : "Desconhecido";


        List<Avaliacao> avaliacoes = avaliacaoRepositorio.pesquisarPorFilme(filmeId);
        double media = avaliacoes.stream()
                .mapToInt(a -> a.getNota().getValor())
                .average()
                .orElse(0.0);

        int idNumerico = Integer.parseInt(filme.getId().getCodigo()); 

        return new FilmeResumo(idNumerico, filme.getTitulo(), filme.getAnoLancamento(), nomeDiretor, media);
    }
}