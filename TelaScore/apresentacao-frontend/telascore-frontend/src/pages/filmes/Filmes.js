import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { filmeService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function Filmes() {
  const { sessao } = useAuth();
  const isAdmin = sessao.papel === 'ADMIN';
  const [filmes, setFilmes] = useState([]);
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    filmeService.listar()
      .then(setFilmes)
      .catch(() => setErro('Não foi possível carregar os filmes.'));
  }, []);

  async function handleRemover(id) {
    if (!window.confirm('Remover este filme?')) return;
    try {
      await filmeService.remover(id);
      setFilmes(prev => prev.filter(f => f.id !== id));
    } catch {
      alert('Erro ao remover filme.');
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <h2 style={styles.titulo}>Filmes</h2>
          {isAdmin && (
            <button style={styles.botaoCadastrar} onClick={() => navigate('/filmes/novo')}>
              + Cadastrar filme
            </button>
          )}
        </div>

        {erro && <p style={styles.erro}>{erro}</p>}

        {filmes.length === 0 && !erro && (
          <p style={styles.vazio}>Nenhum filme cadastrado ainda.</p>
        )}

        <div style={styles.lista}>
          {filmes.map(filme => (
            <div key={filme.id} style={styles.card}>
              <div style={styles.cardEsquerda}>
                {filme.imagemUrl
                  ? <img src={filme.imagemUrl} alt={filme.titulo} style={styles.poster} />
                  : <div style={styles.posterPlaceholder}>🎬</div>
                }
              </div>
              <div style={styles.cardInfo}>
                <span style={styles.cardTitulo}>{filme.titulo}</span>
                <span style={styles.cardSub}>
                  {filme.anoLancamento}{filme.nomeDiretor ? ` · ${filme.nomeDiretor}` : ''}
                </span>
                <span style={styles.cardNota}>
                  {filme.mediaNotas > 0 ? `⭐ ${filme.mediaNotas.toFixed(1)}` : 'Sem avaliações'}
                </span>
              </div>
              <div style={styles.cardAcoes}>
                <button style={styles.btnVer} onClick={() => navigate(`/filmes/${filme.id}`)}>
                  Ver
                </button>
                {isAdmin && (
                  <>
                    <button style={styles.btnEditar} onClick={() => navigate(`/filmes/${filme.id}/editar`)}>
                      Editar
                    </button>
                    <button style={styles.btnRemover} onClick={() => handleRemover(filme.id)}>
                      Remover
                    </button>
                  </>
                )}
              </div>
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
    maxWidth: '800px',
    margin: '0 auto',
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
  botaoCadastrar: {
    backgroundColor: '#e94560',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold',
  },
  lista: {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
  },
  card: {
    backgroundColor: '#16213e',
    borderRadius: '12px',
    padding: '16px',
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
  },
  cardEsquerda: {
    flexShrink: 0,
  },
  poster: {
    width: '50px',
    height: '70px',
    objectFit: 'cover',
    borderRadius: '6px',
  },
  posterPlaceholder: {
    width: '50px',
    height: '70px',
    backgroundColor: '#0f3460',
    borderRadius: '6px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '20px',
    border: '1px solid #2a2a4a',
  },
  cardInfo: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
    minWidth: 0,
  },
  cardTitulo: {
    fontSize: '16px',
    fontWeight: 'bold',
  },
  cardSub: {
    fontSize: '13px',
    color: '#aaa',
  },
  cardNota: {
    fontSize: '13px',
    color: '#f0a500',
  },
  cardAcoes: {
    display: 'flex',
    gap: '8px',
    flexShrink: 0,
  },
  btnVer: {
    padding: '7px 16px',
    borderRadius: '6px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: 'white',
    cursor: 'pointer',
    fontSize: '13px',
  },
  btnEditar: {
    padding: '7px 16px',
    borderRadius: '6px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: 'white',
    cursor: 'pointer',
    fontSize: '13px',
  },
  btnRemover: {
    padding: '7px 16px',
    borderRadius: '6px',
    border: '1px solid #e94560',
    backgroundColor: 'transparent',
    color: '#e94560',
    cursor: 'pointer',
    fontSize: '13px',
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
