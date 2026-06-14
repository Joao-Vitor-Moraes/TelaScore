import { createContext, useContext, useState } from 'react';

const AuthContext = createContext(null);

function carregarSessao() {
  try {
    const raw = localStorage.getItem('telascore_sessao');
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [sessao, setSessao] = useState(carregarSessao);

  function login(dados) {
    localStorage.setItem('telascore_sessao', JSON.stringify(dados));
    setSessao(dados);
  }

  function logout() {
    localStorage.removeItem('telascore_sessao');
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
