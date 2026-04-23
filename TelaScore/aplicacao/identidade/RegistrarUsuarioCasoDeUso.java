package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.servico;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.dto.RegistrarUsuarioComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Apelido;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.Perfil;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.perfil.PerfilRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Email;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;

public class RegistrarUsuarioCasoDeUso {
	
	private final UsuarioRepositorio usuarioRepositorio;
	private final PerfilRepositorio perfilRepositorio;
	private final GeradorId geradorId;

	public RegistrarUsuarioCasoDeUso(UsuarioRepositorio usuarioRepositorio, 
									 PerfilRepositorio perfilRepositorio,
									 GeradorId geradorId) {
		this.usuarioRepositorio = usuarioRepositorio;
		this.perfilRepositorio = perfilRepositorio;
		this.geradorId = geradorId;
	}

	public void executar(RegistrarUsuarioComando comando) {
		// 1. Instancia os Objetos de Valor (A validação de domínio dispara aqui!)
		Email email = new Email(comando.email());
		Apelido apelido = new Apelido(comando.apelido());
		
		// 2. Gera os novos IDs através da infraestrutura
		UsuarioId usuarioId = new UsuarioId(geradorId.gerarProximoIdUsuario());
		PerfilId perfilId = new PerfilId(geradorId.gerarProximoIdPerfil());
		
		// 3. Cria a entidade Usuário (Conta de acesso)
		// Por defeito, quem se regista pela primeira vez é um CINEFILO comum
		Usuario novoUsuario = new Usuario(usuarioId, comando.nome(), email, PapelUsuario.CINEFILO);
		
		// 4. Cria a entidade Perfil (A sua cara na rede social)
		Perfil novoPerfil = new Perfil(perfilId, usuarioId, apelido);
		
		// 5. Manda salvar nos repositórios (Orquestração)
		usuarioRepositorio.salvar(novoUsuario);
		perfilRepositorio.salvar(novoPerfil);
		
		// Nota: Numa aplicação real, a encriptação da senha aconteceria algures por aqui 
		// antes de salvar o utilizador, utilizando um serviço de infraestrutura como o BCrypt.
	}
}