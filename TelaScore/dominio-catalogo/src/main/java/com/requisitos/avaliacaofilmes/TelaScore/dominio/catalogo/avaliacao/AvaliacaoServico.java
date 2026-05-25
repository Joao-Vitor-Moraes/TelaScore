package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao.observer.AvaliacaoObserver;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class AvaliacaoServico {

    private final AvaliacaoRepositorio avaliacaoRepositorio;
    private final List<AvaliacaoObserver> observers = new ArrayList<>();

    public AvaliacaoServico(AvaliacaoRepositorio avaliacaoRepositorio) {
        notNull(avaliacaoRepositorio, "O repositório de avaliações não pode ser nulo");
        this.avaliacaoRepositorio = avaliacaoRepositorio;
    }

    public void adicionarObserver(AvaliacaoObserver observer) {
        notNull(observer, "O observer não pode ser nulo");
        observers.add(observer);
    }

    public void registrarAvaliacao(Avaliacao avaliacao) {
        notNull(avaliacao, "A avaliação não pode ser nula");

        avaliacaoRepositorio.salvar(avaliacao);

        // Notifica todos os observadores após salvar com sucesso
        for (AvaliacaoObserver observer : observers) {
            observer.aoRegistrarAvaliacao(avaliacao);
        }
    }
}