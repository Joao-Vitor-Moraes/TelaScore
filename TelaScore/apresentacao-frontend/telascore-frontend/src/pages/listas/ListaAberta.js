import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { FiArrowLeft, FiEdit2, FiShare2, FiMoreVertical, FiPlusCircle } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { listaService } from '../../services/api';

const USUARIO_ID = 2;

export default function ListaAberta() {
  const { id } = useParams();
  const [lista, setLista] = useState(null);
  const [itens, setItens] = useState([]);
  const [erro, setErro] = useState(null);
  const [dragSobre, setDragSobre] = useState(null);
  const dragId = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      listaService.obter(id),
      listaService.consultarItens(id),
    ])
      .then(([l, i]) => { setLista(l); setItens(i); })
      .catch(() => setErro('Erro ao carregar lista.'));
  }, [id]);

  function handleDragStart(filmeId) {
    dragId.current = filmeId;
  }

  function handleDragOver(e, filmeId) {
    e.preventDefault();
    setDragSobre(filmeId);
  }

  function handleDragLeave() {
    setDragSobre(null);
  }

  async function handleDrop(e, targetIndex) {
    e.preventDefault();
    setDragSobre(null);
    const origemId = dragId.current;
    if (!origemId || origemId === itens[targetIndex]?.filmeId) return;

    const novaPosicao = targetIndex + 1;
    try {
      await listaService.reordenarFilme(id, origemId, { usuarioId: USUARIO_ID, novaPosicao });
      const atualizados = await listaService.consultarItens(id);
      setItens(atualizados);
    } catch {
      // posição inválida ou erro de rede — ignora silenciosamente
    }
  }

  if (erro) return <div style={styles.pagina}><Navbar /><p style={styles.erro}>{erro}</p></div>;
  if (!lista) return <div style={styles.pagina}><Navbar /><p style={styles.carregando}>Carregando...</p></div>;

  const voltarRota = lista.tipo === 'WATCHLIST' ? '/watchlist' : '/listas';

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>

        <div style={styles.cabecalho}>
          <div style={styles.esquerda}>
            <button style={styles.btnVoltar} onClick={() => navigate(voltarRota)}>
              <FiArrowLeft size={20} />
            </button>
            <h2 style={styles.nomeLista}>{lista.nome}</h2>
            {lista.rankeada && <span style={styles.badgeRankeada}>Rankeada</span>}
          </div>
          <div style={styles.direita}>
            <button style={styles.btnIcone} onClick={() => navigate(`/listas/${id}/editar`)}>
              <FiEdit2 size={18} />
            </button>
            <button style={styles.btnIcone}>
              <FiShare2 size={18} />
            </button>
            <button style={styles.btnIcone}>
              <FiMoreVertical size={18} />
            </button>
            <button style={styles.btnAdicionar} onClick={() => navigate(`/listas/${id}/adicionar`)}>
              <FiPlusCircle size={16} />
              Adicionar
            </button>
          </div>
        </div>

        {lista.rankeada && itens.length > 1 && (
          <p style={styles.dica}>Arraste os cards para reordenar</p>
        )}

        {itens.length === 0 ? (
          <p style={styles.vazio}>Nenhum filme nesta lista ainda.</p>
        ) : (
          <div style={styles.grid}>
            {itens.map((item, index) => (
              <div
                key={item.filmeId}
                style={{
                  ...styles.card,
                  ...(lista.rankeada ? { cursor: 'grab' } : {}),
                  ...(dragSobre === item.filmeId ? styles.cardDragSobre : {}),
                }}
                draggable={lista.rankeada}
                onDragStart={() => handleDragStart(item.filmeId)}
                onDragOver={e => lista.rankeada && handleDragOver(e, item.filmeId)}
                onDragLeave={handleDragLeave}
                onDrop={e => lista.rankeada && handleDrop(e, index)}
              >
                <div style={styles.capaFilme}>
                  {item.imagemUrl
                    ? <img src={item.imagemUrl} alt="" style={styles.posterImg} />
                    : <span style={styles.semImagem}>🎬</span>
                  }
                </div>
                {item.titulo && <span style={styles.tituloCard}>{item.titulo}</span>}
                <span style={styles.posicao}>
                  {lista.rankeada ? `#${item.posicao ?? index + 1}` : index + 1}
                </span>
              </div>
            ))}
          </div>
        )}
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
    marginBottom: '32px',
  },
  esquerda: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
  },
  direita: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  btnVoltar: {
    background: 'none',
    border: 'none',
    color: 'white',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
  },
  nomeLista: {
    margin: 0,
    fontSize: '22px',
  },
  badgeRankeada: {
    backgroundColor: '#f0a500',
    color: '#000',
    fontSize: '11px',
    fontWeight: 'bold',
    padding: '2px 8px',
    borderRadius: '4px',
  },
  btnIcone: {
    background: 'none',
    border: 'none',
    color: 'white',
    cursor: 'pointer',
    padding: '6px',
    display: 'flex',
    alignItems: 'center',
  },
  btnAdicionar: {
    display: 'flex',
    alignItems: 'center',
    gap: '6px',
    backgroundColor: '#e94560',
    color: 'white',
    border: 'none',
    padding: '8px 16px',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold',
    marginLeft: '8px',
  },
  dica: {
    color: '#aaa',
    fontSize: '13px',
    marginBottom: '16px',
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))',
    gap: '16px',
  },
  card: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '8px',
    borderRadius: '8px',
    transition: 'opacity 0.15s',
  },
  cardDragSobre: {
    opacity: 0.5,
    outline: '2px dashed #e94560',
  },
  capaFilme: {
    width: '100%',
    aspectRatio: '2/3',
    backgroundColor: '#16213e',
    borderRadius: '8px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  posterImg: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
    borderRadius: '8px',
  },
  semImagem: {
    fontSize: '32px',
  },
  tituloCard: {
    fontSize: '12px',
    textAlign: 'center',
    color: 'white',
    maxWidth: '100%',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
    whiteSpace: 'nowrap',
  },
  posicao: {
    fontSize: '13px',
    color: '#aaa',
  },
  vazio: {
    color: '#aaa',
    textAlign: 'center',
    marginTop: '60px',
  },
  carregando: {
    color: '#aaa',
    textAlign: 'center',
    marginTop: '60px',
  },
  erro: {
    color: '#e94560',
    textAlign: 'center',
    marginTop: '60px',
  },
};
