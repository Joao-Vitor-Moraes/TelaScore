import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { FiCalendar, FiClock, FiTrash2, FiCheck } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { eventoService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

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
    eventoService.listarFuturos()
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
          <p style={styles.vazio}>Nenhum evento futuro agendado. Crie o primeiro!</p>
        )}

        <div style={styles.lista}>
          {eventos.map(evento => {
            const meuEvento = evento.criadorId === USUARIO_ID;
            return (
              <div key={evento.id} style={styles.card}>
                <div style={styles.dataBox}>
                  <FiClock size={20} color="#e94560" />
                </div>

                <div style={styles.cardInfo}>
                  <span style={styles.cardTitulo}>
                    {evento.titulo}
                    {meuEvento && <span style={styles.tagMeu}>Meu</span>}
                  </span>
                  {evento.descricao && (
                    <span style={styles.cardSub}>{evento.descricao}</span>
                  )}
                  <span style={styles.cardData}>{formatarDataHora(evento.dataHora)}</span>
                </div>

                {meuEvento && (
                  <button style={styles.btnCancelar} onClick={() => handleCancelar(evento.id)}>
                    <FiTrash2 size={14} /> Cancelar
                  </button>
                )}
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
    backgroundColor: '#e94560', color: 'white', border: 'none', padding: '10px 20px',
    borderRadius: '8px', cursor: 'pointer', fontSize: '14px', fontWeight: 'bold',
  },
  sucesso: {
    display: 'flex', alignItems: 'center', gap: '8px', backgroundColor: '#10b98122',
    border: '1px solid #10b981', color: '#10b981', borderRadius: '8px',
    padding: '10px 16px', marginBottom: '16px', fontSize: '13px',
  },
  lista: { display: 'flex', flexDirection: 'column', gap: '12px' },
  card: {
    backgroundColor: '#16213e', borderRadius: '12px', padding: '16px',
    display: 'flex', alignItems: 'center', gap: '16px',
  },
  dataBox: {
    width: '48px', height: '48px', borderRadius: '8px', backgroundColor: '#0f3460',
    border: '1px solid #2a2a4a', display: 'flex', alignItems: 'center', justifyContent: 'center',
    flexShrink: 0,
  },
  cardInfo: { flex: 1, display: 'flex', flexDirection: 'column', gap: '4px', minWidth: 0 },
  cardTitulo: { fontSize: '16px', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '8px' },
  tagMeu: {
    backgroundColor: '#e94560', borderRadius: '4px', padding: '1px 8px',
    fontSize: '10px', fontWeight: 'bold', letterSpacing: '0.5px',
  },
  cardSub: { fontSize: '13px', color: '#aaa' },
  cardData: { fontSize: '13px', color: '#f0a500', fontWeight: 'bold', marginTop: '2px' },
  btnCancelar: {
    display: 'flex', alignItems: 'center', gap: '6px', padding: '7px 14px', borderRadius: '6px',
    border: '1px solid #e94560', backgroundColor: 'transparent', color: '#e94560',
    cursor: 'pointer', fontSize: '13px', flexShrink: 0,
  },
  vazio: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
  msg: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
  erro: { color: '#e94560', marginBottom: '16px' },
};
