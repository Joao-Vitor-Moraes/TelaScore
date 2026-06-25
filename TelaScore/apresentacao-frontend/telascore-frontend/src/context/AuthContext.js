import { createContext, useContext, useEffect, useState } from 'react';
import { carregarSessaoSalva, limparSessao, salvarSessao, SESSAO_STORAGE_KEY } from '../services/sessaoStorage';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [sessao, setSessao] = useState(carregarSessaoSalva);

  useEffect(() => {
    function sincronizarSessao(event) {
      if (event.key === SESSAO_STORAGE_KEY) {
        setSessao(carregarSessaoSalva());
      }
    }

    window.addEventListener('storage', sincronizarSessao);
    return () => window.removeEventListener('storage', sincronizarSessao);
  }, []);

  function login(dados) {
    setSessao(salvarSessao(dados));
  }

  function logout() {
    limparSessao();
    setSessao(null);
  }

  return (
    <AuthContext.Provider value={{ sessao, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
