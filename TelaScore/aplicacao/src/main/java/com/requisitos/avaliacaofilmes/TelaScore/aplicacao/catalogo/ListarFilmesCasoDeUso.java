package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo;

import java.util.List;
import java.util.stream.Collectors;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.AvaliacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.CalculadoraMediaAvaliacoes;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.Diretor;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeRepositorio;

public class ListarFilmesCasoDeUso {

    private final FilmeRepositorio filmeRepositorio;
    private final AvaliacaoRepositorio avaliacaoRepositorio;
    private final DiretorRepositorio diretorRepositorio;

    public ListarFilmesCasoDeUso(FilmeRepositorio filmeRepositorio,
                                  AvaliacaoRepositorio avaliacaoRepositorio,
                                  DiretorRepositorio diretorRepositorio) {
        this.filmeRepositorio = filmeRepositorio;
        this.avaliacaoRepositorio = avaliacaoRepositorio;
        this.diretorRepositorio = diretorRepositorio;
    }

    public List<FilmeResumo> executar() {
        CalculadoraMediaAvaliacoes calculadora = new CalculadoraMediaAvaliacoes();
        return filmeRepositorio.listarTodos().stream()
                .map(f -> {
                    double media = calculadora.calcular(
                            avaliacaoRepositorio.pesquisarPorFilme(f.getId()));

                    String nomeDiretor = "";
                    if (!f.getDiretores().isEmpty()) {
                        Diretor diretor = diretorRepositorio.obter(f.getDiretores().iterator().next());
                        if (diretor != null) nomeDiretor = diretor.getNome();
                    }

                    return new FilmeResumo(
                            Integer.parseInt(f.getId().getCodigo()),
                            f.getTitulo(),
                            f.getSinopse(),
                            f.getAnoLancamento(),
                            nomeDiretor,
                            media,
                            f.getImagemUrl(),
                            f.getDataEstreia() != null ? f.getDataEstreia().toString() : null);
                })
                .collect(Collectors.toList());
    }
}
