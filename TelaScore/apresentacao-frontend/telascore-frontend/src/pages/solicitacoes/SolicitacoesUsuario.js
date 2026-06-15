import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { solicitacaoService } from '../../services/api';
import { FiUser, FiFilter, FiCheck, FiX, FiClock } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext';

function getOcultos(key) {
  try { return JSON.parse(localStorage.getItem(key) || '[]'); }
  catch { return []; }
}

const STATUS_CONFIG = {
  PENDENTE:           { icone: FiClock, cor: '#f59e0b', label: 'Pendente' },
  APROVADA:           { icone: FiCheck, cor: '#10b981', label: 'Aprovada' },
  REJEITADA:          { icone: FiX,     cor: '#e94560', label: 'Rejeitada' },
  CANCELADA:          { icone: FiX,     cor: '#6b7280', label: 'Cancelada' },
  AGUARDANDO_AJUSTES: { icone: FiClock, cor: '#f59e0b', label: 'Pendente' },
};

const FILTROS = ['PENDENTE', 'APROVADA', 'REJEITADA'];

export default function SolicitacoesUsuario() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const OCULTOS_KEY = `solicitacoes_ocultas_${USUARIO_ID}`;
  const [solicitacoes, setSolicitacoes] = useState([]);
  const [ocultos, setOcultos] = useState(() => getOcultos(OCULTOS_KEY));
  const [carregando, setCarregando] = useState(true);
  const [filtro, setFiltro] = useState(null);
  const [filtroAberto, setFiltroAberto] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    solicitacaoService.listarPorSolicitante(USUARIO_ID)
      .then(setSolicitacoes)
      .catch(() => {})
      .finally(() => setCarregando(false));
  }, []);

  const visiveis = solicitacoes.filter(s => !ocultos.includes(s.id));

  const listagem = filtro === null
    ? visiveis
    : visiveis.filter(s => {
        if (filtro === 'PENDENTE') return s.status === 'PENDENTE' || s.status === 'AGUARDANDO_AJUSTES';
        return s.status === filtro;
      });

  async function handleCancelar(id) {
    if (!window.confirm('Cancelar esta solicitação?')) return;
    try {
      await solicitacaoService.cancelar(id, USUARIO_ID);
      const dados = await solicitacaoService.listarPorSolicitante(USUARIO_ID);
      setSolicitacoes(dados);
    } catch {
      alert('Erro ao cancelar.');
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>

        <div style={styles.topo}>
          <div style={styles.topoEsq}>
            <FiUser size={20} color="#aaa" />
            <h2 style={styles.titulo}>SOLICITAÇÕES USUÁRIO</h2>
          </div>
          <div style={{ position: 'relative' }}>
            <button style={styles.btnIcone} onClick={() => setFiltroAberto(v => !v)}>
              <FiFilter size={18} color={filtro !== null ? '#e94560' : '#aaa'} />
            </button>
            {filtroAberto && (
              <div style={styles.dropdown}>
                {FILTROS.map(f => {
                  const cfg = STATUS_CONFIG[f];
                  return (
                    <button
                      key={f}
                      style={{
                        ...styles.dropdownItem,
                        color: filtro === f ? cfg.cor : 'white',
                        fontWeight: filtro === f ? 'bold' : 'normal',
                      }}
                      onClick={() => { setFiltro(f); setFiltroAberto(false); }}
                    >
                      {cfg.label}
                    </button>
                  );
                })}
              </div>
            )}
          </div>
        </div>

        <div style={styles.secaoRow}>
          <p style={styles.secao}>SUAS SOLICITAÇÕES</p>
          {visiveis.some(s => ['APROVADA', 'REJEITADA', 'CANCELADA'].includes(s.status)) && (
            <button
              style={styles.btnLimparHistorico}
              onClick={() => {
                const ids = visiveis
                  .filter(s => ['APROVADA', 'REJEITADA', 'CANCELADA'].includes(s.status))
                  .map(s => s.id);
                const novos = [...new Set([...ocultos, ...ids])];
                localStorage.setItem(OCULTOS_KEY, JSON.stringify(novos));
                setOcultos(novos);
              }}
            >
              Limpar histórico
            </button>
          )}
        </div>

        {carregando && <p style={styles.msg}>Carregando...</p>}

        {!carregando && listagem.length === 0 && (
          <p style={styles.msg}>Nenhuma solicitação encontrada.</p>
        )}

        <div style={styles.lista}>
          {listagem.map(s => {
            const cfg = STATUS_CONFIG[s.status] ?? { icone: FiClock, cor: '#aaa', label: s.status };
            const Icone = cfg.icone;
            return (
              <div key={s.id} style={styles.card}>
                <div style={styles.placeholder}>
                  <FiX size={24} color="#444" />
                </div>

                <div style={styles.cardInfo}>
                  <span style={styles.cardTitulo}>{s.tituloSugerido}</span>
                  {s.justificativa && (
                    <span style={styles.cardSub} title={s.justificativa}>
                      {s.justificativa.length > 60 ? s.justificativa.slice(0, 60) + '…' : s.justificativa}
                    </span>
                  )}
                  {s.feedbackAdmin && (
                    <span style={{ ...styles.cardSub, color: '#f97316' }}>
                      Admin: {s.feedbackAdmin.length > 50 ? s.feedbackAdmin.slice(0, 50) + '…' : s.feedbackAdmin}
                    </span>
                  )}
                  {s.status === 'AGUARDANDO_AJUSTES' && (
                    <button style={styles.btnEditar} onClick={() => navigate(`/solicitacoes/${s.id}/editar`)}>
                      Editar e reenviar
                    </button>
                  )}
                  {(s.status === 'PENDENTE' || s.status === 'AGUARDANDO_AJUSTES') && (
                    <button style={styles.btnCancelar} onClick={() => handleCancelar(s.id)}>
                      Cancelar
                    </button>
                  )}
                </div>

                <div style={styles.statusCol}>
                  <div style={{ ...styles.statusIcone, backgroundColor: cfg.cor + '22', border: `1px solid ${cfg.cor}` }}>
                    <Icone size={16} color={cfg.cor} />
                  </div>
                  <span style={{ ...styles.statusLabel, color: cfg.cor }}>{cfg.label}</span>
                </div>
              </div>
            );
          })}
        </div>

        <div style={styles.rodape}>
          <button style={styles.btnNova} onClick={() => navigate('/solicitacoes/nova')}>
            + Nova Solicitação
          </button>
        </div>
      </div>
    </div>
  );
}

