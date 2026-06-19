import { useCallback, useEffect, useMemo, useState } from 'react';
import Navbar from '../../components/Navbar';
import { filmeService, recomendacaoService, usuarioService } from '../../services/api';
import {
  FiCheckCircle, FiChevronDown, FiClock, FiFilm, FiHeart,
  FiInbox, FiSearch, FiSend, FiUser, FiX, FiXCircle, FiZap,
} from 'react-icons/fi';
import './analise.css';

export default function Recomendacoes() {
  const [recomendacoes, setRecomendacoes] = useState([]);
  const [enviadas, setEnviadas] = useState([]);
  const [aba, setAba] = useState('recebidas');
  const [filmes, setFilmes] = useState([]);
  const [form, setForm] = useState({ destinatarioId: '', conteudoId: '', mensagem: '' });
  const [buscaApelido, setBuscaApelido] = useState('');
  const [usuarios, setUsuarios] = useState([]);
  const [destinatario, setDestinatario] = useState(null);
  const [buscaFilme, setBuscaFilme] = useState('');
  const [filmeSelecionado, setFilmeSelecionado] = useState(null);
  const [listaFilmesAberta, setListaFilmesAberta] = useState(false);
  const [erro, setErro] = useState('');
  const [feedback, setFeedback] = useState('');
  const [respondendoId, setRespondendoId] = useState(null);
  const [resposta, setResposta] = useState('');
  const [comentarioResposta, setComentarioResposta] = useState('');

  const carregar = useCallback(() => {
    return Promise.all([
      recomendacaoService.listar(),
      recomendacaoService.listarEnviadas(),
    ]).then(([recebidas, recomendacoesEnviadas]) => {
      setRecomendacoes(recebidas);
      setEnviadas(recomendacoesEnviadas);
    }).catch(e => setErro(e.message));
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

  useEffect(() => {
    if (!feedback) return undefined;
    const timeout = setTimeout(() => setFeedback(''), 4200);
    return () => clearTimeout(timeout);
  }, [feedback]);

  const nomesFilmes = useMemo(
    () => Object.fromEntries(filmes.map(f => [String(f.id), f.titulo])),
    [filmes],
  );
  const filmesFiltrados = useMemo(() => {
    const termo = buscaFilme.trim().toLocaleLowerCase('pt-BR');
    if (!termo) return filmes;
    return filmes.filter(filme => filme.titulo.toLocaleLowerCase('pt-BR').includes(termo));
  }, [buscaFilme, filmes]);
  const naoLidas = useMemo(
    () => recomendacoes.filter(recomendacao => recomendacao.status === 'PENDENTE').length,
    [recomendacoes],
  );

  async function enviar(e) {
    e.preventDefault();
    if (!destinatario) {
      setErro('Selecione um usuário na busca por apelido.');
      return;
    }
    try {
      setErro('');
      setFeedback('');
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
      await carregar();
      setFeedback(`Recomendação enviada para @${destinatario.apelido}.`);
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

  async function abrirResposta(id) {
    setRespondendoId(id);
    setResposta('');
    setComentarioResposta('');
    setErro('');
    const recomendacao = recomendacoes.find(item => item.id === id);
    if (recomendacao?.status === 'PENDENTE') {
      setRecomendacoes(atuais => atuais.map(item => (
        item.id === id ? { ...item, status: 'VISUALIZADA' } : item
      )));
      try {
        await recomendacaoService.visualizar(id);
      } catch (e) {
        setErro(e.message);
        carregar();
      }
    }
  }

  function cancelarResposta() {
    setRespondendoId(null);
    setResposta('');
    setComentarioResposta('');
  }

  async function responder(id) {
    if (!resposta) {
      setErro('Escolha uma resposta antes de confirmar.');
      return;
    }
    try {
      setErro('');
      await recomendacaoService.responder(id, resposta, comentarioResposta);
      await carregar();
      cancelarResposta();
      setFeedback('Sua resposta foi enviada para quem fez a recomendação.');
    } catch (e) {
      setErro(e.message);
    }
  }

  function textoStatus(status) {
    return {
      PENDENTE: 'Nova',
      VISUALIZADA: 'Visualizada',
      ACEITA: 'Aceita',
      REJEITADA: 'Rejeitada',
      VOU_ASSISTIR: 'Vou assistir',
      JA_ASSISTI: 'Já assisti',
      SEM_INTERESSE: 'Não tenho interesse',
    }[status] || status.replaceAll('_', ' ');
  }

  function iconeStatus(status) {
    if (status === 'ACEITA' || status === 'VOU_ASSISTIR' || status === 'JA_ASSISTI') return <FiCheckCircle />;
    if (status === 'REJEITADA' || status === 'SEM_INTERESSE') return <FiXCircle />;
    return <FiClock />;
  }

  return (
    <div className="cinema-page analysis-page">
      <Navbar />
      <main className="cinema-container recommendations-container">
        <div className="page-heading">
          <div>
            <p className="page-eyebrow">Cinema compartilhado</p>
            <h1 className="page-title">Recomendações</h1>
            <p className="page-description">Envie boas descobertas e veja o que seus amigos separaram para você.</p>
          </div>
          <FiZap className="heading-icon" />
        </div>

        <form onSubmit={enviar} className="recommendation-compose">
          <div className="recommendation-compose__heading">
            <span><FiSend /></span>
            <div>
              <p className="page-eyebrow">Nova indicação</p>
              <h2>Recomendar um filme</h2>
            </div>
          </div>
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
          <textarea className="recommendation-message" maxLength="255" placeholder="Por que essa pessoa deveria assistir?"
            value={form.mensagem} onChange={e => setForm({ ...form, mensagem: e.target.value })} />
          <div className="recommendation-compose__footer">
            <span>{form.mensagem.length}/255</span>
            <button className="btn-primary"><FiSend /> Enviar recomendação</button>
          </div>
        </form>

        {erro && <div className="analysis-error">{erro}</div>}
        {feedback && (
          <div className="recommendation-feedback" role="status">
            <span><FiCheckCircle /></span>
            <div>
              <strong>Recomendação enviada!</strong>
              <p>{feedback}</p>
            </div>
            <button type="button" onClick={() => setFeedback('')} aria-label="Fechar mensagem"><FiX /></button>
          </div>
        )}
        <section className="recommendations-inbox">
          <div className="recommendations-inbox__heading">
            <div>
              <p className="page-eyebrow">Cinema compartilhado</p>
              <h2>{aba === 'recebidas' ? 'Recomendações recebidas' : 'Recomendações enviadas'}</h2>
            </div>
            {aba === 'recebidas' && naoLidas > 0 && (
              <span className="recommendations-unread-summary">
                {naoLidas} {naoLidas === 1 ? 'nova' : 'novas'}
              </span>
            )}
          </div>
          <div className="recommendations-tabs" role="tablist">
            <button type="button" className={aba === 'recebidas' ? 'is-active' : ''} onClick={() => setAba('recebidas')}>
              <FiInbox /> Recebidas
              {!!recomendacoes.length && (
                <span>{recomendacoes.length}</span>
              )}
            </button>
            <button type="button" className={aba === 'enviadas' ? 'is-active' : ''} onClick={() => setAba('enviadas')}>
              <FiSend /> Enviadas
              {!!enviadas.length && (
                <span>{enviadas.length}</span>
              )}
            </button>
          </div>
          <div className="recommendations-list">
          {aba === 'recebidas' && recomendacoes.map(r => (
            <article key={r.id} className={`recommendation-card ${r.status === 'PENDENTE' ? 'recommendation-card--unread' : ''}`}>
              <FiHeart className="recommendation-card__icon" />
              <div>
                <h3>{nomesFilmes[r.conteudoId] || `${r.tipoConteudo} #${r.conteudoId}`}</h3>
                <span className="recommendation-card__sender">{r.remetenteId ? `Enviada pelo usuário #${r.remetenteId}` : 'Sugestão da plataforma'}</span>
                {r.mensagem && <p>{r.mensagem}</p>}
              </div>
              <div className="recommendation-card__side">
                <span className={`recommendation-status recommendation-status--${r.status.toLowerCase()}`}>{textoStatus(r.status)}</span>
                {(r.status === 'PENDENTE' || r.status === 'VISUALIZADA') && (
                  <div className="recommendation-card__actions">
                    <button className="recommendation-accept" onClick={() => abrirResposta(r.id)}>Responder</button>
                  </div>
                )}
              </div>
              {respondendoId === r.id && (
                <div className="recommendation-response">
                  <strong>O que você achou da indicação?</strong>
                  <div className="recommendation-response__options">
                    {[
                      ['VOU_ASSISTIR', 'Vou assistir'],
                      ['JA_ASSISTI', 'Já assisti'],
                      ['SEM_INTERESSE', 'Não tenho interesse'],
                    ].map(([valor, rotulo]) => (
                      <button type="button" key={valor}
                        className={resposta === valor ? 'is-selected' : ''}
                        onClick={() => setResposta(valor)}>
                        {rotulo}
                      </button>
                    ))}
                  </div>
                  <textarea maxLength="255" value={comentarioResposta}
                    onChange={e => setComentarioResposta(e.target.value)}
                    placeholder="Quer deixar um comentário? (opcional)" />
                  <div className="recommendation-response__footer">
                    <span>{comentarioResposta.length}/255</span>
                    <button type="button" className="recommendation-response__cancel" onClick={cancelarResposta}>Cancelar</button>
                    <button type="button" className="recommendation-response__submit" onClick={() => responder(r.id)}>Enviar resposta</button>
                  </div>
                </div>
              )}
              {r.comentarioResposta && (
                <div className="recommendation-response-comment">
                  <strong>Sua resposta</strong>
                  <p>{r.comentarioResposta}</p>
                </div>
              )}
            </article>
          ))}
          {aba === 'enviadas' && enviadas.map(r => (
            <article key={r.id} className={`recommendation-card recommendation-card--sent recommendation-card--${r.status.toLowerCase()}`}>
              <div className="recommendation-card__result-icon">{iconeStatus(r.status)}</div>
              <div>
                <h3>{nomesFilmes[r.conteudoId] || `${r.tipoConteudo} #${r.conteudoId}`}</h3>
                <span className="recommendation-card__sender">Enviada para @{r.destinatarioApelido}</span>
                {r.mensagem && <p>{r.mensagem}</p>}
              </div>
              <div className="recommendation-card__side">
                <span className={`recommendation-status recommendation-status--${r.status.toLowerCase()}`}>
                  {textoStatus(r.status)}
                </span>
                {!['PENDENTE', 'VISUALIZADA'].includes(r.status) && (
                  <small className="recommendation-result-text">
                    @{r.destinatarioApelido}: {textoStatus(r.status)}
                  </small>
                )}
              </div>
              {r.comentarioResposta && (
                <div className="recommendation-response-comment recommendation-response-comment--sent">
                  <strong>Comentário de @{r.destinatarioApelido}</strong>
                  <p>{r.comentarioResposta}</p>
                </div>
              )}
            </article>
          ))}
          {aba === 'recebidas' && !recomendacoes.length && <div className="recommendations-empty"><FiHeart /><p>Nenhuma recomendação recebida ainda.</p><span>Quando alguém separar um filme para você, ele aparecerá aqui.</span></div>}
          {aba === 'enviadas' && !enviadas.length && <div className="recommendations-empty"><FiSend /><p>Nenhuma recomendação enviada ainda.</p><span>As indicações enviadas aparecerão aqui com a resposta da pessoa.</span></div>}
          </div>
        </section>
      </main>
    </div>
  );
}
