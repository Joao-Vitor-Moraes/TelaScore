package com.requisitos.avaliacaofilmes.TelaScore.aplicacao.social.comunidade;

import com.requisitos.avaliacaofilmes.TelaScore.dominio.identidade.usuario.UsuarioId;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntrarComunidadeProxy implements EntrarComunidade {

    private final EntrarComunidade casoDeUsoReal;
    private final Map<Integer, RegistroAcesso> historicoAcessos = new ConcurrentHashMap<>();

    public EntrarComunidadeProxy(EntrarComunidade casoDeUsoReal) {
        this.casoDeUsoReal = casoDeUsoReal;
    }

    @Override
    public void executar(int comunidadeId, UsuarioId usuarioId) {
        int uid = usuarioId.getId();
        LocalDateTime agora = LocalDateTime.now();

        RegistroAcesso registro = historicoAcessos.get(uid);

        if (registro != null && registro.getMinutoJanela().plusMinutes(1).isAfter(agora)) {
            if (registro.getContador() >= 3) {
                throw new SecurityException("Acesso negado: Limite de requisicoes atingido. Aguarde um minuto para entrar em novas comunidades.");
            }
            registro.incrementar();
        } else {
            historicoAcessos.put(uid, new RegistroAcesso(agora, 1));
        }

        casoDeUsoReal.executar(comunidadeId, usuarioId);
    }

    private static class RegistroAcesso {
        private final LocalDateTime minutoJanela;
        private int contador;

        public RegistroAcesso(LocalDateTime minutoJanela, int contador) {
            this.minutoJanela = minutoJanela;
            this.contador = contador;
        }

        public LocalDateTime getMinutoJanela() { return minutoJanela; }
        public int getContador() { return contador; }
        public void incrementar() { this.contador++; }
    }
}