import { useCallback, useEffect, useMemo, useState } from 'react';
import Navbar from '../../components/Navbar';
import { filmeService, recomendacaoService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function Recomendacoes() {
  const { sessao } = useAuth();
  const [recomendacoes, setRecomendacoes] = useState([]);
  const [filmes, setFilmes] = useState([]);
  const [form, setForm] = useState({ destinatarioId: '', conteudoId: '', mensagem: '' });
  const [erro, setErro] = useState('');

  const carregar = useCallback(() => {
    recomendacaoService.listar(sessao.id).then(setRecomendacoes).catch(e => setErro(e.message));
  }, [sessao.id]);

  useEffect(() => {
    carregar();
    filmeService.listar().then(setFilmes).catch(() => setFilmes([]));
  }, [carregar]);

  const nomesFilmes = useMemo(() => Object.fromEntries(filmes.map(f => [String(f.id), f.titulo])), [filmes]);

  async function enviar(e) {
    e.preventDefault();
    try {
      await recomendacaoService.enviar({
        remetenteId: sessao.id,
        destinatarioId: Number(form.destinatarioId),
        conteudoId: String(form.conteudoId),
        tipoConteudo: 'FILME',
        mensagem: form.mensagem || null,
      });
      setForm({ destinatarioId: '', conteudoId: '', mensagem: '' });
      alert('Recomendação enviada.');
    } catch (e) { setErro(e.message); }
  }

  async function responder(id, aceitar) {
    try {
      await recomendacaoService.responder(id, aceitar);
      carregar();
    } catch (e) { setErro(e.message); }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <main style={styles.conteudo}>
        <h2>Recomendações</h2>
        <form onSubmit={enviar} style={styles.form}>
          <h3 style={{ marginTop: 0 }}>Recomendar um filme</h3>
          <input style={styles.input} type="number" min="1" placeholder="ID do usuário destinatário"
            value={form.destinatarioId} onChange={e => setForm({ ...form, destinatarioId: e.target.value })} required />
          <select style={styles.input} value={form.conteudoId}
            onChange={e => setForm({ ...form, conteudoId: e.target.value })} required>
            <option value="">Escolha o filme</option>
            {filmes.map(f => <option key={f.id} value={f.id}>{f.titulo}</option>)}
          </select>
          <textarea style={styles.input} maxLength="255" placeholder="Por que essa pessoa deveria assistir?"
            value={form.mensagem} onChange={e => setForm({ ...form, mensagem: e.target.value })} />
          <button style={styles.primario}>Enviar recomendação</button>
        </form>
        {erro && <p style={styles.erro}>{erro}</p>}
        <div style={styles.lista}>
          {recomendacoes.map(r => (
            <article key={r.id} style={styles.card}>
              <div>
                <h3 style={{ margin: '0 0 6px' }}>{nomesFilmes[r.conteudoId] || `${r.tipoConteudo} #${r.conteudoId}`}</h3>
                <span style={styles.muted}>{r.remetenteId ? `Enviada pelo usuário #${r.remetenteId}` : 'Sugestão da plataforma'}</span>
                {r.mensagem && <p>{r.mensagem}</p>}
              </div>
              <div style={styles.lateral}>
                <span style={styles.status}>{r.status}</span>
                {(r.status === 'PENDENTE' || r.status === 'VISUALIZADA') && (
                  <div style={styles.acoes}>
                    <button style={styles.aceitar} onClick={() => responder(r.id, true)}>Aceitar</button>
                    <button style={styles.rejeitar} onClick={() => responder(r.id, false)}>Rejeitar</button>
                  </div>
                )}
              </div>
            </article>
          ))}
          {!recomendacoes.length && <p style={styles.muted}>Nenhuma recomendação recebida.</p>}
        </div>
      </main>
    </div>
  );
}

const styles = {
  pagina: { minHeight: '100vh', background: '#0f3460', color: 'white' },
  conteudo: { maxWidth: 850, margin: '0 auto', padding: 32 },
  form: { background: '#16213e', padding: 20, borderRadius: 12, display: 'grid', gap: 10, marginBottom: 24 },
  input: { padding: 11, borderRadius: 7, border: '1px solid #334', background: '#0f3460', color: 'white' },
  lista: { display: 'grid', gap: 12 },
  card: { display: 'flex', justifyContent: 'space-between', gap: 18, background: '#16213e', padding: 18, borderRadius: 12 },
  lateral: { display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 12 },
  status: { color: '#f0a500', fontSize: 12 },
  muted: { color: '#aaa', fontSize: 13 },
  acoes: { display: 'flex', gap: 8 },
  primario: { border: 0, borderRadius: 7, padding: 10, background: '#e94560', color: 'white', cursor: 'pointer' },
  aceitar: { border: 0, borderRadius: 6, padding: '7px 12px', background: '#10b981', color: 'white', cursor: 'pointer' },
  rejeitar: { border: '1px solid #e94560', borderRadius: 6, padding: '7px 12px', background: 'transparent', color: '#e94560', cursor: 'pointer' },
  erro: { color: '#ff8095' },
};
