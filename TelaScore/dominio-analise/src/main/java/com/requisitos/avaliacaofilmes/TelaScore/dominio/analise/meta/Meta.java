package com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDate;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class Meta {
	private final MetaId id;
	private final UsuarioId usuarioId;

	private String titulo;
	private int quantidadeAlvo;
	private int quantidadeAtual;
	private LocalDate dataPrazo;
	private StatusMeta status;

	public Meta(MetaId id, UsuarioId usuarioId, String titulo, int quantidadeAlvo, LocalDate dataPrazo) {
		notNull(id, "O id da meta não pode ser nulo");
		notNull(usuarioId, "O id do utilizador não pode ser nulo");
		notNull(dataPrazo, "A data de prazo não pode ser nula");
		
		// Regra de negócio: A meta deve terminar no futuro
		isTrue(dataPrazo.isAfter(LocalDate.now()) || dataPrazo.isEqual(LocalDate.now()), 
				"O prazo deve ser uma data futura ou o dia de hoje");

		this.id = id;
		this.usuarioId = usuarioId;
		this.dataPrazo = dataPrazo;
		this.quantidadeAtual = 0;
		this.status = StatusMeta.EM_ANDAMENTO;

		setTitulo(titulo);
		setQuantidadeAlvo(quantidadeAlvo);
	}

	public MetaId getId() { return id; }
	public UsuarioId getUsuarioId() { return usuarioId; }
	public int getQuantidadeAtual() { return quantidadeAtual; }
	public LocalDate getDataPrazo() { return dataPrazo; }
	public StatusMeta getStatus() { return status; }

	public void setTitulo(String titulo) {
		notNull(titulo, "O título não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");
		this.titulo = titulo;
	}
	public String getTitulo() { return titulo; }

	public void setQuantidadeAlvo(int quantidadeAlvo) {
		isTrue(quantidadeAlvo > 0, "A quantidade alvo deve ser maior que zero");
		this.quantidadeAlvo = quantidadeAlvo;
	}
	public int getQuantidadeAlvo() { return quantidadeAlvo; }

	public void adicionarProgresso(int quantidade) {
		if (this.status != StatusMeta.EM_ANDAMENTO) {
			throw new IllegalStateException("Apenas metas em andamento podem receber progresso.");
		}
		
		isTrue(quantidade > 0, "A quantidade de progresso a adicionar deve ser maior que zero");

		this.quantidadeAtual += quantidade;

		if (this.quantidadeAtual >= this.quantidadeAlvo) {
			this.quantidadeAtual = this.quantidadeAlvo;
			this.status = StatusMeta.CONCLUIDA;
		}
	}
	
	public void marcarComoFalhada() {
		this.status = StatusMeta.FALHADA;
	}
}