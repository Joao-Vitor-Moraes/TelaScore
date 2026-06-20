package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.analise;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.EnviarRecomendacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.EnviarRecomendacaoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ResponderRecomendacaoCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ResponderRecomendacaoComando;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.ListarRecomendacoesPorUsuarioCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.analise.recomendacao.RecomendacaoResumo;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.identidade.SessaoUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AvaliarFilmeCasoDeUso;
import com.requisitos.avaliacaofilmes.TelaScore.aplicacao.catalogo.AvaliarFilmeComando;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.Recomendacao;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.RecomendacaoRepositorio;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.analise.recomendacao.TipoConteudo;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.Usuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioRepositorio;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/recomendacoes")
public class RecomendacaoController {

    private final EnviarRecomendacaoCasoDeUso enviarRecomendacaoCasoDeUso;
    private final ResponderRecomendacaoCasoDeUso responderRecomendacaoCasoDeUso;
    private final ListarRecomendacoesPorUsuarioCasoDeUso listarRecomendacoesCasoDeUso;
    private final RecomendacaoRepositorio recomendacoes;
    private final SessaoUsuario sessao;
    private final UsuarioRepositorio usuarios;
    private final AvaliarFilmeCasoDeUso avaliarFilme;

    public RecomendacaoController(EnviarRecomendacaoCasoDeUso enviarRecomendacaoCasoDeUso,
                                  ResponderRecomendacaoCasoDeUso responderRecomendacaoCasoDeUso,
                                  ListarRecomendacoesPorUsuarioCasoDeUso listarRecomendacoesCasoDeUso,
                                  RecomendacaoRepositorio recomendacoes,
                                  SessaoUsuario sessao,
                                  UsuarioRepositorio usuarios,
                                  AvaliarFilmeCasoDeUso avaliarFilme) {
        this.enviarRecomendacaoCasoDeUso = enviarRecomendacaoCasoDeUso;
        this.responderRecomendacaoCasoDeUso = responderRecomendacaoCasoDeUso;
        this.listarRecomendacoesCasoDeUso = listarRecomendacoesCasoDeUso;
        this.recomendacoes = recomendacoes;
        this.sessao = sessao;
        this.usuarios = usuarios;
        this.avaliarFilme = avaliarFilme;
    }

    @GetMapping
    public List<RecomendacaoRecebidaResponse> listar() {
        return listarRecomendacoesCasoDeUso.executar(usuarioAtual().getId().getId()).stream()
                .map(this::resumirRecebida)
                .toList();
    }

    @GetMapping("/enviadas")
    public List<RecomendacaoEnviadaResponse> listarEnviadas() {
        return recomendacoes.buscarEnviadasPorUsuario(usuarioAtual().getId()).stream()
                .map(this::resumirEnviada)
                .toList();
    }

    @PostMapping
    public ResponseEntity<String> enviarRecomendacao(@RequestBody EnviarRecomendacaoRequest request) {
        try {
            UsuarioLogado remetente = usuarioAtual();
            enviarRecomendacaoCasoDeUso.executar(new EnviarRecomendacaoComando(
                    remetente.getId().getId(),
                    request.destinatarioId(),
                    request.conteudoId(),
                    request.tipoConteudo(),
                    request.mensagem()));
            return ResponseEntity.ok("Recomendação enviada com sucesso!");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao enviar recomendação: " + e.getMessage());
        }
    }

