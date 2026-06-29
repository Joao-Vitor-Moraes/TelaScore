import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { FiArrowLeft, FiSearch, FiCheck } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { listaService, filmeService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function AdicionarFilme() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const { id } = useParams();
  const navigate = useNavigate();

  const [lista, setLista] = useState(null);
  const [filmes, setFilmes] = useState([]);
  const [busca, setBusca] = useState('');
  const [adicionados, setAdicionados] = useState(new Set());
  const [erro, setErro] = useState(null);

  useEffect(() => {
    Promise.all([listaService.obter(id), filmeService.listar(), listaService.consultarItens(id)])
      .then(([l, f, itens]) => {
        setLista(l);
        setFilmes(f);
        setAdicionados(new Set(itens.map(i => i.filmeId)));
      })
      .catch(() => setErro('Erro ao carregar dados.'));
  }, [id]);

  const filmesFiltrados = filmes.filter(f =>
    f.titulo.toLowerCase().includes(busca.toLowerCase())
  );

  async function handleAdicionar(filme) {
    try {
      await listaService.adicionarFilme(id, {
        usuarioId: USUARIO_ID,
        filmeId: filme.id,
      });
      setAdicionados(prev => new Set([...prev, filme.id]));
    } catch (e) {
      setErro(`Erro ao adicionar "${filme.titulo}": ${e.message}`);
    }
  }

  if (!lista) return <div style={styles.pagina}><Navbar /><p style={styles.msg}>Carregando...</p></div>;

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>

        <div style={styles.cabecalho}>
          <button style={styles.btnVoltar} onClick={() => navigate(`/listas/${id}`)}>
            <FiArrowLeft size={20} />
          </button>
          <h2 style={styles.titulo}>Adicionar filme em "{lista.nome}"</h2>
        </div>

        {erro && <p style={styles.erro}>{erro}</p>}

        <div style={styles.buscaWrapper}>
          <FiSearch size={16} style={{ color: '#aaa' }} />
          <input
            style={styles.buscaInput}
            placeholder="Buscar filme..."
            value={busca}
            onChange={e => setBusca(e.target.value)}
          />
        </div>

        {filmesFiltrados.length === 0 && (
          <p style={styles.vazio}>Nenhum filme encontrado.</p>
        )}

        <div style={styles.lista}>
          {filmesFiltrados.map(filme => {
            const jaAdicionado = adicionados.has(filme.id);
            return (
              <div key={filme.id} style={styles.item}>
                <div style={styles.itemEsquerda}>
                  {filme.imagemUrl
                    ? <img src={filme.imagemUrl} alt={filme.titulo} style={styles.poster} />
                    : <div style={styles.posterPlaceholder}>🎬</div>
                  }
                  <div style={styles.itemInfo}>
                    <span style={styles.itemTitulo}>{filme.titulo}</span>
                    <span style={styles.itemAno}>{filme.anoLancamento}</span>
                  </div>
                </div>
                <button
                  style={{ ...styles.btnAdicionar, ...(jaAdicionado ? styles.btnAdicionado : {}) }}
                  onClick={() => !jaAdicionado && handleAdicionar(filme)}
                  disabled={jaAdicionado}
                >
                  {jaAdicionado ? <><FiCheck size={14} /> Adicionado</> : '+ Adicionar'}
                </button>
              </div>
            );
          })}
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
    maxWidth: '700px',
    margin: '0 auto',
    padding: '32px',
  },
  cabecalho: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    marginBottom: '24px',
  },
  btnVoltar: {
    background: 'none',
    border: 'none',
    color: 'white',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
  },
  titulo: {
    margin: 0,
    fontSize: '20px',
  },
  buscaWrapper: {
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    backgroundColor: '#16213e',
    borderRadius: '10px',
    padding: '10px 16px',
    marginBottom: '24px',
  },
  buscaInput: {
    flex: 1,
    background: 'none',
    border: 'none',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
  },
  lista: {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
  },
  item: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    backgroundColor: '#16213e',
    borderRadius: '10px',
    padding: '12px 18px',
  },
  itemEsquerda: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
  },
  poster: {
    width: '40px',
    height: '60px',
    objectFit: 'cover',
    borderRadius: '4px',
  },
  posterPlaceholder: {
    width: '40px',
    height: '60px',
    backgroundColor: '#0f3460',
    borderRadius: '4px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '18px',
  },
  itemInfo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
  },
  itemTitulo: {
    fontSize: '15px',
    fontWeight: 'bold',
  },
  itemAno: {
    fontSize: '12px',
    color: '#aaa',
  },
  btnAdicionar: {
    minHeight: '38px',
    backgroundColor: '#ff2f48',
    color: '#fff',
    border: '1px solid rgba(255,255,255,0.14)',
    padding: '9px 16px',
    borderRadius: '999px',
    cursor: 'pointer',
    fontSize: '13px',
    fontWeight: '800',
    whiteSpace: 'nowrap',
    boxShadow: '0 10px 20px rgba(255,47,72,0.22)',
    textShadow: '0 1px 1px rgba(0,0,0,0.28)',
  },
  btnAdicionado: {
    backgroundColor: '#176b38',
    color: '#fff',
    border: '1px solid rgba(255,255,255,0.18)',
    cursor: 'default',
    display: 'flex',
    alignItems: 'center',
    gap: '6px',
  },
  vazio: {
    color: '#aaa',
    textAlign: 'center',
    marginTop: '40px',
  },
  msg: {
    color: '#aaa',
    textAlign: 'center',
    marginTop: '60px',
  },
  erro: {
    color: '#e94560',
    marginBottom: '16px',
  },
};
