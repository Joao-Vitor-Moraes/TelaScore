import { useEffect, useMemo, useState } from 'react';
import {
  FiCalendar,
  FiExternalLink,
  FiFlag,
  FiHash,
  FiPlus,
  FiSearch,
  FiTag,
  FiTarget,
} from 'react-icons/fi';
import DenunciaFormModal from '../../components/DenunciaFormModal';
import Navbar from '../../components/Navbar';
import { useAuth } from '../../context/AuthContext';
import { denunciaService } from '../../services/api';

export default function Denuncias() {
  const { sessao } = useAuth();
  const [denuncias, setDenuncias] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);
  const [erroAcao, setErroAcao] = useState(null);
  const [busca, setBusca] = useState('');
  const [modalAberto, setModalAberto] = useState(false);

  useEffect(() => {
    carregar();
  }, []);

  async function carregar() {
    setCarregando(true);
    setErro(null);
    try {
      const dados = await denunciaService.listarMinhas();
      setDenuncias(Array.isArray(dados) ? dados : []);
    } catch {
      setDenuncias([]);
      setErro('Erro ao carregar denuncias.');
    } finally {
      setCarregando(false);
    }
  }

  const listagem = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    if (!termo) return denuncias;

    return denuncias.filter(denuncia => [
      denuncia.id,
      denuncia.alvoId,
      denuncia.tipoAlvo,
      denuncia.motivo,
      denuncia.status,
      denuncia.descricao,
      denuncia.linkOcorrencia,
      formatarData(denuncia.dataCriacao),
    ].some(valor => String(valor ?? '').toLowerCase().includes(termo)));
  }, [busca, denuncias]);

  function registrarNaLista(denuncia) {
    setErroAcao(null);
    if (denuncia) {
      setDenuncias(atuais => [denuncia, ...atuais]);
    } else {
      carregar();
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <main style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <div>
            <h2 style={styles.titulo}>DENÚNCIAS</h2>
            <span style={styles.total}>{listagem.length} de {denuncias.length} denuncias</span>
          </div>

          <div style={styles.acoesTopo}>
            <label className="template-search">
              <FiSearch size={16} />
              <input
                value={busca}
                onChange={e => setBusca(e.target.value)}
                placeholder="Buscar denúncia..."
              />
            </label>

            <button className="btn-primary" onClick={() => setModalAberto(true)}>
              <FiPlus size={15} />
              Nova denúncia
            </button>
          </div>
        </div>

        {erroAcao && <p style={styles.msgErroAcao}>{erroAcao}</p>}
        {carregando && <p style={styles.msg}>Carregando...</p>}
        {erro && !carregando && <p style={styles.msgErro}>{erro}</p>}
        {!erro && !carregando && listagem.length === 0 && (
          <p style={styles.msg}>Nenhuma denúncia encontrada.</p>
        )}

        <div style={styles.lista}>
          {listagem.map(denuncia => (
            <article key={denuncia.id} style={styles.card}>
              <div style={styles.cardTopo}>
                <div style={styles.icone}>
                  <FiFlag size={18} />
                </div>

                <div style={styles.cardInfo}>
                  <div style={styles.linhaPrincipal}>
                    <span style={styles.cardTitulo}>Denúncia #{denuncia.id}</span>
                    <span style={{ ...styles.badge, ...corStatus(denuncia.status) }}>
                      {rotuloStatus(denuncia.status)}
                    </span>
                  </div>

                  <div style={styles.detalhes}>
                    <span style={styles.detalhe}><FiTarget size={13} /> {rotuloTipo(denuncia.tipoAlvo)}</span>
                    <span style={styles.detalhe}><FiHash size={13} /> Alvo {denuncia.alvoId}</span>
                    <span style={styles.detalhe}><FiTag size={13} /> {rotuloMotivo(denuncia.motivo)}</span>
                    <span style={styles.detalhe}><FiCalendar size={13} /> {formatarData(denuncia.dataCriacao)}</span>
                  </div>

                  <p style={styles.descricao}>{denuncia.descricao}</p>
                </div>

                <div style={styles.acoesCard}>
                  {denuncia.linkOcorrencia ? (
                    <a className="btn-secondary" href={denuncia.linkOcorrencia} target="_blank" rel="noreferrer">
                      <FiExternalLink size={14} />
                      Abrir
                    </a>
                  ) : (
                    <span style={styles.semLink}>Sem link</span>
                  )}
                </div>
              </div>
            </article>
          ))}
        </div>
      </main>

      <DenunciaFormModal
        aberto={modalAberto}
        denuncianteId={sessao?.id}
        onFechar={() => setModalAberto(false)}
        onRegistrada={registrarNaLista}
      />
    </div>
  );
}

function formatarData(valor) {
  if (!valor) return '';
  return new Date(valor).toLocaleString('pt-BR');
}

function rotuloTipo(tipo) {
  const rotulos = {
    USUARIO: 'Usuario',
    MENSAGEM: 'Mensagem',
    COMUNIDADE: 'Comunidade',
    AVALIACAO: 'Avaliacao',
  };
  return rotulos[tipo] || tipo;
}

