package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.denuncia;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.GeradorId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.Denuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.DenunciaServico;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.MotivoDenuncia;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.social.denuncia.TipoAlvoDenuncia;

public class RegistrarDenunciaCasoDeUso {
	private final DenunciaServico servico;
	private final GeradorId geradorId;

	public RegistrarDenunciaCasoDeUso(DenunciaServico servico, GeradorId geradorId) {
		this.servico = servico;
		this.geradorId = geradorId;
	}

	public DenunciaResumo executar(RegistrarDenunciaComando comando) {
		Denuncia denuncia = new Denuncia(
			new DenunciaId(geradorId.gerarProximoIdDenuncia()),
			new UsuarioId(comando.denuncianteId()),
			comando.alvoId(),
			TipoAlvoDenuncia.valueOf(comando.tipoAlvo()),
			MotivoDenuncia.valueOf(comando.motivo()),
			comando.descricao()
		);

		servico.registrar(denuncia);
		return DenunciaResumo.de(denuncia);
	}
}
