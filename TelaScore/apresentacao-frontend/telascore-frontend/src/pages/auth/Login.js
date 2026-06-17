import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function Login() {
  const [modo, setModo] = useState('login');
  const [etapaRegistro, setEtapaRegistro] = useState(1);
  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [apelido, setApelido] = useState('');
  const [biografia, setBiografia] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [erro, setErro] = useState(null);
  const [carregando, setCarregando] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);

    if (modo === 'registro' && etapaRegistro === 1) {
      setEtapaRegistro(2);
      return;
    }

    setCarregando(true);
    try {
      const dados = modo === 'login'
        ? await authService.login(email, senha)
        : await authService.registrar({
          nome,
          email,
          senha,
          apelido: apelido || null,
          biografia: biografia || null,
          avatarUrl: avatarUrl || null,
        });
      login(dados);
      navigate(dados.papel === 'ADMIN' ? '/filmes' : '/');
    } catch {
      setErro(modo === 'login' ? 'Email ou senha invalidos.' : 'Erro ao criar conta. Tente novamente.');
    } finally {
      setCarregando(false);
    }
  }

  function alternarModo() {
    setModo(m => m === 'login' ? 'registro' : 'login');
    setErro(null);
    setEtapaRegistro(1);
    setNome('');
    setEmail('');
    setSenha('');
    setApelido('');
    setBiografia('');
    setAvatarUrl('');
  }

  function voltarEtapa() {
    setErro(null);
    setEtapaRegistro(1);
  }

  return (
    <div style={styles.pagina}>
      <div style={styles.card}>
        <h1 style={styles.logo}>TelaScore</h1>
        <p style={styles.subtitulo}>
          {modo === 'login' ? 'Entre na sua conta' : etapaRegistro === 1 ? 'Dados de acesso' : 'Dados do usuario'}
        </p>

        {modo === 'registro' && (
          <div style={styles.steps}>
            <span style={{ ...styles.step, ...(etapaRegistro === 1 ? styles.stepAtivo : styles.stepFeito) }}>1</span>
            <span style={styles.linhaStep} />
            <span style={{ ...styles.step, ...(etapaRegistro === 2 ? styles.stepAtivo : {}) }}>2</span>
          </div>
        )}

        <form onSubmit={handleSubmit} style={styles.form}>
          {modo === 'registro' && etapaRegistro === 1 && (
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

          {(modo === 'login' || etapaRegistro === 1) && (
            <>
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
                placeholder="********"
                required
              />
            </>
          )}

          {modo === 'registro' && etapaRegistro === 2 && (
            <>
              <label style={styles.label}>APELIDO</label>
              <input
                style={styles.input}
                type="text"
                value={apelido}
                onChange={e => setApelido(e.target.value)}
                placeholder="Como voce quer aparecer"
              />

              <label style={styles.label}>URL DA IMAGEM</label>
              <input
                style={styles.input}
                type="url"
                value={avatarUrl}
                onChange={e => setAvatarUrl(e.target.value)}
                placeholder="https://exemplo.com/foto.jpg"
              />

              <label style={styles.label}>BIOGRAFIA</label>
              <textarea
                style={{ ...styles.input, ...styles.textarea }}
                value={biografia}
                onChange={e => setBiografia(e.target.value)}
                placeholder="Fale um pouco sobre voce"
              />
            </>
          )}

          {erro && <p style={styles.erro}>{erro}</p>}

          <div style={styles.acoesForm}>
            {modo === 'registro' && etapaRegistro === 2 && (
              <button type="button" style={styles.botaoVoltar} onClick={voltarEtapa} disabled={carregando}>
                VOLTAR
              </button>
            )}

            <button type="submit" style={styles.botao} disabled={carregando}>
              {carregando
                ? (modo === 'login' ? 'Entrando...' : 'Criando conta...')
                : (modo === 'login' ? 'ENTRAR' : etapaRegistro === 1 ? 'CONTINUAR' : 'CRIAR CONTA')}
            </button>
          </div>
        </form>

        <p style={styles.trocar}>
          {modo === 'login' ? 'Nao tem conta?' : 'Ja tem conta?'}{' '}
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
    padding: '24px',
  },
  card: {
    backgroundColor: '#16213e',
    borderRadius: '12px',
    padding: '38px 40px',
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
    margin: '0 0 18px 0',
  },
  steps: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '10px',
    marginBottom: '14px',
  },
  step: {
    width: '28px',
    height: '28px',
    borderRadius: '50%',
    border: '1px solid #2a2a4a',
    color: '#aaa',
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '12px',
    fontWeight: 'bold',
  },
  stepAtivo: {
    borderColor: '#e94560',
    color: 'white',
    backgroundColor: '#e94560',
  },
  stepFeito: {
    borderColor: '#10b981',
    color: '#10b981',
  },
  linhaStep: {
    width: '48px',
    height: '1px',
    backgroundColor: '#2a2a4a',
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
    fontFamily: 'inherit',
  },
  textarea: {
    minHeight: '82px',
    resize: 'vertical',
  },
  acoesForm: {
    display: 'flex',
    gap: '10px',
    marginTop: '20px',
  },
  botao: {
    padding: '14px',
    borderRadius: '8px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    fontSize: '14px',
    fontWeight: 'bold',
    cursor: 'pointer',
    letterSpacing: '1px',
    flex: 1,
  },
  botaoVoltar: {
    padding: '14px',
    borderRadius: '8px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: '#aaa',
    fontSize: '14px',
    fontWeight: 'bold',
    cursor: 'pointer',
    letterSpacing: '1px',
    flex: 1,
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
