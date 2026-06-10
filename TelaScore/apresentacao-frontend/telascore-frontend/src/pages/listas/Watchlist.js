import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { listaService } from '../../services/api';

const USUARIO_ID = 3;

export default function Watchlist() {
  const [listas, setListas] = useState([]);
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    listaService.listarPorUsuario(USUARIO_ID)
      .then(todas => setListas(todas.filter(l => l.tipo === 'WATCHLIST')))
      .catch(() => setErro('Erro ao carregar watchlists.'));
  }, []);

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <h2 style={styles.titulo}>Minhas Watchlists</h2>
          <button style={styles.botaoCriar} onClick={() => navigate('/listas/nova?tipo=WATCHLIST')}>+ Criar Watchlist</button>
        </div>

        {erro && <p style={styles.erro}>{erro}</p>}

        {listas.length === 0 && !erro && (
          <p style={styles.vazio}>Você ainda não tem watchlists. Crie uma!</p>
        )}

        <div style={styles.grid}>
          {listas.map(lista => (
            <div key={lista.id} style={styles.card} onClick={() => navigate(`/listas/${lista.id}`)}>
              <h3 style={styles.nomeLista}>{lista.nome}</h3>
              <span style={styles.tag}>Watchlist</span>
              <p style={styles.filmes}>{lista.quantidadeTotalDeFilmes} filme(s) para assistir</p>
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
    backgroundColor: '#0f9b8e',
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
    borderLeft: '3px solid #0f9b8e',
  },
  nomeLista: {
    margin: '0 0 8px 0',
    fontSize: '16px',
  },
  tag: {
    backgroundColor: '#0f9b8e',
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
