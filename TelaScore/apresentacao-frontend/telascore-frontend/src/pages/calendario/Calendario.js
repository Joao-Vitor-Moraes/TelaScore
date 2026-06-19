import { useEffect, useMemo, useState } from 'react';
import { FiCalendar, FiBell, FiBellOff, FiTrash2, FiPlus } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { calendarioService, filmeService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

function formatarData(iso) {
  if (!iso) return '';
  const [ano, mes, dia] = iso.split('-');
  return `${dia}/${mes}/${ano}`;
}

function hojeISO() {
  const d = new Date();
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
}

export default function Calendario() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;

  const [entradas, setEntradas] = useState([]);
  const [filmes, setFilmes] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  const [filmeSelecionado, setFilmeSelecionado] = useState('');
  const [dataEstreia, setDataEstreia] = useState('');
  const [adicionando, setAdicionando] = useState(false);

  const filmeMap = useMemo(
    () => Object.fromEntries(filmes.map(f => [String(f.id), f])),
    [filmes]
  );

  function carregarCalendario() {
    return calendarioService.obter(USUARIO_ID)
      .then(cal => setEntradas(cal?.entradas ?? []))
      .catch(() => setEntradas([])); // 404 = utilizador ainda não tem calendário
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
  }, []);

  async function handleAdicionar(e) {
    e.preventDefault();
    setErro(null);
    if (!filmeSelecionado || !dataEstreia) {
      setErro('Selecione um filme e a data de estreia.');
      return;
    }
    setAdicionando(true);
    try {
      await calendarioService.registrarFilme(USUARIO_ID, {
        filmeId: filmeSelecionado,
        dataEstreia,
      });
      setFilmeSelecionado('');
      setDataEstreia('');
      await carregarCalendario();
    } catch {
      setErro('Erro ao adicionar filme ao calendário.');
    } finally {
      setAdicionando(false);
    }
  }

  async function handleAlternarLembrete(filmeId) {
    try {
      await calendarioService.alternarLembrete(USUARIO_ID, filmeId);
      await carregarCalendario();
    } catch {
      alert('Erro ao alterar o lembrete.');
    }
  }

  async function handleRemover(filmeId) {
    if (!window.confirm('Remover este filme do calendário?')) return;
    try {
      await calendarioService.removerFilme(USUARIO_ID, filmeId);
      await carregarCalendario();
    } catch {
      alert('Erro ao remover o filme.');
    }
  }

  async function handleDispararLembretes() {
    try {
      await calendarioService.dispararLembretes(USUARIO_ID, hojeISO());
      alert('Lembretes do dia disparados!');
    } catch {
      alert('Erro ao disparar lembretes.');
    }
  }

  const entradasOrdenadas = [...entradas].sort(
    (a, b) => a.dataEstreiaPrevista.localeCompare(b.dataEstreiaPrevista)
  );

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <div style={styles.tituloWrapper}>
            <FiCalendar size={22} color="#e94560" />
            <h2 style={styles.titulo}>Calendário de Estreias</h2>
          </div>
          {entradas.length > 0 && (
            <button style={styles.btnLembretes} onClick={handleDispararLembretes}>
              <FiBell size={14} /> Disparar lembretes de hoje
            </button>
          )}
        </div>

        <form style={styles.formAdd} onSubmit={handleAdicionar}>
          <p style={styles.formTitulo}>ADICIONAR ESTREIA</p>
          <div style={styles.formLinha}>
            <select
              style={{ ...styles.input, flex: 2 }}
              value={filmeSelecionado}
              onChange={e => setFilmeSelecionado(e.target.value)}
            >
              <option value="">Selecione um filme</option>
              {filmes.map(f => (
                <option key={f.id} value={f.id}>
                  {f.titulo}{f.anoLancamento ? ` (${f.anoLancamento})` : ''}
                </option>
              ))}
            </select>
            <input
              style={{ ...styles.input, flex: 1 }}
              type="date"
              value={dataEstreia}
              onChange={e => setDataEstreia(e.target.value)}
            />
            <button type="submit" style={styles.btnAdd} disabled={adicionando}>
              <FiPlus size={16} /> {adicionando ? 'Adicionando...' : 'Adicionar'}
            </button>
          </div>
        </form>

        {erro && <p style={styles.erro}>{erro}</p>}

        {carregando && <p style={styles.msg}>Carregando...</p>}

        {!carregando && entradasOrdenadas.length === 0 && !erro && (
          <p style={styles.vazio}>Seu calendário está vazio. Adicione um filme para acompanhar a estreia!</p>
        )}

        <div style={styles.lista}>
          {entradasOrdenadas.map(entrada => {
            const filme = filmeMap[String(entrada.filmeId)];
            return (
              <div key={entrada.filmeId} style={styles.card}>
                <div style={styles.cardEsquerda}>
                  {filme?.imagemUrl
                    ? <img src={filme.imagemUrl} alt={filme.titulo} style={styles.poster} />
                    : <div style={styles.posterPlaceholder}>🎬</div>}
                </div>

                <div style={styles.cardInfo}>
                  <span style={styles.cardTitulo}>
                    {filme?.titulo ?? `Filme #${entrada.filmeId}`}
                  </span>
                  <span style={styles.cardData}>
                    Estreia: {formatarData(entrada.dataEstreiaPrevista)}
                  </span>
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
                    title="Remover do calendário"
                    onClick={() => handleRemover(entrada.filmeId)}
                  >
                    <FiTrash2 size={16} />
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
  cabecalho: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', gap: '12px' },
  tituloWrapper: { display: 'flex', alignItems: 'center', gap: '10px' },
  titulo: { margin: 0, fontSize: '24px' },
  btnLembretes: {
    display: 'flex', alignItems: 'center', gap: '6px', padding: '7px 14px', borderRadius: '6px',
    border: '1px solid #aaa', backgroundColor: 'transparent', color: '#aaa',
    cursor: 'pointer', fontSize: '12px', whiteSpace: 'nowrap',
  },
  formAdd: { backgroundColor: '#16213e', borderRadius: '12px', padding: '18px', marginBottom: '24px' },
  formTitulo: { fontSize: '11px', fontWeight: 'bold', letterSpacing: '1px', color: '#aaa', margin: '0 0 12px 0' },
  formLinha: { display: 'flex', gap: '10px', alignItems: 'center', flexWrap: 'wrap' },
  input: {
    padding: '9px 12px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none',
    colorScheme: 'dark', minWidth: '120px',
  },
  btnAdd: {
    display: 'flex', alignItems: 'center', gap: '6px', padding: '9px 18px', borderRadius: '6px',
    border: 'none', backgroundColor: '#e94560', color: 'white', cursor: 'pointer',
    fontSize: '13px', fontWeight: 'bold', whiteSpace: 'nowrap',
  },
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
  vazio: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
  msg: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
  erro: { color: '#e94560', marginBottom: '16px' },
};
