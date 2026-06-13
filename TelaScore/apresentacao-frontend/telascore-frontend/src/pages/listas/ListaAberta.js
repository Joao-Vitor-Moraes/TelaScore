import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { FiArrowLeft, FiEdit2, FiShare2, FiMoreVertical, FiPlusCircle } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { listaService } from '../../services/api';

const USUARIO_ID = 3;

export default function ListaAberta() {
  const { id } = useParams();
  const [lista, setLista] = useState(null);
  const [itens, setItens] = useState([]);
  const [erro, setErro] = useState(null);
  const [dragSobre, setDragSobre] = useState(null);
  const [menuAberto, setMenuAberto] = useState(false);
  const [modalColaborador, setModalColaborador] = useState(false);
  const [colaboradorId, setColaboradorId] = useState('');
  const dragId = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      listaService.obter(id, USUARIO_ID),
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

  async function handleRemover(filmeId) {
    if (!window.confirm('Remover este filme da lista?')) return;
    try {
      await listaService.removerFilme(id, filmeId, USUARIO_ID);
      setItens(prev => prev.filter(i => i.filmeId !== filmeId));
    } catch {
      alert('Erro ao remover filme.');
    }
  }

  async function handleTornarColaborativa() {
    setMenuAberto(false);
    try {
      await listaService.tornarColaborativa(id, USUARIO_ID);
      setLista(prev => ({ ...prev, colaborativa: true }));
    } catch {
      alert('Erro ao tornar lista colaborativa.');
    }
  }

  async function handleAdicionarColaborador(e) {
    e.preventDefault();
    try {
      await listaService.adicionarColaborador(id, { donoId: USUARIO_ID, novoColaboradorId: parseInt(colaboradorId) });
      setModalColaborador(false);
      setColaboradorId('');
      alert('Colaborador adicionado com sucesso!');
    } catch {
      alert('Erro ao adicionar colaborador. Verifique o ID informado.');
    }
  }

  async function handleDrop(e, targetIndex) {
    e.preventDefault();
    setDragSobre(null);
    const origemId = dragId.current;
    if (origemId == null || origemId === itens[targetIndex]?.filmeId) return;

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
    <>
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
            <div style={{ position: 'relative' }}>
              <button style={styles.btnIcone} onClick={() => setMenuAberto(prev => !prev)}>
                <FiMoreVertical size={18} />
              </button>
              {menuAberto && (
                <>
                  <div style={styles.overlayTransparente} onClick={() => setMenuAberto(false)} />
                  <div style={styles.dropdown}>
                    {lista.donoId === USUARIO_ID && !lista.colaborativa && (
                      <button style={styles.dropdownItem} onClick={handleTornarColaborativa}>
                        Tornar colaborativa
                      </button>
                    )}
                    {lista.donoId === USUARIO_ID && (
                      <button style={styles.dropdownItem} onClick={() => { setMenuAberto(false); setModalColaborador(true); }}>
                        Adicionar colaborador
                      </button>
                    )}
                    {lista.donoId !== USUARIO_ID && (
                      <span style={{ ...styles.dropdownItem, color: '#aaa', cursor: 'default' }}>
                        Sem ações disponíveis
                      </span>
                    )}
                  </div>
                </>
              )}
            </div>
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
                    ? <img src={item.imagemUrl} alt="" style={styles.posterImg} draggable={false} />
                    : <span style={styles.semImagem}>🎬</span>
                  }
                  <button
                    style={styles.btnRemover}
                    onClick={e => { e.stopPropagation(); handleRemover(item.filmeId); }}
                    title="Remover da lista"
                  >×</button>
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

    {modalColaborador && (
      <div style={styles.overlayModal}>
        <div style={styles.modal}>
          <h3 style={styles.modalTitulo}>Adicionar Colaborador</h3>
          <form onSubmit={handleAdicionarColaborador}>
            <input
              style={styles.modalInput}
              type="number"
              placeholder="ID do usuário"
              value={colaboradorId}
              onChange={e => setColaboradorId(e.target.value)}
              required
            />
            <div style={styles.modalBotoes}>
              <button type="button" style={styles.btnCancelarModal} onClick={() => { setModalColaborador(false); setColaboradorId(''); }}>
                Cancelar
              </button>
              <button type="submit" style={styles.btnConfirmarModal}>
                Adicionar
              </button>
            </div>
          </form>
        </div>
      </div>
    )}
    </>
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
    position: 'relative',
  },
  btnRemover: {
    position: 'absolute',
    top: '6px',
    right: '6px',
    background: 'rgba(0,0,0,0.6)',
    color: 'white',
    border: 'none',
    borderRadius: '50%',
    width: '22px',
    height: '22px',
    fontSize: '16px',
    lineHeight: '1',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 0,
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
  overlayTransparente: {
    position: 'fixed',
    inset: 0,
    zIndex: 10,
  },
  dropdown: {
    position: 'absolute',
    top: '100%',
    right: 0,
    backgroundColor: '#16213e',
    border: '1px solid #2a2a4a',
    borderRadius: '8px',
    minWidth: '180px',
    zIndex: 11,
    overflow: 'hidden',
  },
  dropdownItem: {
    display: 'block',
    width: '100%',
    padding: '10px 16px',
    background: 'none',
    border: 'none',
    color: 'white',
    fontSize: '14px',
    textAlign: 'left',
    cursor: 'pointer',
  },
  overlayModal: {
    position: 'fixed',
    inset: 0,
    backgroundColor: 'rgba(0,0,0,0.6)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 20,
  },
  modal: {
    backgroundColor: '#16213e',
    borderRadius: '12px',
    padding: '28px',
    width: '320px',
  },
  modalTitulo: {
    margin: '0 0 20px 0',
    fontSize: '16px',
  },
  modalInput: {
    width: '100%',
    padding: '10px 14px',
    borderRadius: '8px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
    boxSizing: 'border-box',
  },
  modalBotoes: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '10px',
    marginTop: '16px',
  },
  btnCancelarModal: {
    padding: '8px 18px',
    borderRadius: '8px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
  },
  btnConfirmarModal: {
    padding: '8px 18px',
    borderRadius: '8px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold',
  },
};
