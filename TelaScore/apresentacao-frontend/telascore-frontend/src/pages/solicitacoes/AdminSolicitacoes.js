import { useState, useEffect } from 'react';
import Navbar from '../../components/Navbar';
import { useAppDialog } from '../../components/AppDialog';
import { solicitacaoService } from '../../services/api';
import { FiSearch, FiFilter } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext';
import NavbarAdmin from '../../components/NavbarAdmin';

export default function AdminSolicitacoes() {
  const { sessao } = useAuth();
  const ADM_ID = sessao.id;
  const [solicitacoes, setSolicitacoes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [busca, setBusca] = useState('');
  const [buscaAberta, setBuscaAberta] = useState(false);
  const [feedbackAberto, setFeedbackAberto] = useState(null);
  const [feedbackTexto, setFeedbackTexto] = useState('');
  const { confirmar, avisar, Dialog } = useAppDialog();

  async function carregar() {
    setCarregando(true);
    try {
      const dados = await solicitacaoService.listarPorStatus('PENDENTE');
      setSolicitacoes(dados);
    } catch {
      setSolicitacoes([]);
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => { carregar(); }, []);

  async function handleAceitar(id) {
    try {
      await solicitacaoService.avaliar(id, ADM_ID, true);
      carregar();
    } catch {
      avisar({ titulo: 'Solicitação não aceita', mensagem: 'Tente novamente em instantes.' });
    }
  }

  async function handleRecusar(id) {
    const podeRecusar = await confirmar({
      titulo: 'Recusar solicitação',
      mensagem: 'A solicitação será marcada como rejeitada.',
      textoConfirmar: 'Recusar',
    });
    if (!podeRecusar) return;
    try {
      await solicitacaoService.avaliar(id, ADM_ID, false);
      carregar();
    } catch {
      avisar({ titulo: 'Solicitação não recusada', mensagem: 'Tente novamente em instantes.' });
    }
  }

  async function handleFeedback(id) {
    if (!feedbackTexto.trim()) return;
    try {
      await solicitacaoService.solicitarAjustes(id, ADM_ID, feedbackTexto);
      setFeedbackAberto(null);
      setFeedbackTexto('');
      carregar();
    } catch {
      avisar({ titulo: 'Feedback não enviado', mensagem: 'Tente novamente em instantes.' });
    }
  }

  const listagem = busca.trim()
    ? solicitacoes.filter(s =>
        s.tituloSugerido.toLowerCase().includes(busca.toLowerCase())
      )
    : solicitacoes;

  return (
    <div style={styles.pagina}>
      {Dialog}
      <Navbar />
      <NavbarAdmin />
      <div style={styles.conteudo}>

        <div style={styles.cabecalho}>
          <h2 style={styles.titulo}>ADM. SOLICITAÇÕES</h2>
          <div style={styles.cabDir}>
            {buscaAberta
              ? <input
                  autoFocus
                  style={styles.inputBusca}
                  value={busca}
                  onChange={e => setBusca(e.target.value)}
                  onBlur={() => { if (!busca) setBuscaAberta(false); }}
                  placeholder="Buscar..."
                />
              : <button style={styles.btnIcone} onClick={() => setBuscaAberta(true)}>
                  <FiSearch size={18} color="#aaa" />
                </button>
            }
            <FiFilter size={18} color="#aaa" />
          </div>
        </div>

        {carregando && <p style={styles.msg}>Carregando...</p>}
        {!carregando && listagem.length === 0 && (
          <p style={styles.msg}>Nenhuma solicitação pendente.</p>
        )}

        <div style={styles.lista}>
          {listagem.map(s => (
            <div key={s.id} style={{ ...styles.card, flexDirection: feedbackAberto === s.id ? 'column' : 'row' }}>
              <div style={{ display: 'flex', gap: '16px', alignItems: 'center', width: '100%' }}>
              <div style={styles.cardEsq}>
                <span style={styles.userLabel}>USER{s.solicitanteId}</span>
                <div style={styles.placeholder}>✕</div>
              </div>

              <div style={styles.cardInfo}>
                <span style={styles.cardTitulo}>{s.tituloSugerido}</span>
                {(s.ano || s.pais) && (
                  <span style={styles.cardSub}>
                    {[s.ano, s.pais].filter(Boolean).join(' · ')}
                  </span>
                )}
                {s.justificativa && (
                  <span style={styles.cardDesc}>
                    {s.justificativa.length > 80 ? s.justificativa.slice(0, 80) + '…' : s.justificativa}
                  </span>
                )}
              </div>

              <div style={styles.acoes}>
                <button style={styles.btnAceitar} onClick={() => handleAceitar(s.id)}>
                  ACEITAR
                </button>
                <button style={styles.btnRecusar} onClick={() => handleRecusar(s.id)}>
                  RECUSAR
                </button>
                <button
                  style={styles.btnFeedback}
                  onClick={() => {
                    setFeedbackAberto(feedbackAberto === s.id ? null : s.id);
                    setFeedbackTexto('');
                  }}
                >
                  FEEDBACK
                </button>
              </div>
              </div>

              {feedbackAberto === s.id && (
                <div style={styles.feedbackForm}>
                  <textarea
                    style={styles.feedbackInput}
                    value={feedbackTexto}
                    onChange={e => setFeedbackTexto(e.target.value)}
                    placeholder="Descreva o que o usuário precisa corrigir..."
                    rows={3}
                    autoFocus
                  />
                  <div style={styles.feedbackBotoes}>
                    <button style={styles.btnCancelarFeedback} onClick={() => { setFeedbackAberto(null); setFeedbackTexto(''); }}>
                      Cancelar
                    </button>
                    <button style={styles.btnEnviarFeedback} onClick={() => handleFeedback(s.id)}>
                      Enviar
                    </button>
                  </div>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

const styles = {
  pagina: { minHeight: '100vh', backgroundColor: '#0f3460', color: 'white' },
  conteudo: { maxWidth: '720px', margin: '0 auto', padding: '24px 32px' },
  cabecalho: {
    display: 'flex', alignItems: 'center', justifyContent: 'space-between',
    marginBottom: '24px',
  },
  cabDir: { display: 'flex', gap: '14px', alignItems: 'center' },
  titulo: { fontSize: '16px', letterSpacing: '1px', margin: 0 },
  btnIcone: { background: 'none', border: 'none', cursor: 'pointer', padding: 0 },
  inputBusca: {
    padding: '5px 10px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none', width: '160px',
  },
  lista: { display: 'flex', flexDirection: 'column', gap: '12px' },
  card: {
    backgroundColor: '#16213e', borderRadius: '10px', padding: '16px',
    display: 'flex', alignItems: 'center', gap: '16px',
  },
  cardEsq: { display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '6px', flexShrink: 0 },
  userLabel: { fontSize: '11px', color: '#aaa', fontWeight: 'bold', letterSpacing: '0.5px' },
  placeholder: {
    width: '56px', height: '72px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', display: 'flex', alignItems: 'center',
    justifyContent: 'center', fontSize: '18px', color: '#444',
  },
  cardInfo: { flex: 1, display: 'flex', flexDirection: 'column', gap: '4px', minWidth: 0 },
  cardTitulo: { fontSize: '15px', fontWeight: 'bold' },
  cardSub: { fontSize: '12px', color: '#aaa' },
  cardDesc: {
    fontSize: '12px', color: '#888', overflow: 'hidden',
    textOverflow: 'ellipsis', whiteSpace: 'nowrap',
  },
  acoes: { display: 'flex', flexDirection: 'column', gap: '8px', flexShrink: 0 },
  btnAceitar: {
    padding: '7px 18px', borderRadius: '6px', border: 'none',
    backgroundColor: '#10b981', color: 'white', cursor: 'pointer',
    fontSize: '12px', fontWeight: 'bold', letterSpacing: '0.5px',
  },
  btnRecusar: {
    padding: '7px 18px', borderRadius: '6px', border: '1px solid #e94560',
    backgroundColor: 'transparent', color: '#e94560', cursor: 'pointer',
    fontSize: '12px', fontWeight: 'bold', letterSpacing: '0.5px',
  },
  btnFeedback: {
    padding: '7px 18px', borderRadius: '6px', border: '1px solid #f97316',
    backgroundColor: 'transparent', color: '#f97316', cursor: 'pointer',
    fontSize: '12px', fontWeight: 'bold', letterSpacing: '0.5px',
  },
  feedbackForm: {
    width: '100%', display: 'flex', flexDirection: 'column', gap: '8px',
    borderTop: '1px solid #2a2a4a', paddingTop: '12px',
  },
  feedbackInput: {
    padding: '10px 12px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px',
    outline: 'none', resize: 'vertical', fontFamily: 'inherit', width: '100%', boxSizing: 'border-box',
  },
  feedbackBotoes: { display: 'flex', justifyContent: 'flex-end', gap: '8px' },
  btnCancelarFeedback: {
    padding: '6px 16px', borderRadius: '6px', border: '1px solid #aaa',
    backgroundColor: 'transparent', color: '#aaa', cursor: 'pointer', fontSize: '13px',
  },
  btnEnviarFeedback: {
    padding: '6px 16px', borderRadius: '6px', border: 'none',
    backgroundColor: '#f97316', color: 'white', cursor: 'pointer', fontSize: '13px', fontWeight: 'bold',
  },
  msg: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
};
