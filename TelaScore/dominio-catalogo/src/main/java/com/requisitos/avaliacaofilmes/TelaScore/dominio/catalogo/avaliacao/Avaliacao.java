package com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.avaliacao;

import java.time.LocalDate;

import static org.apache.commons.lang3.Validate.notNull;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.catalogo.filme.FilmeId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Avaliacao {
	private final AvaliacaoId id;
	private final FilmeId filmeId;
	private final UsuarioId usuarioId;
	
	private Nota nota;
	private String resenha; // Texto descritivo da avaliação (opcional)
	private final LocalDate dataAvaliacao;

	public Avaliacao(AvaliacaoId id, FilmeId filmeId, UsuarioId usuarioId, Nota nota, String resenha) {
		notNull(id, "O id da avaliação não pode ser nulo");
		notNull(filmeId, "O id do filme não pode ser nulo");
		notNull(usuarioId, "O id do usuário não pode ser nulo");
		notNull(nota, "A nota não pode ser nula");
		
		this.id = id;
		this.filmeId = filmeId;
		this.usuarioId = usuarioId;
		this.dataAvaliacao = LocalDate.now(); // Regista automaticamente a data em que foi criada

		this.nota = nota;
		this.resenha = resenha;
	}

	public AvaliacaoId getId() { return id; }
	public FilmeId getFilmeId() { return filmeId; }
	public UsuarioId getUsuarioId() { return usuarioId; }
	public LocalDate getDataAvaliacao() { return dataAvaliacao; }

	public Nota getNota() { return nota; }

	public void setNota(Nota nota) {
		notNull(nota, "A nota não pode ser nula");
		this.nota = nota;
	}

	public String getResenha() { return resenha; }

	public void setResenha(String resenha) {
		this.resenha = resenha;
	}
}