package com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.social.denuncia;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.Denuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.MotivoDenuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.StatusDenuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.TipoAlvoDenuncia;
import com.requisitos.avaliacaofilmes.TelaScore.infraestrutura.config.ConexaoBanco;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class DenunciaRepositorioImpl implements DenunciaRepositorio {

	@Override
	public void salvar(Denuncia denuncia) {
		EntityManager em = ConexaoBanco.obterEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			int atualizadas = em.createNativeQuery("""
					UPDATE denuncia
					SET denunciante_id = :denuncianteId,
						alvo_id = :alvoId,
						tipo_alvo = :tipoAlvo,
						motivo = :motivo,
						descricao = :descricao,
						link_ocorrencia = :linkOcorrencia,
						data_criacao = :dataCriacao,
						status = :status
					WHERE id = :id
					""")
				.setParameter("id", denuncia.getId().getId())
				.setParameter("denuncianteId", denuncia.getDenuncianteId().getId())
				.setParameter("alvoId", denuncia.getAlvoId())
				.setParameter("tipoAlvo", denuncia.getTipoAlvo().name())
				.setParameter("motivo", denuncia.getMotivo().name())
				.setParameter("descricao", denuncia.getDescricao())
				.setParameter("linkOcorrencia", denuncia.getLinkOcorrencia())
				.setParameter("dataCriacao", denuncia.getDataCriacao())
				.setParameter("status", denuncia.getStatus().name())
				.executeUpdate();

			if (atualizadas == 0) {
				em.createNativeQuery("""
						INSERT INTO denuncia (
							id, denunciante_id, alvo_id, tipo_alvo, motivo,
							descricao, link_ocorrencia, data_criacao, status
						)
						VALUES (
							:id, :denuncianteId, :alvoId, :tipoAlvo, :motivo,
							:descricao, :linkOcorrencia, :dataCriacao, :status
						)
						""")
					.setParameter("id", denuncia.getId().getId())
					.setParameter("denuncianteId", denuncia.getDenuncianteId().getId())
					.setParameter("alvoId", denuncia.getAlvoId())
					.setParameter("tipoAlvo", denuncia.getTipoAlvo().name())
					.setParameter("motivo", denuncia.getMotivo().name())
					.setParameter("descricao", denuncia.getDescricao())
					.setParameter("linkOcorrencia", denuncia.getLinkOcorrencia())
					.setParameter("dataCriacao", denuncia.getDataCriacao())
					.setParameter("status", denuncia.getStatus().name())
					.executeUpdate();
			}

			tx.commit();
		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw new RuntimeException("Erro ao salvar denuncia no banco: " + mensagemRaiz(e), e);
		} finally {
			em.close();
		}
	}

	@Override
	public Denuncia obter(DenunciaId id) {
		EntityManager em = ConexaoBanco.obterEntityManager();
		try {
			List<?> resultados = em.createNativeQuery("""
					SELECT id, denunciante_id, alvo_id, tipo_alvo, motivo,
					       descricao, link_ocorrencia, data_criacao, status
					FROM denuncia
					WHERE id = :id
					""")
				.setParameter("id", id.getId())
				.getResultList();

			return resultados.isEmpty() ? null : mapearLinhaParaDominio((Object[]) resultados.get(0));
		} finally {
			em.close();
		}
	}

	@Override
	public List<Denuncia> listarPorStatus(StatusDenuncia status) {
		EntityManager em = ConexaoBanco.obterEntityManager();
		try {
			return em.createNativeQuery("""
					SELECT id, denunciante_id, alvo_id, tipo_alvo, motivo,
					       descricao, link_ocorrencia, data_criacao, status
					FROM denuncia
					WHERE status = :status
					ORDER BY data_criacao DESC
					""")
				.setParameter("status", status.name())
				.getResultList()
				.stream()
				.map(resultado -> mapearLinhaParaDominio((Object[]) resultado))
				.toList();
		} finally {
			em.close();
		}
	}

	@Override
	public List<Denuncia> listarPorUsuario(UsuarioId denuncianteId) {
		EntityManager em = ConexaoBanco.obterEntityManager();
		try {
			return em.createNativeQuery("""
					SELECT id, denunciante_id, alvo_id, tipo_alvo, motivo,
					       descricao, link_ocorrencia, data_criacao, status
					FROM denuncia
					WHERE denunciante_id = :denuncianteId
					ORDER BY data_criacao DESC
					""")
				.setParameter("denuncianteId", denuncianteId.getId())
				.getResultList()
				.stream()
				.map(resultado -> mapearLinhaParaDominio((Object[]) resultado))
				.toList();
		} finally {
			em.close();
		}
	}

	@Override
	public boolean existeDenunciaDoUsuarioParaAlvo(UsuarioId denuncianteId, TipoAlvoDenuncia tipoAlvo, int alvoId) {
		EntityManager em = ConexaoBanco.obterEntityManager();
		try {
			Number total = (Number) em.createNativeQuery("""
					SELECT COUNT(*)
					FROM denuncia
					WHERE denunciante_id = :denuncianteId
					  AND tipo_alvo = :tipoAlvo
					  AND alvo_id = :alvoId
					""")
				.setParameter("denuncianteId", denuncianteId.getId())
				.setParameter("tipoAlvo", tipoAlvo.name())
				.setParameter("alvoId", alvoId)
				.getSingleResult();

			return total.longValue() > 0;
		} finally {
			em.close();
		}
	}

	private Denuncia mapearLinhaParaDominio(Object[] linha) {
		return new Denuncia(
			new DenunciaId(((Number) linha[0]).intValue()),
			new UsuarioId(((Number) linha[1]).intValue()),
			((Number) linha[2]).intValue(),
			TipoAlvoDenuncia.valueOf((String) linha[3]),
			MotivoDenuncia.valueOf((String) linha[4]),
			(String) linha[5],
			(String) linha[6],
			converterData(linha[7]),
			StatusDenuncia.valueOf((String) linha[8])
		);
	}

	private LocalDateTime converterData(Object valor) {
		if (valor instanceof LocalDateTime data) {
			return data;
		}
		if (valor instanceof Timestamp timestamp) {
			return timestamp.toLocalDateTime();
		}
		throw new IllegalArgumentException("Data de criacao da denuncia invalida");
	}

	private String mensagemRaiz(Throwable erro) {
		Throwable causa = erro;
		while (causa.getCause() != null) {
			causa = causa.getCause();
		}
		return causa.getMessage();
	}
}
