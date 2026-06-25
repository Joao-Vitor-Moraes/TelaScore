import { useEffect, useMemo, useState } from 'react';
import { FiCalendar, FiBell, FiBellOff, FiTrash2, FiList, FiGrid, FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { useAppDialog } from '../../components/AppDialog';
import { calendarioService, filmeService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

const NOMES_MES = ['Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'];
const DIAS_SEMANA = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
const pad2 = n => String(n).padStart(2, '0');

function formatarData(iso) {
  if (!iso) return '';
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${ano}`;
}

function hojeISO() {
  const d = new Date();
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`;
}

export default function Calendario() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;

  const [entradas, setEntradas] = useState([]);
  const [filmes, setFilmes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);
  const [salvandoId, setSalvandoId] = useState(null);
  const [view, setView] = useState('lista');
  const { confirmar, avisar, Dialog } = useAppDialog();
  const [cursor, setCursor] = useState(() => {
    const d = new Date();
    return { ano: d.getFullYear(), mes: d.getMonth() };
  });

  const filmeMap = useMemo(
    () => Object.fromEntries(filmes.map(f => [String(f.id), f])),
    [filmes]
  );

  function carregarCalendario() {
    return calendarioService.obter(USUARIO_ID)
      .then(cal => setEntradas(cal?.entradas ?? []))
      .catch(() => setEntradas([])); // 404 = utilizador ainda não acompanha nenhuma estreia
  }

  useEffect(() => {
    Promise.all([
      calendarioService.obter(USUARIO_ID).catch(() => null),
      filmeService.listar().catch(() => []),
    ])
      .then(([cal, lista]) => {
        setEntradas(cal?.entradas ?? []);
        setFilmes(lista ?? []);
      })
      .catch(() => setErro('Erro ao carregar o calendário.'))
      .finally(() => setCarregando(false));
  }, [USUARIO_ID]);

  const hoje = hojeISO();
  const idsSeguidos = useMemo(
    () => new Set(entradas.map(e => String(e.filmeId))),
    [entradas]
  );

  const proximasEstreias = useMemo(
    () => filmes
      .filter(f => f.dataEstreia && f.dataEstreia >= hoje && !idsSeguidos.has(String(f.id)))
      .sort((a, b) => a.dataEstreia.localeCompare(b.dataEstreia)),
    [filmes, idsSeguidos, hoje]
  );

  const acompanhando = useMemo(
    () => [...entradas].sort((a, b) => a.dataEstreiaPrevista.localeCompare(b.dataEstreiaPrevista)),
    [entradas]
  );

  const gradeMes = useMemo(() => {
    const { ano, mes } = cursor;
    const primeiroDiaSemana = new Date(ano, mes, 1).getDay();
    const diasNoMes = new Date(ano, mes + 1, 0).getDate();
    const celulas = [];
    for (let i = 0; i < primeiroDiaSemana; i++) celulas.push(null);
    for (let dia = 1; dia <= diasNoMes; dia++) {
      const dataStr = `${ano}-${pad2(mes + 1)}-${pad2(dia)}`;
      const estreias = filmes.filter(f => f.dataEstreia === dataStr);
      celulas.push({ dia, dataStr, estreias });
    }
    return celulas;
  }, [cursor, filmes]);

  function mudarMes(delta) {
    setCursor(c => {
      const total = c.ano * 12 + c.mes + delta;
      return { ano: Math.floor(total / 12), mes: ((total % 12) + 12) % 12 };
    });
  }

  async function handleSeguir(filme) {
    setSalvandoId(filme.id);
    setErro(null);
    try {
      await calendarioService.registrarFilme(USUARIO_ID, {
        filmeId: String(filme.id),
        dataEstreia: filme.dataEstreia,
      });
      await carregarCalendario();
    } catch {
      setErro('Erro ao seguir a estreia.');
    } finally {
      setSalvandoId(null);
    }
  }

  async function handleAlternarLembrete(filmeId) {
    try {
      await calendarioService.alternarLembrete(USUARIO_ID, filmeId);
      await carregarCalendario();
    } catch {
      avisar({ titulo: 'Lembrete não alterado', mensagem: 'Tente novamente em instantes.' });
    }
  }

  async function handleRemover(filmeId) {
    const podeRemover = await confirmar({
      titulo: 'Deixar de acompanhar',
      mensagem: 'Esta estreia sairá do seu calendário.',
      textoConfirmar: 'Remover',
    });
    if (!podeRemover) return;
    try {
      await calendarioService.removerFilme(USUARIO_ID, filmeId);
      await carregarCalendario();
    } catch {
      avisar({ titulo: 'Não foi possível remover', mensagem: 'Tente novamente em instantes.' });
    }
  }

  function toggleSeguir(filme) {
    if (idsSeguidos.has(String(filme.id))) handleRemover(String(filme.id));
    else handleSeguir(filme);
  }

  return (
    <div style={styles.pagina}>
      {Dialog}
      <Navbar />
      <div style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <div style={styles.tituloWrapper}>
            <FiCalendar size={22} color="#e94560" />
            <h2 style={styles.titulo}>Calendário de Estreias</h2>
          </div>
          <div style={styles.viewToggle}>
            <button style={{ ...styles.viewBtn, ...(view === 'lista' ? styles.viewBtnAtivo : {}) }}
              onClick={() => setView('lista')}>
              <FiList size={15} /> Lista
            </button>
            <button style={{ ...styles.viewBtn, ...(view === 'mes' ? styles.viewBtnAtivo : {}) }}
              onClick={() => setView('mes')}>
              <FiGrid size={15} /> Mês
            </button>
          </div>
        </div>
        <p style={styles.subtitulo}>
          Escolha os filmes que você quer acompanhar e seja avisado quando estrearem.
        </p>

        {erro && <p style={styles.erro}>{erro}</p>}
        {carregando && <p style={styles.msg}>Carregando...</p>}

        {!carregando && view === 'mes' && (
          <>
            <div style={styles.mesNav}>
              <button style={styles.navBtn} onClick={() => mudarMes(-1)} title="Mês anterior">
                <FiChevronLeft size={18} />
              </button>
              <span style={styles.mesTitulo}>{NOMES_MES[cursor.mes]} {cursor.ano}</span>
              <button style={styles.navBtn} onClick={() => mudarMes(1)} title="Próximo mês">
                <FiChevronRight size={18} />
              </button>
            </div>

            <div style={styles.semana}>
              {DIAS_SEMANA.map(d => <span key={d} style={styles.diaSemana}>{d}</span>)}
            </div>

            <div style={styles.grade}>
              {gradeMes.map((celula, i) => {
                if (!celula) return <div key={`v${i}`} style={styles.celulaVazia} />;
                const ehHoje = celula.dataStr === hoje;
                return (
                  <div key={celula.dataStr} style={styles.celula}>
                    <span style={ehHoje ? styles.diaHoje : styles.diaNum}>{celula.dia}</span>
                    {celula.estreias.slice(0, 3).map(f => {
                      const seguido = idsSeguidos.has(String(f.id));
                      return (
                        <span key={f.id}
                          style={{ ...styles.chip, ...(seguido ? styles.chipSeguido : {}) }}
                          title={`${f.titulo}${seguido ? ' (acompanhando — clique para remover)' : ' (clique para acompanhar)'}`}
                          onClick={() => toggleSeguir(f)}>
                          {f.titulo}
                        </span>
                      );
                    })}
                    {celula.estreias.length > 3 && (
                      <span style={styles.maisChip}>+{celula.estreias.length - 3} mais</span>
                    )}
                  </div>
                );
              })}
            </div>

            <div style={styles.legenda}>
              <span style={styles.legItem}><span style={{ ...styles.legBolinha, backgroundColor: '#c8102e' }} /> Acompanhando</span>
              <span style={styles.legItem}><span style={{ ...styles.legBolinha, backgroundColor: '#0f3460', border: '1px solid #2a2a4a' }} /> Disponível (clique para acompanhar)</span>
            </div>
          </>
        )}

        {!carregando && view === 'lista' && (
          <>
            {/* Acompanhando */}
            <p style={styles.secao}>ACOMPANHANDO ({acompanhando.length})</p>
            {acompanhando.length === 0 ? (
              <p style={styles.vazioSecao}>Você ainda não acompanha nenhuma estreia. Escolha abaixo!</p>
            ) : (
              <div style={styles.lista}>
                {acompanhando.map(entrada => {
                  const filme = filmeMap[String(entrada.filmeId)];
                  return (
                    <div key={entrada.filmeId} style={styles.card}>
                      <div style={styles.cardEsquerda}>
                        {filme?.imagemUrl
                          ? <img src={filme.imagemUrl} alt={filme.titulo} style={styles.poster} />
                          : <div style={styles.posterPlaceholder}>🎬</div>}
                      </div>
                      <div style={styles.cardInfo}>
                        <span style={styles.cardTitulo}>{filme?.titulo ?? `Filme #${entrada.filmeId}`}</span>
                        <span style={styles.cardData}>Estreia: {formatarData(entrada.dataEstreiaPrevista)}</span>
                        <span style={{ ...styles.lembreteStatus, color: entrada.lembreteAtivo ? '#10b981' : '#6b7280' }}>
                          {entrada.lembreteAtivo ? 'Lembrete ativo' : 'Lembrete desativado'}
                        </span>
                      </div>
                      <div style={styles.cardAcoes}>
                        <button
                          style={{
                            ...styles.btnIcone,
                            borderColor: entrada.lembreteAtivo ? '#10b981' : '#6b7280',
                            color: entrada.lembreteAtivo ? '#10b981' : '#6b7280',
                          }}
                          title={entrada.lembreteAtivo ? 'Desativar lembrete' : 'Ativar lembrete'}
                          onClick={() => handleAlternarLembrete(entrada.filmeId)}
                        >
                          {entrada.lembreteAtivo ? <FiBell size={16} /> : <FiBellOff size={16} />}
                        </button>
                        <button
                          style={{ ...styles.btnIcone, borderColor: '#e94560', color: '#e94560' }}
                          title="Deixar de acompanhar"
                          onClick={() => handleRemover(entrada.filmeId)}
                        >
                          <FiTrash2 size={16} />
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}

            {/* Próximas estreias */}
            <p style={{ ...styles.secao, marginTop: '32px' }}>PRÓXIMAS ESTREIAS</p>
            {proximasEstreias.length === 0 ? (
              <p style={styles.vazioSecao}>Nenhuma estreia futura no catálogo no momento.</p>
            ) : (
              <div style={styles.lista}>
                {proximasEstreias.map(filme => (
                  <div key={filme.id} style={styles.card}>
                    <div style={styles.cardEsquerda}>
                      {filme.imagemUrl
                        ? <img src={filme.imagemUrl} alt={filme.titulo} style={styles.poster} />
                        : <div style={styles.posterPlaceholder}>🎬</div>}
                    </div>
                    <div style={styles.cardInfo}>
                      <span style={styles.cardTitulo}>{filme.titulo}</span>
                      <span style={styles.cardData}>Estreia: {formatarData(filme.dataEstreia)}</span>
                    </div>
                    <button
                      style={styles.btnSeguir}
                      onClick={() => handleSeguir(filme)}
                      disabled={salvandoId === filme.id}
                    >
                      <FiBell size={14} /> {salvandoId === filme.id ? 'Salvando...' : 'Quero ser avisado'}
                    </button>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}

const styles = {
  pagina: { minHeight: '100vh', backgroundColor: '#0f3460', color: 'white' },
  conteudo: { maxWidth: '760px', margin: '0 auto', padding: '32px' },
  cabecalho: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '12px', flexWrap: 'wrap' },
  tituloWrapper: { display: 'flex', alignItems: 'center', gap: '10px' },
  titulo: { margin: 0, fontSize: '24px' },
  viewToggle: { display: 'flex', gap: '6px' },
  viewBtn: {
    display: 'flex', alignItems: 'center', gap: '6px', padding: '7px 12px', borderRadius: '8px',
    border: '1px solid #2a2a4a', backgroundColor: '#16213e', color: '#aaa', cursor: 'pointer',
    fontSize: '13px', fontWeight: 'bold',
  },
  viewBtnAtivo: { backgroundColor: '#c8102e', color: '#fff', borderColor: '#c8102e' },
  subtitulo: { color: '#aaa', fontSize: '14px', margin: '8px 0 24px 0' },
  secao: { fontSize: '11px', letterSpacing: '1px', color: '#aaa', margin: '0 0 12px 0', fontWeight: 'bold' },
  lista: { display: 'flex', flexDirection: 'column', gap: '12px' },
  card: { backgroundColor: '#16213e', borderRadius: '12px', padding: '16px', display: 'flex', alignItems: 'center', gap: '16px' },
  cardEsquerda: { flexShrink: 0 },
  poster: { width: '50px', height: '70px', objectFit: 'cover', borderRadius: '6px' },
  posterPlaceholder: {
    width: '50px', height: '70px', backgroundColor: '#0f3460', borderRadius: '6px',
    display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '20px', border: '1px solid #2a2a4a',
  },
  cardInfo: { flex: 1, display: 'flex', flexDirection: 'column', gap: '4px', minWidth: 0 },
  cardTitulo: { fontSize: '16px', fontWeight: 'bold' },
  cardData: { fontSize: '13px', color: '#f0a500', fontWeight: 'bold' },
  lembreteStatus: { fontSize: '12px' },
  cardAcoes: { display: 'flex', gap: '8px', flexShrink: 0 },
  btnIcone: {
    width: '36px', height: '36px', borderRadius: '8px', border: '1px solid',
    backgroundColor: 'transparent', cursor: 'pointer',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
  },
  btnSeguir: {
    display: 'flex', alignItems: 'center', gap: '6px', padding: '9px 16px', borderRadius: '8px',
    border: 'none', backgroundColor: '#c8102e', color: '#fff', cursor: 'pointer',
    fontSize: '13px', fontWeight: 'bold', whiteSpace: 'nowrap', flexShrink: 0,
  },
  vazioSecao: { color: '#aaa', fontSize: '14px', margin: '0 0 8px 0' },
  msg: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
  erro: { color: '#e94560', marginBottom: '16px' },

  // Visão mensal
  mesNav: { display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '16px', margin: '4px 0 16px' },
  mesTitulo: { fontSize: '18px', fontWeight: 'bold', minWidth: '170px', textAlign: 'center' },
  navBtn: {
    background: 'none', border: '1px solid #2a2a4a', color: 'white', borderRadius: '8px',
    width: '34px', height: '34px', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center',
  },
  semana: { display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '6px', marginBottom: '6px' },
  diaSemana: { textAlign: 'center', fontSize: '11px', color: '#8a8aa0', fontWeight: 'bold' },
  grade: { display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: '6px' },
  celulaVazia: { minHeight: '94px' },
  celula: {
    minHeight: '94px', backgroundColor: '#16213e', border: '1px solid #2a2a4a', borderRadius: '8px',
    padding: '6px', display: 'flex', flexDirection: 'column', gap: '3px', overflow: 'hidden',
  },
  diaNum: { fontSize: '12px', color: '#aaa', fontWeight: 'bold' },
  diaHoje: {
    fontSize: '11px', color: '#fff', backgroundColor: '#c8102e', borderRadius: '50%',
    width: '20px', height: '20px', display: 'inline-flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold',
  },
  chip: {
    fontSize: '10px', padding: '2px 5px', borderRadius: '4px', cursor: 'pointer',
    whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
    border: '1px solid #2a2a4a', backgroundColor: '#0f3460', color: '#ccc',
  },
  chipSeguido: { backgroundColor: '#c8102e', color: '#fff', borderColor: '#c8102e' },
  maisChip: { fontSize: '10px', color: '#8a8aa0' },
  legenda: { display: 'flex', gap: '18px', marginTop: '16px', fontSize: '12px', color: '#aaa', flexWrap: 'wrap' },
  legItem: { display: 'flex', alignItems: 'center', gap: '6px' },
  legBolinha: { width: '12px', height: '12px', borderRadius: '3px', display: 'inline-block' },
};
