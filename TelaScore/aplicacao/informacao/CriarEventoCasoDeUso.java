package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.servico;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.informacao.dto.CriarEventoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.servico.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.Evento;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.informacao.evento.EventoRepositorio;

public class CriarEventoCasoDeUso {

	private final EventoRepositorio eventoRepositorio;
	private final GeradorId geradorId;

	public CriarEventoCasoDeUso(EventoRepositorio eventoRepositorio, GeradorId geradorId) {
		this.eventoRepositorio = eventoRepositorio;
		this.geradorId = geradorId;
	}

	public void executar(CriarEventoComando comando) {
		// 1. Instancia os Objetos de Valor
		UsuarioId criadorId = new UsuarioId(comando.criadorId());
		EventoId novoEventoId = new EventoId(geradorId.gerarProximoIdEvento());
		
		// 2. Cria a entidade de Domínio
		Evento evento = new Evento(novoEventoId, criadorId, comando.titulo(), comando.dataHora());
		evento.setDescricao(comando.descricao());
		
		// 3. Salva no banco de dados
		eventoRepositorio.salvar(evento);
	}
}