    @PutMapping("/reagir")
    public ResponseEntity<String> responderRecomendacao(@RequestBody ResponderRecomendacaoComando comando) {
        try {
            Recomendacao recomendacao = recomendacoes.obter(new RecomendacaoId(comando.recomendacaoId()));
            if (recomendacao == null) {
                throw new IllegalArgumentException("Recomendação não encontrada.");
            }
            if (!usuarioAtual().isMesmoUsuario(recomendacao.getUsuarioId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta recomendação pertence a outro usuário.");
            }
            responderRecomendacaoCasoDeUso.executar(comando);
            return ResponseEntity.ok("Resposta registrada com sucesso!");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao responder recomendação: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/visualizar")
    public ResponseEntity<Void> visualizar(@PathVariable int id) {
        Recomendacao recomendacao = recomendacoes.obter(new RecomendacaoId(id));
        if (recomendacao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não encontrada.");
        }
        if (!usuarioAtual().isMesmoUsuario(recomendacao.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta recomendação pertence a outro usuário.");
        }
        if (recomendacao.getStatus().name().equals("PENDENTE")) {
            recomendacao.marcarComoVisualizada();
            recomendacoes.salvar(recomendacao);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/avaliacao")
    public ResponseEntity<Void> avaliarPosteriormente(@PathVariable int id,
                                                       @RequestBody AvaliacaoPosteriorRequest request) {
        Recomendacao recomendacao = recomendacoes.obter(new RecomendacaoId(id));
        if (recomendacao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recomendação não encontrada.");
        }
        UsuarioLogado usuario = usuarioAtual();
        if (!usuario.isMesmoUsuario(recomendacao.getUsuarioId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta recomendação pertence a outro usuário.");
        }
        if (!recomendacao.getStatus().name().equals("JA_ASSISTI")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Marque a recomendação como Já assisti antes de avaliar.");
        }
        if (recomendacao.getTipoConteudo() != TipoConteudo.FILME) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Apenas recomendações de filmes podem receber uma avaliação.");
        }
        avaliarFilme.executar(new AvaliarFilmeComando(
                Integer.parseInt(recomendacao.getConteudoId()),
                usuario.getId().getId(),
                request.nota(),
                request.comentario(),
                request.visibilidade()));
        recomendacao.registrarAvaliacaoPosterior(request.nota(), request.comentario());
        recomendacoes.salvar(recomendacao);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    private UsuarioLogado usuarioAtual() {
        UsuarioLogado usuario = sessao.obterUsuarioLogado();
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Faça login para acessar recomendações.");
        }
        return usuario;
    }

    private RecomendacaoEnviadaResponse resumirEnviada(Recomendacao recomendacao) {
        Usuario destinatario = usuarios.obter(recomendacao.getUsuarioId());
        return new RecomendacaoEnviadaResponse(
                recomendacao.getId().getId(),
                recomendacao.getConteudoId(),
                recomendacao.getTipoConteudo().name(),
                destinatario == null ? "usuário" : destinatario.getApelido().getValor(),
                recomendacao.getMensagem(),
                recomendacao.getDataGeracao(),
                recomendacao.getStatus().name(),
                recomendacao.getComentarioResposta(),
                recomendacao.getNotaPosterior(),
                recomendacao.getAvaliacaoPosterior());
    }

    private RecomendacaoRecebidaResponse resumirRecebida(RecomendacaoResumo recomendacao) {
        Usuario remetente = recomendacao.remetenteId() == null
                ? null
                : usuarios.obter(new com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId(
                        recomendacao.remetenteId()));
        return new RecomendacaoRecebidaResponse(
                recomendacao.id(),
                recomendacao.conteudoId(),
                recomendacao.tipoConteudo(),
                recomendacao.remetenteId(),
                remetente == null ? null : remetente.getApelido().getValor(),
                recomendacao.mensagem(),
                recomendacao.dataGeracao(),
                recomendacao.status(),
                recomendacao.comentarioResposta(),
                recomendacao.notaPosterior(),
                recomendacao.avaliacaoPosterior());
    }

    public record EnviarRecomendacaoRequest(
            int destinatarioId,
            String conteudoId,
            TipoConteudo tipoConteudo,
            String mensagem) {
    }

    public record RecomendacaoEnviadaResponse(
            int id,
            String conteudoId,
            String tipoConteudo,
            String destinatarioApelido,
            String mensagem,
            java.time.LocalDateTime dataGeracao,
            String status,
            String comentarioResposta,
            Integer notaPosterior,
            String avaliacaoPosterior) {
    }

    public record RecomendacaoRecebidaResponse(
            int id,
            String conteudoId,
            String tipoConteudo,
            Integer remetenteId,
            String remetenteApelido,
            String mensagem,
            java.time.LocalDateTime dataGeracao,
            String status,
            String comentarioResposta,
            Integer notaPosterior,
            String avaliacaoPosterior) {
    }

    public record AvaliacaoPosteriorRequest(int nota, String comentario, String visibilidade) {
    }

}
