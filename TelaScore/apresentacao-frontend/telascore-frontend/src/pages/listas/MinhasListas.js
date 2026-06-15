import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { listaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function MinhasListas() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const [listas, setListas] = useState([]);
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    listaService.listarPorUsuario(USUARIO_ID, USUARIO_ID)
      .then(setListas)
      .catch(() => setErro('Erro ao carregar listas.'));
  }, []);

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <h2 style={styles.titulo}>Minhas Listas</h2>
          <button style={styles.botaoCriar} onClick={() => navigate('/listas/nova?tipo=NORMAL')}>+ Criar Lista</button>
        </div>

        {erro && <p style={styles.erro}>{erro}</p>}

        {listas.length === 0 && !erro && (
          <p style={styles.vazio}>Você ainda não tem listas. Crie uma!</p>
        )}

        <div style={styles.grid}>
          {listas.filter(l => l.tipo !== 'WATCHLIST').map(lista => (
            <div key={lista.id} style={styles.card} onClick={() => navigate(`/listas/${lista.id}`)}>
              <h3 style={styles.nomeLista}>{lista.nome}</h3>
              <span style={styles.tag}>Normal</span>
              <p style={styles.filmes}>{lista.quantidadeTotalDeFilmes} filme(s)</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

const styles = {
  pagina: {
    minHeight: '100vh',
    backgroundColor: '#0f3460',
    color: 'white',
  },
  conteudo: {
    padding: '32px',
  },
  cabecalho: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '24px',
  },
  titulo: {
    margin: 0,
    fontSize: '24px',
  },
  botaoCriar: {
    backgroundColor: '#e94560',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold',
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
    gap: '16px',
  },
  card: {
    backgroundColor: '#16213e',
    borderRadius: '12px',
    padding: '20px',
    cursor: 'pointer',
    transition: 'transform 0.2s',
  },
  nomeLista: {
    margin: '0 0 8px 0',
    fontSize: '16px',
  },
  tag: {
    backgroundColor: '#e94560',
    borderRadius: '4px',
    padding: '2px 8px',
    fontSize: '12px',
  },
  filmes: {
    marginTop: '12px',
    fontSize: '13px',
    color: '#aaa',
  },
  vazio: {
    color: '#aaa',
    textAlign: 'center',
    marginTop: '60px',
  },
  erro: {
    color: '#e94560',
  },
};
