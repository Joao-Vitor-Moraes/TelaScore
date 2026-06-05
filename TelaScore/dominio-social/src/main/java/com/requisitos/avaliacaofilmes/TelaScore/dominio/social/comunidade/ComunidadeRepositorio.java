package com.requisitos.avaliacaofilmes.TelaScore.dominio.social.comunidade;

import java.util.List;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;

public interface ComunidadeRepositorio {
	void salvarComunidade(Comunidade comunidade);
	Comunidade obterComunidade(ComunidadeId id);

	List<Comunidade> listarTodas();
	
	void salvarMembro(MembroComunidade membro);
	void removerMembro(ComunidadeId comunidadeId, UsuarioId usuarioId);


	List<ComunidadeUsuarioResumo> buscarComunidadesDoUsuario(UsuarioId uid);

	void atualizarPapelMembro(ComunidadeId cid, UsuarioId uid, PapelComunidade novoPapel);
	
	List<MembroComunidade> buscarMembrosDaComunidade(ComunidadeId comunidadeId);

	boolean verificarSeEhCriador(ComunidadeId cid, UsuarioId uid);

	boolean existeMembro(ComunidadeId cid, UsuarioId uid);

	void excluirComunidade(ComunidadeId cid);

	void salvarMensagem(MensagemComunidade mensagem);

	List<MensagemComunidade> buscarMensagensDaComunidade(ComunidadeId cid);

	MensagemComunidade obterMensagemPorId(int mensagemId);

	void excluirMensagem(int mensagemId);
}