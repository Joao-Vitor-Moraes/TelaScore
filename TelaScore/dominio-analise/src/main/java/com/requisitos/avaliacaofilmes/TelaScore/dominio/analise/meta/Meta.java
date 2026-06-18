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
	private Integer metaSistemaId;
	private boolean pontosConcedidos;

	public Meta(MetaId id, UsuarioId usuarioId, String titulo, int quantidadeAlvo, LocalDate dataPrazo) {
		notNull(id, "O id da meta não pode ser nulo");
		notNull(usuarioId, "O id do utilizador não pode ser nulo");
		notNull(dataPrazo, "A data de prazo não pode ser nula");
		
		isTrue(dataPrazo.isAfter(LocalDate.now()) || dataPrazo.isEqual(LocalDate.now()), 
				"O prazo deve ser uma data futura ou o dia de hoje");

		this.id = id;
		this.usuarioId = usuarioId;
		this.dataPrazo = dataPrazo;
		this.quantidadeAtual = 0;
		this.status = StatusMeta.EM_ANDAMENTO;
		this.metaSistemaId = null;
		this.pontosConcedidos = false;

		setTitulo(titulo);
		setQuantidadeAlvo(quantidadeAlvo);
	}

	public MetaId getId() { return id; }
	public UsuarioId getUsuarioId() { return usuarioId; }
	public int getQuantidadeAtual() { return quantidadeAtual; }
	public LocalDate getDataPrazo() { return dataPrazo; }
	public StatusMeta getStatus() { return status; }
	public Integer getMetaSistemaId() { return metaSistemaId; }
	public boolean isPontosConcedidos() { return pontosConcedidos; }

	public void vincularMetaSistema(int metaSistemaId) {
		isTrue(metaSistemaId > 0, "O id da meta de sistema deve ser positivo");
		this.metaSistemaId = metaSistemaId;
	}

	public void marcarPontosConcedidos() {
		this.pontosConcedidos = true;
	}

	public void setTitulo(String titulo) {
		notNull(titulo, "O título não pode ser nulo");
		notBlank(titulo, "O título não pode estar em branco");
		this.titulo = titulo;
	}
	public String getTitulo() { return titulo; }

	public void setQuantidadeAlvo(int quantidadeAlvo) {
		isTrue(quantidadeAlvo > 0, "A quantidade alvo deve ser maior que zero");
		this.quantidadeAlvo = quantidadeAlvo;
		
		if (this.status == StatusMeta.EM_ANDAMENTO && this.quantidadeAtual >= this.quantidadeAlvo) {
			this.quantidadeAtual = this.quantidadeAlvo;
			this.status = StatusMeta.CONCLUIDA;
		} else if (this.status == StatusMeta.CONCLUIDA && this.quantidadeAtual < this.quantidadeAlvo) {
			this.status = StatusMeta.EM_ANDAMENTO;
		}
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

	public void removerProgresso(int quantidade) {
		isTrue(quantidade > 0, "A quantidade a remover deve ser maior que zero");
		
		if (this.status == StatusMeta.FALHADA || this.status == StatusMeta.CANCELADA) {
			throw new IllegalStateException("Não é possível alterar o progresso de uma meta encerrada.");
		}

		this.quantidadeAtual -= quantidade;
		
		if (this.quantidadeAtual < 0) {
			this.quantidadeAtual = 0;
		}

		if (this.status == StatusMeta.CONCLUIDA && this.quantidadeAtual < this.quantidadeAlvo) {
			this.status = StatusMeta.EM_ANDAMENTO;
		}
	}

	public void estenderPrazo(LocalDate novoPrazo) {
        if (this.status != StatusMeta.EM_ANDAMENTO) {
            throw new IllegalStateException("Apenas metas em andamento podem ter o prazo estendido.");
        }
        
        notNull(novoPrazo, "O novo prazo não pode ser nulo");
        isTrue(novoPrazo.isAfter(this.dataPrazo), "O novo prazo deve ser posterior ao prazo atual");
        
        this.dataPrazo = novoPrazo;
    }

	public void cancelar() {
		if (this.status != StatusMeta.EM_ANDAMENTO) {
			throw new IllegalStateException("Apenas metas em andamento podem ser canceladas.");
		}
		this.status = StatusMeta.CANCELADA;
	}
	
	public void marcarComoFalhada() {
		if (this.status != StatusMeta.EM_ANDAMENTO) {
			throw new IllegalStateException("Apenas metas em andamento podem ser marcadas como falhadas.");
		}
		this.status = StatusMeta.FALHADA;
	}
	public Meta(MetaId id, UsuarioId usuarioId, String titulo, int quantidadeAlvo, int quantidadeAtual,
			LocalDate dataPrazo, StatusMeta status, Integer metaSistemaId, boolean pontosConcedidos) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.titulo = titulo;
        this.quantidadeAlvo = quantidadeAlvo;
        this.quantidadeAtual = quantidadeAtual;
        this.dataPrazo = dataPrazo;
        this.status = status;
        this.metaSistemaId = metaSistemaId;
        this.pontosConcedidos = pontosConcedidos;
    }

	public Meta(MetaId id, UsuarioId usuarioId, String titulo, int quantidadeAlvo, int quantidadeAtual,
			LocalDate dataPrazo, StatusMeta status) {
		this(id, usuarioId, titulo, quantidadeAlvo, quantidadeAtual, dataPrazo, status, null, false);
	}
}
