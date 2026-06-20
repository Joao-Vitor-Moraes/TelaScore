import { useEffect, useMemo, useState } from 'react';
import { FiCalendar, FiBell, FiBellOff, FiTrash2, FiCheck } from 'react-icons/fi';
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
  const [salvandoId, setSalvandoId] = useState(null);

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
  }, []);

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
      alert('Erro ao alterar o lembrete.');
    }
  }

  async function handleRemover(filmeId) {
    if (!window.confirm('Deixar de acompanhar esta estreia?')) return;
    try {
      await calendarioService.removerFilme(USUARIO_ID, filmeId);
      await carregarCalendario();
    } catch {
      alert('Erro ao remover do calendário.');
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <div style={styles.tituloWrapper}>
            <FiCalendar size={22} color="#e94560" />
            <h2 style={styles.titulo}>Calendário de Estreias</h2>
          </div>
        </div>
        <p style={styles.subtitulo}>
          Escolha os filmes que você quer acompanhar e seja avisado quando estrearem.
        </p>

        {erro && <p style={styles.erro}>{erro}</p>}
        {carregando && <p style={styles.msg}>Carregando...</p>}

        {!carregando && (
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
  cabecalho: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '12px' },
  tituloWrapper: { display: 'flex', alignItems: 'center', gap: '10px' },
  titulo: { margin: 0, fontSize: '24px' },
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
};
