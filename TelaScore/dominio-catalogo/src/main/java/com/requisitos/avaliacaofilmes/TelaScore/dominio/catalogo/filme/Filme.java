package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.diretor.DiretorId;

public class Filme {
	private final FilmeId id;

	private String titulo;
	private String sinopse;
	private Integer anoLancamento;
	private LocalDate dataEstreia;
	private String imagemUrl;

	private final List<DiretorId> diretores = new ArrayList<>();
	private final List<String> generos = new ArrayList<>();

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

	public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
	public String getImagemUrl() { return imagemUrl; }

	public void setAnoLancamento(Integer anoLancamento) {
		notNull(anoLancamento, "O ano de lançamento não pode ser nulo");
		this.anoLancamento = anoLancamento;
	}
	public Integer getAnoLancamento() { return anoLancamento; }

	public void setDataEstreia(LocalDate dataEstreia) { this.dataEstreia = dataEstreia; }
	public LocalDate getDataEstreia() { return dataEstreia; }

	private void setDiretores(Collection<DiretorId> diretores) {
		notNull(diretores, "A lista de diretores não pode ser nula");
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

	public Collection<String> getGeneros() {
		return new ArrayList<>(generos);
	}

	public void setGeneros(Collection<String> generos) {
		this.generos.clear();
		if (generos == null) {
			return;
		}
		for (String genero : generos) {
			adicionarGenero(genero);
		}
	}

	public void adicionarGenero(String genero) {
		if (genero == null || genero.isBlank()) {
			return;
		}
		String normalizado = normalizarGenero(genero);
		boolean jaExiste = generos.stream()
				.anyMatch(valor -> valor.equalsIgnoreCase(normalizado));
		if (!jaExiste) {
			generos.add(normalizado);
		}
	}

	public boolean possuiGenero(String genero) {
		if (genero == null || genero.isBlank()) {
			return false;
		}
		String normalizado = normalizarGenero(genero);
		return generos.stream().anyMatch(valor -> valor.equalsIgnoreCase(normalizado));
	}

	private String normalizarGenero(String genero) {
		String texto = genero.trim().replace('_', ' ').replace('-', ' ');
		if (texto.isBlank()) {
			return texto;
		}
		String[] partes = texto.toLowerCase(Locale.ROOT).split("\\s+");
		StringBuilder resultado = new StringBuilder();
		for (String parte : partes) {
			if (parte.isBlank()) {
				continue;
			}
			if (resultado.length() > 0) {
				resultado.append(' ');
			}
			resultado.append(Character.toUpperCase(parte.charAt(0)));
			if (parte.length() > 1) {
				resultado.append(parte.substring(1));
			}
		}
		return resultado.toString();
	}

	@Override
	public String toString() {
		return titulo + " (" + anoLancamento + ")";
	}
}
