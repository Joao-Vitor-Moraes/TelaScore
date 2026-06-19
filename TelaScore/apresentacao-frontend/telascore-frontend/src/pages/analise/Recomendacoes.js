import { useCallback, useEffect, useMemo, useState } from 'react';
import Navbar from '../../components/Navbar';
import { filmeService, recomendacaoService, usuarioService } from '../../services/api';
import { FiChevronDown, FiFilm, FiHeart, FiSearch, FiSend, FiUser, FiX, FiZap } from 'react-icons/fi';
import './analise.css';

export default function Recomendacoes() {
  const [recomendacoes, setRecomendacoes] = useState([]);
  const [filmes, setFilmes] = useState([]);
  const [form, setForm] = useState({ destinatarioId: '', conteudoId: '', mensagem: '' });
  const [buscaApelido, setBuscaApelido] = useState('');
  const [usuarios, setUsuarios] = useState([]);
  const [destinatario, setDestinatario] = useState(null);
  const [buscaFilme, setBuscaFilme] = useState('');
  const [filmeSelecionado, setFilmeSelecionado] = useState(null);
  const [listaFilmesAberta, setListaFilmesAberta] = useState(false);
  const [erro, setErro] = useState('');

  const carregar = useCallback(() => {
    recomendacaoService.listar().then(setRecomendacoes).catch(e => setErro(e.message));
  }, []);

  useEffect(() => {
    carregar();
    filmeService.listar().then(setFilmes).catch(() => setFilmes([]));
  }, [carregar]);

  useEffect(() => {
    if (destinatario || buscaApelido.trim().replace(/^@/, '').length < 2) {
      setUsuarios([]);
      return undefined;
    }
    const timeout = setTimeout(() => {
      usuarioService.buscarPorApelido(buscaApelido)
        .then(setUsuarios)
        .catch(e => setErro(e.message));
    }, 250);
    return () => clearTimeout(timeout);
  }, [buscaApelido, destinatario]);

  const nomesFilmes = useMemo(
    () => Object.fromEntries(filmes.map(f => [String(f.id), f.titulo])),
    [filmes],
  );
  const filmesFiltrados = useMemo(() => {
    const termo = buscaFilme.trim().toLocaleLowerCase('pt-BR');
    if (!termo) return filmes;
    return filmes.filter(filme => filme.titulo.toLocaleLowerCase('pt-BR').includes(termo));
  }, [buscaFilme, filmes]);

  async function enviar(e) {
    e.preventDefault();
    if (!destinatario) {
      setErro('Selecione um usuário na busca por apelido.');
      return;
    }
    try {
      setErro('');
      await recomendacaoService.enviar({
        destinatarioId: Number(form.destinatarioId),
        conteudoId: String(form.conteudoId),
        tipoConteudo: 'FILME',
        mensagem: form.mensagem || null,
      });
      setForm({ destinatarioId: '', conteudoId: '', mensagem: '' });
      setBuscaApelido('');
      setDestinatario(null);
      setUsuarios([]);
      setBuscaFilme('');
      setFilmeSelecionado(null);
      setListaFilmesAberta(false);
      alert('Recomendação enviada.');
    } catch (e) {
      setErro(e.message);
    }
  }

  function selecionarDestinatario(usuario) {
    setDestinatario(usuario);
    setBuscaApelido(`@${usuario.apelido}`);
    setUsuarios([]);
    setForm(atual => ({ ...atual, destinatarioId: usuario.id }));
  }

  function limparDestinatario() {
    setDestinatario(null);
    setBuscaApelido('');
    setForm(atual => ({ ...atual, destinatarioId: '' }));
  }

  function selecionarFilme(filme) {
    setFilmeSelecionado(filme);
    setBuscaFilme(filme.titulo);
    setListaFilmesAberta(false);
    setForm(atual => ({ ...atual, conteudoId: filme.id }));
  }

  function limparFilme() {
    setFilmeSelecionado(null);
    setBuscaFilme('');
    setListaFilmesAberta(true);
    setForm(atual => ({ ...atual, conteudoId: '' }));
  }

  async function responder(id, aceitar) {
    try {
      await recomendacaoService.responder(id, aceitar);
      carregar();
    } catch (e) {
      setErro(e.message);
    }
  }

  return (
    <div style={styles.pagina} className="cinema-page">
      <Navbar />
      <main style={styles.conteudo} className="cinema-container">
        <div className="page-heading">
          <div>
            <p className="page-eyebrow">Cinema compartilhado</p>
            <h1 className="page-title">Recomendações</h1>
            <p className="page-description">Envie boas descobertas e veja o que seus amigos separaram para você.</p>
          </div>
          <FiZap className="heading-icon" />
        </div>

        <form onSubmit={enviar} style={styles.form} className="glass-panel recommendation-form">
          <h3 style={{ marginTop: 0 }}>Recomendar um filme</h3>
          <div className="recipient-picker">
            <div className={`recipient-picker__input ${destinatario ? 'is-selected' : ''}`}>
              {destinatario ? <FiUser /> : <FiSearch />}
              <input type="text" placeholder="Busque pelo apelido, ex.: @natalia"
                value={buscaApelido}
                onChange={e => {
                  setBuscaApelido(e.target.value);
                  setDestinatario(null);
                  setForm(atual => ({ ...atual, destinatarioId: '' }));
                }}
                autoComplete="off" required />
              {destinatario && (
                <button type="button" onClick={limparDestinatario} aria-label="Trocar destinatário"><FiX /></button>
              )}
            </div>
            {!!usuarios.length && (
              <div className="recipient-picker__results">
                {usuarios.map(usuario => (
                  <button type="button" key={usuario.id} onClick={() => selecionarDestinatario(usuario)}>
                    <span className="recipient-picker__avatar">
                      {usuario.avatarUrl
                        ? <img src={usuario.avatarUrl} alt="" />
                        : usuario.apelido.charAt(0).toUpperCase()}
                    </span>
                    <span><strong>@{usuario.apelido}</strong><small>{usuario.nome}</small></span>
                  </button>
                ))}
              </div>
            )}
            {!destinatario && buscaApelido.trim().length >= 2 && !usuarios.length && (
              <small className="recipient-picker__hint">Digite o apelido e selecione uma pessoa na busca.</small>
            )}
          </div>

          <div className="movie-picker">
            <div className={`movie-picker__input ${filmeSelecionado ? 'is-selected' : ''}`}>
              <FiFilm />
              <input type="text" placeholder="Digite o nome ou abra a lista de filmes"
                value={buscaFilme}
                onFocus={() => setListaFilmesAberta(true)}
                onChange={e => {
                  setBuscaFilme(e.target.value);
                  setFilmeSelecionado(null);
                  setListaFilmesAberta(true);
                  setForm(atual => ({ ...atual, conteudoId: '' }));
                }}
                autoComplete="off" required />
              {filmeSelecionado
                ? <button type="button" onClick={limparFilme} aria-label="Trocar filme"><FiX /></button>
                : <button type="button" onClick={() => setListaFilmesAberta(aberta => !aberta)}
                    aria-label="Abrir lista de filmes"><FiChevronDown /></button>}
            </div>
            {listaFilmesAberta && (
              <div className="movie-picker__results">
                {filmesFiltrados.map(filme => (
                  <button type="button" key={filme.id} onClick={() => selecionarFilme(filme)}>
                    <FiFilm />
                    <span>{filme.titulo}</span>
                    {filme.anoLancamento && <small>{filme.anoLancamento}</small>}
                  </button>
                ))}
                {!filmesFiltrados.length && <p>Nenhum filme encontrado.</p>}
              </div>
            )}
          </div>
          <textarea style={styles.input} maxLength="255" placeholder="Por que essa pessoa deveria assistir?"
            value={form.mensagem} onChange={e => setForm({ ...form, mensagem: e.target.value })} />
          <button style={styles.primario} className="btn-primary"><FiSend /> Enviar recomendação</button>
        </form>

        {erro && <p style={styles.erro}>{erro}</p>}
        <div style={styles.lista}>
          {recomendacoes.map(r => (
            <article key={r.id} style={styles.card} className="recommendation-card">
              <FiHeart className="recommendation-card__icon" />
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
