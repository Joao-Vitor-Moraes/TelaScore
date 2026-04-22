package com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.calendario;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;

public class EntradaCalendario {
	private final FilmeId filmeId;
	private final LocalDate dataEstreiaPrevista;
	private boolean lembreteAtivo;

	public EntradaCalendario(FilmeId filmeId, LocalDate dataEstreiaPrevista) {
		notNull(filmeId, "O id do filme não pode ser nulo");
		notNull(dataEstreiaPrevista, "A data de estreia prevista não pode ser nula");
		
		this.filmeId = filmeId;
		this.dataEstreiaPrevista = dataEstreiaPrevista;
		this.lembreteAtivo = true;
	}

	public FilmeId getFilmeId() { return filmeId; }
	public LocalDate getDataEstreiaPrevista() { return dataEstreiaPrevista; }
	public boolean isLembreteAtivo() { return lembreteAtivo; }

	public void alternarLembrete() {
		this.lembreteAtivo = !this.lembreteAtivo;
	}
}