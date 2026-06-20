import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { FiCalendar, FiClock, FiTrash2, FiCheck, FiX, FiGlobe, FiUsers, FiLock } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { eventoService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

const VIS = {
  PUBLICO: { label: 'Público', cor: '#10b981', icone: FiGlobe },
  AMIGOS: { label: 'Amigos', cor: '#f59e0b', icone: FiUsers },
  PRIVADO: { label: 'Privado', cor: '#6b7280', icone: FiLock },
};

function formatarDataHora(iso) {
  if (!iso) return '';
  const [data, hora = ''] = iso.split('T');
  const [ano, mes, dia] = data.split('-');
  const horaMin = hora.slice(0, 5);
  return `${dia}/${mes}/${ano}${horaMin ? ` · ${horaMin}` : ''}`;
}

export default function Eventos() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const navigate = useNavigate();
  const location = useLocation();

  const [eventos, setEventos] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);
  const [sucesso, setSucesso] = useState(location.state?.sucesso ?? null);

  function carregar() {
    return eventoService.listar(USUARIO_ID)
      .then(setEventos)
      .catch(() => setErro('Erro ao carregar eventos.'))
      .finally(() => setCarregando(false));
  }

  useEffect(() => {
    carregar();
    if (location.state?.sucesso) {
      window.history.replaceState({}, '');
      const t = setTimeout(() => setSucesso(null), 4000);
      return () => clearTimeout(t);
    }
  }, []);

  async function handleCancelar(id) {
    if (!window.confirm('Cancelar este evento?')) return;
    try {
      await eventoService.cancelar(id);
      setEventos(prev => prev.filter(e => e.id !== id));
    } catch {
      alert('Erro ao cancelar evento.');
    }
  }

  async function handleResponder(id, resposta) {
    try {
      await eventoService.responder(id, USUARIO_ID, resposta);
      await carregar();
    } catch {
      alert('Erro ao registrar sua resposta.');
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <div style={styles.tituloWrapper}>
            <FiCalendar size={22} color="#e94560" />
            <h2 style={styles.titulo}>Eventos</h2>
          </div>
          <button style={styles.botaoCriar} onClick={() => navigate('/eventos/novo')}>
            + Novo Evento
          </button>
        </div>

        {sucesso && (
          <div style={styles.sucesso}>
            <FiCheck size={16} /> Evento "{sucesso}" agendado com sucesso!
          </div>
        )}

        {erro && <p style={styles.erro}>{erro}</p>}
        {carregando && <p style={styles.msg}>Carregando...</p>}

        {!carregando && eventos.length === 0 && !erro && (
          <p style={styles.vazio}>Nenhum evento por aqui. Crie o primeiro!</p>
        )}

        <div style={styles.lista}>
          {eventos.map(evento => {
            const meuEvento = evento.criadorId === USUARIO_ID;
            const vis = VIS[evento.visibilidade] ?? VIS.PUBLICO;
            const VisIcone = vis.icone;
            return (
              <div key={evento.id} style={styles.card}>
                <div style={styles.topo}>
                  <div style={styles.dataBox}>
                    <FiClock size={20} color="#e94560" />
                  </div>

                  <div style={styles.cardInfo}>
                    <div style={styles.tituloLinha}>
                      <span style={styles.cardTitulo}>{evento.titulo}</span>
                      <span style={{ ...styles.badge, color: vis.cor, borderColor: vis.cor }}>
                        <VisIcone size={11} /> {vis.label}
                      </span>
                      {meuEvento && <span style={styles.tagMeu}>Meu</span>}
                    </div>
                    {evento.descricao && <span style={styles.cardSub}>{evento.descricao}</span>}
                    <span style={styles.cardData}>{formatarDataHora(evento.dataHora)}</span>
                    <span style={styles.cardMeta}>
                      por{' '}
                      <span
                        style={styles.linkNome}
                        onClick={() => navigate(`/usuario/${evento.criadorId}`)}
                        title={`Ver perfil de ${evento.criadorNome}`}
                      >
                        {evento.criadorNome}
                      </span>
                      {' '}· {evento.confirmados} confirmado(s)
                    </span>
                  </div>

                  {meuEvento && (
                    <button style={styles.btnCancelar} onClick={() => handleCancelar(evento.id)}>
                      <FiTrash2 size={14} /> Cancelar
                    </button>
                  )}
                </div>

                <div style={styles.rsvpRow}>
                  <span style={styles.rsvpPergunta}>Você vai?</span>
                  <button
                    style={{ ...styles.rsvpBtn, ...(evento.minhaResposta === 'VAI' ? styles.rsvpVai : {}) }}
                    onClick={() => handleResponder(evento.id, 'VAI')}
                  >
                    <FiCheck size={14} /> Vou
                  </button>
                  <button
                    style={{ ...styles.rsvpBtn, ...(evento.minhaResposta === 'NAO_VAI' ? styles.rsvpNaoVai : {}) }}
                    onClick={() => handleResponder(evento.id, 'NAO_VAI')}
                  >
                    <FiX size={14} /> Não vou
                  </button>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

const styles = {
  pagina: { minHeight: '100vh', backgroundColor: '#0f3460', color: 'white' },
  conteudo: { maxWidth: '720px', margin: '0 auto', padding: '32px' },
  cabecalho: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
  tituloWrapper: { display: 'flex', alignItems: 'center', gap: '10px' },
  titulo: { margin: 0, fontSize: '24px' },
  botaoCriar: {
    backgroundColor: '#c8102e', color: '#fff', border: 'none', padding: '10px 20px',
    borderRadius: '8px', cursor: 'pointer', fontSize: '14px', fontWeight: 'bold',
  },
  sucesso: {
    display: 'flex', alignItems: 'center', gap: '8px', backgroundColor: '#10b98122',
    border: '1px solid #10b981', color: '#10b981', borderRadius: '8px',
    padding: '10px 16px', marginBottom: '16px', fontSize: '13px',
  },
  lista: { display: 'flex', flexDirection: 'column', gap: '12px' },
  card: { backgroundColor: '#16213e', borderRadius: '12px', padding: '16px' },
  topo: { display: 'flex', alignItems: 'flex-start', gap: '16px' },
  dataBox: {
    width: '48px', height: '48px', borderRadius: '8px', backgroundColor: '#0f3460',
    border: '1px solid #2a2a4a', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0,
  },
  cardInfo: { flex: 1, display: 'flex', flexDirection: 'column', gap: '4px', minWidth: 0 },
  tituloLinha: { display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' },
  cardTitulo: { fontSize: '16px', fontWeight: 'bold' },
  badge: {
    display: 'inline-flex', alignItems: 'center', gap: '4px', fontSize: '10px', fontWeight: 'bold',
    border: '1px solid', borderRadius: '10px', padding: '1px 8px', letterSpacing: '0.3px',
  },
  tagMeu: {
    backgroundColor: '#c8102e', borderRadius: '4px', padding: '1px 8px',
    fontSize: '10px', fontWeight: 'bold', letterSpacing: '0.5px',
  },
  cardSub: { fontSize: '13px', color: '#aaa' },
  cardData: { fontSize: '13px', color: '#f0a500', fontWeight: 'bold', marginTop: '2px' },
  cardMeta: { fontSize: '12px', color: '#8a8aa0' },
  linkNome: { color: '#ff5963', fontWeight: 'bold', cursor: 'pointer', textDecoration: 'underline' },
  btnCancelar: {
    display: 'flex', alignItems: 'center', gap: '6px', padding: '7px 14px', borderRadius: '6px',
    border: '1px solid #ff5963', backgroundColor: 'transparent', color: '#ff5963',
    cursor: 'pointer', fontSize: '13px', flexShrink: 0,
  },
  rsvpRow: {
    display: 'flex', alignItems: 'center', gap: '10px', marginTop: '14px',
    paddingTop: '12px', borderTop: '1px solid #2a2a4a',
  },
  rsvpPergunta: { fontSize: '13px', color: '#aaa', marginRight: 'auto' },
  rsvpBtn: {
    display: 'flex', alignItems: 'center', gap: '6px', padding: '7px 16px', borderRadius: '8px',
    border: '1px solid #2a2a4a', backgroundColor: '#0f3460', color: '#ccc', cursor: 'pointer',
    fontSize: '13px', fontWeight: 'bold',
  },
  rsvpVai: { backgroundColor: '#10b981', borderColor: '#10b981', color: '#06231a' },
  rsvpNaoVai: { backgroundColor: '#c8102e', borderColor: '#c8102e', color: '#fff' },
  vazio: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
  msg: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
  erro: { color: '#ff5963', marginBottom: '16px' },
};
