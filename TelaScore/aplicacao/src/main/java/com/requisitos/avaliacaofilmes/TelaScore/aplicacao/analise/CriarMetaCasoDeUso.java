package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.Meta;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.meta.MetaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public class CriarMetaCasoDeUso {

	private final MetaRepositorio metaRepositorio;
	private final GeradorId geradorId;

	public CriarMetaCasoDeUso(MetaRepositorio metaRepositorio, GeradorId geradorId) {
		this.metaRepositorio = metaRepositorio;
		this.geradorId = geradorId;
	}

	public void executar(CriarMetaComando comando) {
		// 1. Converte o ID simples que veio da tela para o Objeto de Valor do Domínio
		UsuarioId usuarioId = new UsuarioId(comando.usuarioId());
		
		// 2. Pede um novo ID para a Meta
		MetaId novaMetaId = new MetaId(geradorId.gerarProximoIdMeta());
		
		// 3. Cria a Meta (As regras de negócio, como "prazo não pode ser no passado", são garantidas aqui)
		Meta meta = new Meta(novaMetaId, usuarioId, comando.titulo(), comando.quantidadeAlvo(), comando.dataPrazo());
		
		// 4. Salva no banco de dados
		metaRepositorio.salvar(meta);
	}
}