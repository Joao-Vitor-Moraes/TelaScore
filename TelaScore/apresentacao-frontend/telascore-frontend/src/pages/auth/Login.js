import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function Login() {
  const [modo, setModo] = useState('login');
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    setCarregando(true);
    try {
      const dados = modo === 'login'
        ? await authService.login(email, senha)
        : await authService.registrar(nome, email, senha);
      login(dados);
      navigate(dados.papel === 'ADMIN' ? '/admin/solicitacoes' : '/');
    } catch {
      setErro(modo === 'login' ? 'Email ou senha inválidos.' : 'Erro ao criar conta. Tente novamente.');
    } finally {
      setCarregando(false);
    }
  }

  function alternarModo() {
    setModo(m => m === 'login' ? 'registro' : 'login');
    setErro(null);
  }

  return (
    <div style={styles.pagina}>
      <div style={styles.card}>
        <h1 style={styles.logo}>TelaScore</h1>
        <p style={styles.subtitulo}>
          {modo === 'login' ? 'Entre na sua conta' : 'Crie sua conta'}
        </p>

        <form onSubmit={handleSubmit} style={styles.form}>
          {modo === 'registro' && (
            <>
              <label style={styles.label}>NOME</label>
              <input
                style={styles.input}
                type="text"
                value={nome}
                onChange={e => setNome(e.target.value)}
                placeholder="Seu nome"
                required
              />
            </>
          )}

          <label style={styles.label}>EMAIL</label>
          <input
            style={styles.input}
            type="email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            placeholder="seu@email.com"
            required
          />

          <label style={styles.label}>SENHA</label>
          <input
            style={styles.input}
            type="password"
            value={senha}
            onChange={e => setSenha(e.target.value)}
            placeholder="••••••••"
            required
          />

          {erro && <p style={styles.erro}>{erro}</p>}

          <button type="submit" style={styles.botao} disabled={carregando}>
            {carregando
              ? (modo === 'login' ? 'Entrando...' : 'Criando conta...')
              : (modo === 'login' ? 'ENTRAR' : 'CRIAR CONTA')}
          </button>
        </form>

        <p style={styles.trocar}>
          {modo === 'login' ? 'Não tem conta?' : 'Já tem conta?'}{' '}
          <button style={styles.linkTrocar} onClick={alternarModo}>
            {modo === 'login' ? 'Registre-se' : 'Entre aqui'}
          </button>
        </p>
      </div>
    </div>
  );
}

const styles = {
  pagina: {
    minHeight: '100vh',
    backgroundColor: '#0f3460',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  card: {
    backgroundColor: '#16213e',
    borderRadius: '16px',
    padding: '48px 40px',
    width: '360px',
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
  },
  logo: {
    color: '#e94560',
    fontSize: '28px',
    fontWeight: 'bold',
    margin: '0 0 4px 0',
    textAlign: 'center',
  },
  subtitulo: {
    color: '#aaa',
    fontSize: '14px',
    textAlign: 'center',
    margin: '0 0 24px 0',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
  },
  label: {
    fontSize: '11px',
    fontWeight: 'bold',
    color: '#aaa',
    letterSpacing: '1px',
    marginTop: '8px',
  },
  input: {
    padding: '12px 14px',
    borderRadius: '8px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
  },
  botao: {
    marginTop: '20px',
    padding: '14px',
    borderRadius: '8px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    fontSize: '14px',
    fontWeight: 'bold',
    cursor: 'pointer',
    letterSpacing: '1px',
  },
  erro: {
    color: '#e94560',
    fontSize: '13px',
    margin: '4px 0 0 0',
  },
  trocar: {
    color: '#aaa',
    fontSize: '13px',
    textAlign: 'center',
    marginTop: '20px',
  },
  linkTrocar: {
    background: 'none',
    border: 'none',
    color: '#e94560',
    cursor: 'pointer',
    fontSize: '13px',
    textDecoration: 'underline',
    padding: 0,
  },
};
