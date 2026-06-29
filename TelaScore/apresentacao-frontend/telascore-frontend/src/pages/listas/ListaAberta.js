import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { FiArrowLeft, FiEdit2, FiShare2, FiMoreVertical, FiPlusCircle } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { useAppDialog } from '../../components/AppDialog';
import { listaService, avaliacaoService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function ListaAberta() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const { id } = useParams();
  const [lista, setLista] = useState(null);
  const [itens, setItens] = useState([]);
  const [erro, setErro] = useState(null);
  const [dragSobre, setDragSobre] = useState(null);
  const [hoveredFilmeId, setHoveredFilmeId] = useState(null);
  const [notasCache, setNotasCache] = useState({});
  const [reviewAberta, setReviewAberta] = useState(null);
  const [reviewEditando, setReviewEditando] = useState(false);
  const [reviewForm, setReviewForm] = useState({ valorNota: 5, resenha: '' });
  const [menuAberto, setMenuAberto] = useState(false);
  const [modalColaborador, setModalColaborador] = useState(false);
  const [colaboradorId, setColaboradorId] = useState('');
  const { confirmar, avisar, Dialog } = useAppDialog();
  const dragId = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    Promise.all([
      listaService.obter(id, USUARIO_ID),
      listaService.consultarItens(id),
    ])
      .then(([l, i]) => { setLista(l); setItens(i); })
      .catch(() => setErro('Erro ao carregar lista.'));
  }, [id, USUARIO_ID]);

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
    const podeRemover = await confirmar({
      titulo: 'Remover filme da lista',
      mensagem: 'O filme sairá desta lista, mas continuará no catálogo.',
      textoConfirmar: 'Remover',
    });
    if (!podeRemover) return;
    try {
      await listaService.removerFilme(id, filmeId, USUARIO_ID);
      setItens(prev => prev.filter(i => i.filmeId !== filmeId));
    } catch {
      avisar({ titulo: 'Não foi possível remover', mensagem: 'Tente novamente em instantes.' });
    }
  }

  async function handleTornarColaborativa() {
    setMenuAberto(false);
    try {
      await listaService.tornarColaborativa(id, USUARIO_ID);
      setLista(prev => ({ ...prev, colaborativa: true }));
    } catch {
      avisar({ titulo: 'Não foi possível alterar a lista', mensagem: 'Tente novamente em instantes.' });
    }
  }

  async function handleAdicionarColaborador(e) {
    e.preventDefault();
    try {
      await listaService.adicionarColaborador(id, { donoId: USUARIO_ID, novoColaboradorId: parseInt(colaboradorId) });
      setLista(prev => ({ ...prev, colaboradores: [...(prev.colaboradores || []), parseInt(colaboradorId)] }));
      setModalColaborador(false);
      setColaboradorId('');
    } catch {
      avisar({ titulo: 'Colaborador não adicionado', mensagem: 'Verifique o ID informado e tente novamente.' });
    }
  }

  async function handleRemoverColaborador(colaboradorIdParaRemover) {
    const podeRemover = await confirmar({
      titulo: 'Remover colaborador',
      mensagem: `Remover o colaborador ID ${colaboradorIdParaRemover} desta lista?`,
      textoConfirmar: 'Remover',
    });
    if (!podeRemover) return;
    try {
      await listaService.removerColaborador(id, colaboradorIdParaRemover, USUARIO_ID);
      setLista(prev => ({ ...prev, colaboradores: prev.colaboradores.filter(c => c !== colaboradorIdParaRemover) }));
    } catch {
      avisar({ titulo: 'Não foi possível remover', mensagem: 'Tente novamente em instantes.' });
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

  async function handleMouseEnter(filmeId) {
    setHoveredFilmeId(filmeId);
    if (notasCache[filmeId] !== undefined) return;
    try {
      const avaliacoes = await avaliacaoService.listarPorFilme(filmeId, USUARIO_ID);
      const minha = Array.isArray(avaliacoes)
        ? avaliacoes.find(a => a.usuarioId === USUARIO_ID)
        : null;
      setNotasCache(prev => ({ ...prev, [filmeId]: minha || null }));
    } catch {
      setNotasCache(prev => ({ ...prev, [filmeId]: null }));
    }
  }

  async function carregarMinhaReview(filmeId) {
    if (notasCache[filmeId] !== undefined) return notasCache[filmeId];
    try {
      const avaliacoes = await avaliacaoService.listarPorFilme(filmeId, USUARIO_ID);
      const minha = Array.isArray(avaliacoes)
        ? avaliacoes.find(a => a.usuarioId === USUARIO_ID)
        : null;
      setNotasCache(prev => ({ ...prev, [filmeId]: minha || null }));
      return minha || null;
    } catch {
      setNotasCache(prev => ({ ...prev, [filmeId]: null }));
      return null;
    }
  }

  async function handleAbrirReview(item) {
    const review = await carregarMinhaReview(item.filmeId);
    if (!review) return;
    setReviewAberta({ ...review, filmeId: item.filmeId, titulo: item.titulo, imagemUrl: item.imagemUrl });
    setReviewForm({ valorNota: review.valorNota, resenha: review.resenha || '' });
    setReviewEditando(false);
  }

  function fecharReview() {
    setReviewAberta(null);
    setReviewEditando(false);
  }

  async function handleSalvarReview() {
    try {
      await avaliacaoService.atualizar(reviewAberta.avaliacaoId, {
        valorNota: parseInt(reviewForm.valorNota),
        resenha: reviewForm.resenha,
      });
      const atualizada = {
        ...reviewAberta,
        valorNota: parseInt(reviewForm.valorNota),
        resenha: reviewForm.resenha,
      };
      setReviewAberta(atualizada);
      setNotasCache(prev => ({ ...prev, [reviewAberta.filmeId]: atualizada }));
      setReviewEditando(false);
    } catch {
      avisar({ titulo: 'Resenha não salva', mensagem: 'Não foi possível atualizar sua resenha agora.' });
    }
  }

  async function handleRemoverReview() {
    const podeRemover = await confirmar({
      titulo: 'Remover resenha',
      mensagem: 'Sua avaliação e resenha deste filme serão apagadas.',
      textoConfirmar: 'Remover',
    });
    if (!podeRemover) return;
    try {
      await avaliacaoService.remover(reviewAberta.avaliacaoId);
      setNotasCache(prev => ({ ...prev, [reviewAberta.filmeId]: null }));
      fecharReview();
    } catch {
      avisar({ titulo: 'Resenha não removida', mensagem: 'Tente novamente em instantes.' });
    }
  }

  if (erro) return <div style={styles.pagina}><Navbar /><p style={styles.erro}>{erro}</p></div>;
  if (!lista) return <div style={styles.pagina}><Navbar /><p style={styles.carregando}>Carregando...</p></div>;

  const voltarRota = lista.tipo === 'WATCHLIST' ? '/watchlist' : '/listas';
  const listaAutomatica = lista.tipo === 'ASSISTIDOS';
  const ehDono = Number(lista.donoId) === Number(USUARIO_ID);
  const ehColaborador = (lista.colaboradores || []).some(colaboradorId =>
    Number(colaboradorId) === Number(USUARIO_ID));
  const podeModificar = !listaAutomatica && (ehDono || ehColaborador);

  return (
    <>
    {Dialog}
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
            {ehDono && !listaAutomatica && <button style={styles.btnIcone} onClick={() => navigate(`/listas/${id}/editar`)}>
              <FiEdit2 size={18} />
            </button>}
            {ehDono && !listaAutomatica && <button style={styles.btnIcone}>
              <FiShare2 size={18} />
            </button>}
            {ehDono && !listaAutomatica && <div style={{ position: 'relative' }}>
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
                        Gerenciar colaboradores
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
            </div>}
            {podeModificar && <button style={styles.btnAdicionar} onClick={() => navigate(`/listas/${id}/adicionar`)}>
              <FiPlusCircle size={16} />
              Adicionar
            </button>}
          </div>
        </div>

        {lista.rankeada && itens.length > 1 && podeModificar && (
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
                  ...(lista.rankeada && podeModificar ? { cursor: 'grab' } : {}),
                  ...(dragSobre === item.filmeId ? styles.cardDragSobre : {}),
                }}
                draggable={lista.rankeada && podeModificar}
                onDragStart={() => handleDragStart(item.filmeId)}
                onDragOver={e => lista.rankeada && podeModificar && handleDragOver(e, item.filmeId)}
                onDragLeave={handleDragLeave}
                onDrop={e => lista.rankeada && podeModificar && handleDrop(e, index)}
                onMouseEnter={() => handleMouseEnter(item.filmeId)}
                onMouseLeave={() => setHoveredFilmeId(null)}
              >
                <div style={styles.capaFilme}>
                  {item.imagemUrl
                    ? <img src={item.imagemUrl} alt="" style={styles.posterImg} draggable={false} />
                    : <span style={styles.semImagem}>🎬</span>
                  }
                  <button
                    style={{ ...styles.btnRemover, display: podeModificar ? 'flex' : 'none' }}
                    onClick={e => { e.stopPropagation(); handleRemover(item.filmeId); }}
                    title="Remover da lista"
                  >×</button>
                  {hoveredFilmeId === item.filmeId && notasCache[item.filmeId]?.valorNota != null && (
                    <button
                      type="button"
                      style={styles.overlayEstrelas}
                      onClick={e => { e.stopPropagation(); handleAbrirReview(item); }}
                      title="Abrir sua resenha"
                    >
                      {Array.from({ length: 5 }, (_, i) => (
                        <span key={i} style={{ color: i < notasCache[item.filmeId].valorNota ? '#f6c969' : 'rgba(255,255,255,0.25)', fontSize: '16px' }}>★</span>
                      ))}
                    </button>
                  )}
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
          <h3 style={styles.modalTitulo}>Colaboradores</h3>

          {lista.colaboradores && lista.colaboradores.length > 0 && (
            <div style={{ marginBottom: '16px' }}>
              {lista.colaboradores.map(cId => (
                <div key={cId} style={styles.colaboradorItem}>
                  <span style={{ fontSize: '14px', color: 'white' }}>ID: {cId}</span>
                  <button style={styles.btnRemoverColaborador} onClick={() => handleRemoverColaborador(cId)}>
                    Remover
                  </button>
                </div>
              ))}
            </div>
          )}

          <form onSubmit={handleAdicionarColaborador}>
            <input
              style={styles.modalInput}
              type="number"
              placeholder="ID do usuário para adicionar"
              value={colaboradorId}
              onChange={e => setColaboradorId(e.target.value)}
              required
            />
            <div style={styles.modalBotoes}>
              <button type="button" style={styles.btnCancelarModal} onClick={() => { setModalColaborador(false); setColaboradorId(''); }}>
                Fechar
              </button>
              <button type="submit" style={styles.btnConfirmarModal}>
                Adicionar
              </button>
            </div>
          </form>
        </div>
      </div>
    )}

    {reviewAberta && (
      <div style={styles.overlayModal}>
        <div style={styles.reviewModal}>
          <button type="button" style={styles.btnFecharReview} onClick={fecharReview}>×</button>
          <div style={styles.reviewTopo}>
            {reviewAberta.imagemUrl && <img src={reviewAberta.imagemUrl} alt="" style={styles.reviewPoster} />}
            <div>
              <span style={styles.reviewLabel}>Sua resenha</span>
              <h3 style={styles.modalTitulo}>{reviewAberta.titulo || 'Filme'}</h3>
              <p style={styles.reviewData}>{reviewAberta.dataAvaliacao}</p>
            </div>
          </div>

          {reviewEditando ? (
            <>
              <label style={styles.modalLabel}>Nota</label>
              <select
                style={styles.modalInput}
                value={reviewForm.valorNota}
                onChange={e => setReviewForm(prev => ({ ...prev, valorNota: e.target.value }))}
              >
                {[1, 2, 3, 4, 5].map(n => <option key={n} value={n}>{n} estrela(s)</option>)}
              </select>
              <label style={styles.modalLabel}>Resenha</label>
              <textarea
                style={styles.modalTextarea}
                value={reviewForm.resenha}
                onChange={e => setReviewForm(prev => ({ ...prev, resenha: e.target.value }))}
                rows={5}
                placeholder="Escreva sua resenha..."
              />
              <div style={styles.modalBotoes}>
                <button type="button" style={styles.btnCancelarModal} onClick={() => setReviewEditando(false)}>
                  Cancelar
                </button>
                <button type="button" style={styles.btnConfirmarModal} onClick={handleSalvarReview}>
                  Salvar
                </button>
              </div>
            </>
          ) : (
            <>
              <div style={styles.reviewStars}>
                {Array.from({ length: 5 }, (_, i) => (
                  <span key={i} style={{ color: i < reviewAberta.valorNota ? '#f6c969' : 'rgba(255,255,255,0.22)' }}>★</span>
                ))}
                {reviewAberta.visibilidade === 'PRIVADA' && <span style={styles.reviewBadge}>Privada</span>}
              </div>
              <p style={styles.reviewTexto}>{reviewAberta.resenha || 'Você ainda não escreveu uma resenha para essa avaliação.'}</p>
              <div style={styles.modalBotoes}>
                <button type="button" style={styles.btnCancelarModal} onClick={handleRemoverReview}>
                  Excluir
                </button>
                <button type="button" style={styles.btnConfirmarModal} onClick={() => setReviewEditando(true)}>
                  Editar
                </button>
              </div>
            </>
          )}
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
  overlayEstrelas: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    background: 'linear-gradient(transparent, rgba(0,0,0,0.75))',
    display: 'flex',
    justifyContent: 'center',
    gap: '2px',
    padding: '20px 4px 8px',
    borderRadius: '0 0 8px 8px',
    animation: 'fadeIn 0.15s ease',
    border: 'none',
    cursor: 'pointer',
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
  modalLabel: {
    display: 'block',
    margin: '12px 0 6px',
    color: '#aaa',
    fontSize: '11px',
    fontWeight: 'bold',
    letterSpacing: '1px',
    textTransform: 'uppercase',
  },
  modalTextarea: {
    width: '100%',
    padding: '10px 14px',
    borderRadius: '8px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
    boxSizing: 'border-box',
    resize: 'vertical',
    fontFamily: 'inherit',
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
  colaboradorItem: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '8px 0',
    borderBottom: '1px solid #2a2a4a',
  },
  btnRemoverColaborador: {
    padding: '4px 10px',
    borderRadius: '6px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    cursor: 'pointer',
    fontSize: '12px',
  },
  reviewModal: {
    position: 'relative',
    backgroundColor: '#16213e',
    borderRadius: '12px',
    padding: '24px',
    width: 'min(460px, calc(100vw - 32px))',
    boxSizing: 'border-box',
    boxShadow: '0 24px 70px rgba(0,0,0,0.45)',
  },
  btnFecharReview: {
    position: 'absolute',
    top: '12px',
    right: '12px',
    width: '30px',
    height: '30px',
    borderRadius: '50%',
    border: '1px solid #2a2a4a',
    backgroundColor: 'rgba(255,255,255,0.06)',
    color: 'white',
    cursor: 'pointer',
    fontSize: '20px',
    lineHeight: '1',
  },
  reviewTopo: {
    display: 'flex',
    alignItems: 'center',
    gap: '14px',
    paddingRight: '32px',
    marginBottom: '18px',
  },
  reviewPoster: {
    width: '58px',
    aspectRatio: '2/3',
    objectFit: 'cover',
    borderRadius: '8px',
    flexShrink: 0,
  },
  reviewLabel: {
    color: '#f6c969',
    fontSize: '11px',
    fontWeight: 'bold',
    letterSpacing: '1px',
    textTransform: 'uppercase',
  },
  reviewData: {
    margin: '4px 0 0',
    color: '#777',
    fontSize: '12px',
  },
  reviewStars: {
    display: 'flex',
    alignItems: 'center',
    gap: '4px',
    marginBottom: '14px',
    fontSize: '20px',
  },
  reviewBadge: {
    marginLeft: '8px',
    border: '1px solid #f6c969',
    borderRadius: '999px',
    color: '#f6c969',
    fontSize: '11px',
    padding: '3px 8px',
  },
  reviewTexto: {
    margin: '0 0 18px',
    color: '#ddd',
    fontSize: '14px',
    lineHeight: '1.6',
  },
};
