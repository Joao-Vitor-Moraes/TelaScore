import { useCallback, useEffect, useState } from 'react';
import Navbar from '../../components/Navbar';
import { metaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { FiActivity, FiPlus, FiTarget } from 'react-icons/fi';
import './analise.css';

export default function Metas() {
  const { sessao } = useAuth();
  const [metas, setMetas] = useState([]);
  const [form, setForm] = useState({ titulo: '', quantidadeAlvo: 10, dataPrazo: '' });
  const [erro, setErro] = useState('');

  const carregar = useCallback(() => {
    metaService.listar(sessao.id).then(setMetas).catch(e => setErro(e.message));
  }, [sessao.id]);

  useEffect(() => { carregar(); }, [carregar]);

  async function criar(e) {
    e.preventDefault();
    setErro('');
    try {
      await metaService.criar({ usuarioId: sessao.id, ...form, quantidadeAlvo: Number(form.quantidadeAlvo) });
      setForm({ titulo: '', quantidadeAlvo: 10, dataPrazo: '' });
      carregar();
    } catch (e) { setErro(e.message); }
  }

  async function alterarProgresso(meta, delta) {
    try {
      if (delta > 0) await metaService.adicionarProgresso(meta.id, delta);
      else await metaService.removerProgresso(meta.id, Math.abs(delta));
      carregar();
    } catch (e) { setErro(e.message); }
  }

  async function estender(meta) {
    const novoPrazo = window.prompt('Novo prazo (AAAA-MM-DD):', meta.dataPrazo);
    if (!novoPrazo) return;
    try {
      await metaService.estenderPrazo(meta.id, novoPrazo);
      carregar();
    } catch (e) { setErro(e.message); }
  }

  return (
    <div style={styles.pagina} className="cinema-page">
      <Navbar />
      <main style={styles.conteudo} className="cinema-container">
        <div className="page-heading"><div><p className="page-eyebrow">Desafio pessoal</p><h1 className="page-title">Minhas metas</h1><p className="page-description">Transforme sua paixão por cinema em objetivos que você consegue acompanhar.</p></div><FiTarget className="heading-icon" /></div>
        <form onSubmit={criar} style={styles.form} className="glass-panel goal-form">
          <input style={styles.input} placeholder="Ex.: Assistir 30 filmes" value={form.titulo}
            onChange={e => setForm({ ...form, titulo: e.target.value })} required />
          <input style={styles.input} type="number" min="1" value={form.quantidadeAlvo}
            onChange={e => setForm({ ...form, quantidadeAlvo: e.target.value })} required />
          <input style={styles.input} type="date" value={form.dataPrazo}
            onChange={e => setForm({ ...form, dataPrazo: e.target.value })} required />
          <button style={styles.primario} className="btn-primary"><FiPlus /> Criar meta</button>
        </form>
        {erro && <p style={styles.erro}>{erro}</p>}
        <div style={styles.grid}>
          {metas.map(meta => {
            const percentual = Math.min(100, Math.round(meta.quantidadeAtual * 100 / meta.quantidadeAlvo));
            return (
              <article key={meta.id} style={styles.card} className="goal-card">
                <FiActivity className="goal-card__icon" />
                <div style={styles.linha}><h3 style={{ margin: 0 }}>{meta.titulo}</h3><span style={styles.status}>{meta.status}</span></div>
                <p>{meta.quantidadeAtual} de {meta.quantidadeAlvo} filmes</p>
                <div style={styles.barra}><div style={{ ...styles.progresso, width: `${percentual}%` }} /></div>
                <p style={styles.muted}>Prazo: {meta.dataPrazo}</p>
                <div style={styles.acoes}>
                  <button style={styles.secundario} onClick={() => alterarProgresso(meta, -1)}>-1</button>
                  <button style={styles.primario} onClick={() => alterarProgresso(meta, 1)}>+1</button>
                  {meta.status === 'EM_ANDAMENTO' && <button style={styles.secundario} onClick={() => estender(meta)}>Estender prazo</button>}
                </div>
              </article>
            );
          })}
        </div>
      </main>
    </div>
  );
}

const styles = {
  pagina: { minHeight: '100vh', background: '#0f3460', color: 'white' },
  conteudo: { maxWidth: 900, margin: '0 auto', padding: 32 },
  form: { display: 'grid', gridTemplateColumns: '2fr 1fr 1fr auto', gap: 10, marginBottom: 24 },
  input: { padding: 11, borderRadius: 7, border: '1px solid #334', background: '#16213e', color: 'white' },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(280px,1fr))', gap: 16 },
  card: { background: '#16213e', borderRadius: 12, padding: 20 },
  linha: { display: 'flex', justifyContent: 'space-between', gap: 10 },
  status: { color: '#f0a500', fontSize: 12 },
  barra: { height: 9, background: '#0f3460', borderRadius: 9, overflow: 'hidden' },
  progresso: { height: '100%', background: '#e94560' },
  acoes: { display: 'flex', gap: 8, flexWrap: 'wrap' },
  primario: { border: 0, borderRadius: 7, padding: '9px 14px', background: '#e94560', color: 'white', cursor: 'pointer' },
  secundario: { border: '1px solid #aaa', borderRadius: 7, padding: '8px 12px', background: 'transparent', color: 'white', cursor: 'pointer' },
  muted: { color: '#aaa', fontSize: 13 },
  erro: { color: '#ff8095' },
};