function rotuloMotivo(motivo) {
  const rotulos = {
    OFENSIVO: 'Ofensivo',
    SPAM: 'Spam',
    ASSEDIO: 'Assedio',
    CONTEUDO_INADEQUADO: 'Conteudo inadequado',
    OUTRO: 'Outro',
  };
  return rotulos[motivo] || motivo;
}

function rotuloStatus(status) {
  const rotulos = {
    PENDENTE: 'Pendente',
    EM_ANALISE: 'Em análise',
    ACEITA: 'Aceita',
    REJEITADA: 'Rejeitada',
  };
  return rotulos[status] || status;
}

function corStatus(status) {
  if (status === 'ACEITA') return styles.badgeAceita;
  if (status === 'REJEITADA') return styles.badgeRejeitada;
  if (status === 'EM_ANALISE') return styles.badgeAnalise;
  return styles.badgePendente;
}

const styles = {
  pagina: {
    minHeight: '100vh',
    backgroundColor: '#0f3460',
    color: 'white',
  },
  conteudo: {
    maxWidth: '920px',
    margin: '0 auto',
    padding: '24px 32px',
  },
  cabecalho: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: '16px',
    marginBottom: '24px',
    flexWrap: 'wrap',
  },
  titulo: {
    fontSize: '16px',
    letterSpacing: '1px',
    margin: 0,
  },
  total: {
    display: 'block',
    color: '#aaa',
    fontSize: '12px',
    marginTop: '6px',
  },
  acoesTopo: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    flexWrap: 'wrap',
  },
  buscaBox: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    backgroundColor: '#16213e',
    border: '1px solid #2a2a4a',
    borderRadius: '8px',
    padding: '8px 12px',
    minWidth: '240px',
  },
  inputBusca: {
    border: 'none',
    backgroundColor: 'transparent',
    color: 'white',
    outline: 'none',
    fontSize: '13px',
    width: '100%',
  },
  btnAtualizar: {
    width: '38px',
    height: '38px',
    borderRadius: '6px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#16213e',
    color: '#aaa',
    cursor: 'pointer',
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  btnNova: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '8px',
    padding: '10px 14px',
    borderRadius: '6px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    cursor: 'pointer',
    fontSize: '12px',
    fontWeight: 'bold',
  },
  lista: {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
  },
  card: {
    backgroundColor: '#16213e',
    borderRadius: '10px',
    padding: '16px',
    border: '1px solid #22304f',
  },
  cardTopo: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
  },
  icone: {
    width: '56px',
    height: '56px',
    borderRadius: '8px',
    backgroundColor: '#0f3460',
    border: '1px solid #2a2a4a',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: '#e94560',
    flexShrink: 0,
  },
  cardInfo: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
    minWidth: 0,
  },
  linhaPrincipal: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-start',
    gap: '12px',
    flexWrap: 'wrap',
  },
  cardTitulo: {
    fontSize: '15px',
    fontWeight: 'bold',
  },
  badge: {
    display: 'inline-flex',
    alignItems: 'center',
    padding: '4px 8px',
    borderRadius: '6px',
    fontSize: '11px',
    fontWeight: 'bold',
    letterSpacing: '0.5px',
    border: '1px solid currentColor',
  },
  badgePendente: {
    color: '#f97316',
  },
  badgeAnalise: {
    color: '#93c5fd',
  },
  badgeAceita: {
    color: '#10b981',
  },
  badgeRejeitada: {
    color: '#e94560',
  },
  detalhes: {
    display: 'flex',
    flexWrap: 'wrap',
    gap: '8px 14px',
  },
  detalhe: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '6px',
    color: '#aaa',
    fontSize: '12px',
    minWidth: 0,
  },
  descricao: {
    color: '#ddd',
    fontSize: '13px',
    margin: 0,
    lineHeight: 1.4,
  },
  acoesCard: {
    display: 'flex',
    alignItems: 'center',
    flexShrink: 0,
  },
  btnLink: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '6px',
    padding: '7px 12px',
    borderRadius: '6px',
    border: '1px solid #93c5fd',
    backgroundColor: 'transparent',
    color: '#93c5fd',
    cursor: 'pointer',
    fontSize: '12px',
    fontWeight: 'bold',
    textDecoration: 'none',
  },
  semLink: {
    color: '#777',
    fontSize: '12px',
  },
  msg: {
    color: '#aaa',
    textAlign: 'center',
    marginTop: '60px',
  },
  msgErro: {
    color: '#e94560',
    textAlign: 'center',
    marginTop: '60px',
  },
  msgErroAcao: {
    color: '#e94560',
    backgroundColor: 'rgba(233, 69, 96, 0.1)',
    border: '1px solid rgba(233, 69, 96, 0.35)',
    borderRadius: '8px',
    padding: '10px 12px',
    fontSize: '13px',
    marginBottom: '12px',
  },
};
