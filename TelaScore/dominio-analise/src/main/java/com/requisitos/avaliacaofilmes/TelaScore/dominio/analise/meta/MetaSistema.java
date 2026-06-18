package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class MetaSistema {
    private final int id;
    private final String titulo;
    private final int quantidadeAlvo;
    private final int duracaoDias;
    private final int criadaPorUsuarioId;
    private final boolean ativa;

    public MetaSistema(int id, String titulo, int quantidadeAlvo, int duracaoDias, int criadaPorUsuarioId) {
        this(id, titulo, quantidadeAlvo, duracaoDias, criadaPorUsuarioId, true);
    }

    public MetaSistema(int id, String titulo, int quantidadeAlvo, int duracaoDias,
            int criadaPorUsuarioId, boolean ativa) {
        isTrue(id > 0, "O id deve ser positivo");
        notNull(titulo, "O título não pode ser nulo");
        notBlank(titulo, "O título não pode estar em branco");
        isTrue(quantidadeAlvo > 0, "A quantidade alvo deve ser positiva");
        isTrue(duracaoDias > 0, "A duração deve ser positiva");
        this.id = id;
        this.titulo = titulo;
        this.quantidadeAlvo = quantidadeAlvo;
        this.duracaoDias = duracaoDias;
        this.criadaPorUsuarioId = criadaPorUsuarioId;
        this.ativa = ativa;
    }

    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public int getQuantidadeAlvo() { return quantidadeAlvo; }
    public int getDuracaoDias() { return duracaoDias; }
    public int getCriadaPorUsuarioId() { return criadaPorUsuarioId; }
    public boolean isAtiva() { return ativa; }
}
