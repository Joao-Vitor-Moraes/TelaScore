package com.requisitos.avaliacaofilmes.TelaScore.apresentacao.seguranca;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.PapelUsuario;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioLogado;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class TokenServico {

    private static final String ALGORITMO = "HmacSHA256";
    private static final String SEGREDO_PADRAO = "TelaScore-token-dev-secret";
    private static final Duration VALIDADE = Duration.ofHours(8);
    private static final Pattern ID_PATTERN = Pattern.compile("\"sub\"\\s*:\\s*(\\d+)");
    private static final Pattern PAPEL_PATTERN = Pattern.compile("\"papel\"\\s*:\\s*\"([A-Z_]+)\"");
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");

    private final String segredo;

    public TokenServico() {
        String segredoAmbiente = System.getenv("TELASCORE_TOKEN_SECRET");
        this.segredo = segredoAmbiente == null || segredoAmbiente.isBlank() ? SEGREDO_PADRAO : segredoAmbiente;
    }

    public TokenGerado gerar(UsuarioLogado usuarioLogado) {
        long expiracao = Instant.now().plus(VALIDADE).getEpochSecond();
        String header = codificarBase64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = codificarBase64Url(String.format(
                "{\"sub\":%d,\"papel\":\"%s\",\"exp\":%d}",
                usuarioLogado.getId().getId(),
                usuarioLogado.getPapel().name(),
                expiracao));

        String conteudoAssinado = header + "." + payload;
        String assinatura = assinar(conteudoAssinado);

        return new TokenGerado(conteudoAssinado + "." + assinatura, VALIDADE.toSeconds());
    }

    public Optional<UsuarioLogado> validar(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        String[] partes = token.split("\\.");
        if (partes.length != 3) {
            return Optional.empty();
        }

        String conteudoAssinado = partes[0] + "." + partes[1];
        String assinaturaEsperada = assinar(conteudoAssinado);
        if (!MessageDigest.isEqual(
                assinaturaEsperada.getBytes(StandardCharsets.UTF_8),
                partes[2].getBytes(StandardCharsets.UTF_8))) {
            return Optional.empty();
        }

        try {
            String payload = new String(Base64.getUrlDecoder().decode(partes[1]), StandardCharsets.UTF_8);
            int id = extrairInteiro(payload, ID_PATTERN);
            String papel = extrairTexto(payload, PAPEL_PATTERN);
            long expiracao = extrairLong(payload, EXP_PATTERN);

            if (Instant.now().getEpochSecond() >= expiracao) {
                return Optional.empty();
            }

            return Optional.of(new UsuarioLogado(new UsuarioId(id), PapelUsuario.valueOf(papel)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private String assinar(String conteudo) {
        try {
            Mac mac = Mac.getInstance(ALGORITMO);
            SecretKeySpec chave = new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), ALGORITMO);
            mac.init(chave);
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao assinar token", e);
        }
    }

    private String codificarBase64Url(String valor) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(valor.getBytes(StandardCharsets.UTF_8));
    }

    private int extrairInteiro(String payload, Pattern pattern) {
        return Math.toIntExact(extrairLong(payload, pattern));
    }

    private long extrairLong(String payload, Pattern pattern) {
        Matcher matcher = pattern.matcher(payload);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Token invalido");
        }
        return Long.parseLong(matcher.group(1));
    }

    private String extrairTexto(String payload, Pattern pattern) {
        Matcher matcher = pattern.matcher(payload);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Token invalido");
        }
        return matcher.group(1);
    }

    public record TokenGerado(String valor, long expiraEmSegundos) {
    }
}
