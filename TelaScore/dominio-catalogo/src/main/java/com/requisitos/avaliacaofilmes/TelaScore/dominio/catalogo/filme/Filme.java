package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notEmpty;
import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorId;

public class Filme {
	private final FilmeId id;

	private String titulo;
	private String sinopse;
	private Integer anoLancamento;

	private final List<DiretorId> diretores = new ArrayList<>();

	public Filme(FilmeId id, String titulo, String sinopse, Integer anoLancamento, List<DiretorId> diretores) {
		notNull(id, "O id não pode ser nulo");
		this.id = id;

		setTitulo(titulo);
		setSinopse(sinopse);
		setAnoLancamento(anoLancamento);
		setDiretores(diretores);
	}

	public FilmeId getId() { return id; }

	public void setTitulo(String titulo) {
		notNull(titulo, "O título não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");
		this.titulo = titulo;
	}
	public String getTitulo() { return titulo; }

	public void setSinopse(String sinopse) {
		if (sinopse != null) {
			notBlank(sinopse, "A sinopse, se informada, não pode estar em branco");
		}
		this.sinopse = sinopse;
	}
	public String getSinopse() { return sinopse; }

	public void setAnoLancamento(Integer anoLancamento) {
		notNull(anoLancamento, "O ano de lançamento não pode ser nulo");
		this.anoLancamento = anoLancamento;
	}
	public Integer getAnoLancamento() { return anoLancamento; }

	private void setDiretores(Collection<DiretorId> diretores) {
		notNull(diretores, "A lista de diretores não pode ser nula");
		notEmpty(diretores, "O filme deve ter pelo menos um diretor");

		for (var diretor : diretores) {
			adicionarDiretor(diretor);
		}
	}

	public Collection<DiretorId> getDiretores() {
		var copia = new ArrayList<DiretorId>();
		copia.addAll(diretores);
		return copia;
	}

	public void adicionarDiretor(DiretorId diretor) {
		notNull(diretor, "O diretor não pode ser nulo");
		diretores.add(diretor);
	}

	@Override
	public String toString() {
		return titulo + " (" + anoLancamento + ")";
	}
}