const styles = {
  pagina: { minHeight: '100vh', backgroundColor: '#0f3460', color: 'white' },
  conteudo: { maxWidth: '640px', margin: '0 auto', padding: '32px', display: 'flex', flexDirection: 'column', minHeight: 'calc(100vh - 60px)' },
  topo: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' },
  topoEsq: { display: 'flex', alignItems: 'center', gap: '10px' },
  titulo: { fontSize: '18px', letterSpacing: '1px', margin: 0 },
  btnIcone: { background: 'none', border: 'none', cursor: 'pointer', padding: '4px' },
  dropdownDivisor: { borderTop: '1px solid #2a2a4a', margin: '4px 0' },
  secaoRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', marginTop: '8px' },
  btnLimparHistorico: {
    background: 'none', border: '1px solid #aaa', borderRadius: '4px',
    color: '#aaa', cursor: 'pointer', fontSize: '11px', padding: '3px 10px',
  },
  dropdown: {
    position: 'absolute', right: 0, top: '32px', backgroundColor: '#16213e',
    border: '1px solid #2a2a4a', borderRadius: '8px', overflow: 'hidden', zIndex: 10, minWidth: '160px',
  },
  dropdownItem: {
    display: 'block', width: '100%', padding: '10px 16px', background: 'none',
    border: 'none', cursor: 'pointer', fontSize: '13px', textAlign: 'left',
  },
  secao: { fontSize: '11px', letterSpacing: '1px', color: '#aaa', margin: 0 },
  lista: { display: 'flex', flexDirection: 'column', gap: '12px', flex: 1 },
  card: {
    backgroundColor: '#16213e', borderRadius: '10px', padding: '16px',
    display: 'flex', alignItems: 'center', gap: '16px',
  },
  placeholder: {
    width: '56px', height: '56px', borderRadius: '6px', backgroundColor: '#0f3460',
    border: '1px solid #2a2a4a', display: 'flex', alignItems: 'center', justifyContent: 'center',
    flexShrink: 0,
  },
  cardInfo: { flex: 1, display: 'flex', flexDirection: 'column', gap: '4px', minWidth: 0 },
  cardTitulo: { fontSize: '15px', fontWeight: 'bold' },
  cardSub: { fontSize: '12px', color: '#aaa', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' },
  btnEditar: {
    marginTop: '4px', alignSelf: 'flex-start', padding: '3px 10px', borderRadius: '4px',
    border: '1px solid #f97316', background: 'transparent', color: '#f97316', cursor: 'pointer', fontSize: '12px',
  },
  btnCancelar: {
    marginTop: '4px', alignSelf: 'flex-start', padding: '3px 10px', borderRadius: '4px',
    border: '1px solid #e94560', background: 'transparent', color: '#e94560', cursor: 'pointer', fontSize: '12px',
  },
  statusCol: { display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '4px', flexShrink: 0 },
  statusIcone: { width: '32px', height: '32px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center' },
  statusLabel: { fontSize: '10px', fontWeight: 'bold', letterSpacing: '0.3px' },
  rodape: { paddingTop: '32px', display: 'flex', justifyContent: 'center' },
  btnNova: {
    padding: '12px 32px', borderRadius: '8px', border: 'none',
    backgroundColor: '#e94560', color: 'white', cursor: 'pointer', fontSize: '14px', fontWeight: 'bold',
  },
  msg: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
};
