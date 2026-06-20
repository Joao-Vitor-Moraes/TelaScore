import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import {
  comunidadeService, eventoService, filmeService, listaService, metaService,
  noticiaService, quizService, recomendacaoService, usuarioService,
} from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import {
  FiBookmark, FiCheckCircle, FiChevronDown, FiClock, FiFilm, FiHeart,
  FiCalendar, FiFileText, FiHelpCircle, FiInbox, FiList, FiPlus, FiSearch, FiSend,
  FiTarget, FiUser, FiUsers, FiX, FiXCircle, FiZap, FiStar,
} from 'react-icons/fi';
import './analise.css';

const TIPOS_CONTEUDO = {
  FILME: { rotulo: 'Filme', plural: 'filmes', icone: FiFilm, placeholder: 'Digite o nome ou abra a lista de filmes' },
  LISTA: { rotulo: 'Lista', plural: 'listas públicas', icone: FiList, placeholder: 'Busque uma lista pública' },
  COMUNIDADE: { rotulo: 'Comunidade', plural: 'comunidades', icone: FiUsers, placeholder: 'Busque uma comunidade' },
  EVENTO: { rotulo: 'Evento', plural: 'eventos', icone: FiCalendar, placeholder: 'Busque um evento futuro' },
  NOTICIA: { rotulo: 'Notícia', plural: 'notícias', icone: FiFileText, placeholder: 'Busque uma notícia' },
  META_SISTEMA: { rotulo: 'Meta do sistema', plural: 'metas do sistema', icone: FiTarget, placeholder: 'Busque uma meta do sistema' },
  QUIZ: { rotulo: 'Quiz', plural: 'quizzes', icone: FiHelpCircle, placeholder: 'Busque um quiz' },
};

function configuracaoTipo(tipo) {
  if (tipo === 'META_MODELO') return TIPOS_CONTEUDO.META_SISTEMA;
  return TIPOS_CONTEUDO[tipo];
}

export default function Recomendacoes() {
  const { sessao } = useAuth();
  const navigate = useNavigate();
  const [recomendacoes, setRecomendacoes] = useState([]);
  const [enviadas, setEnviadas] = useState([]);
  const [aba, setAba] = useState('recebidas');
  const [filmes, setFilmes] = useState([]);
  const [conteudos, setConteudos] = useState({ LISTA: [], COMUNIDADE: [], EVENTO: [], NOTICIA: [], META_SISTEMA: [], QUIZ: [] });
  const [tipoConteudo, setTipoConteudo] = useState('FILME');
  const [form, setForm] = useState({ destinatarioId: '', conteudoId: '', mensagem: '' });
  const [buscaApelido, setBuscaApelido] = useState('');
  const [usuarios, setUsuarios] = useState([]);
  const [destinatario, setDestinatario] = useState(null);
  const [buscaConteudo, setBuscaConteudo] = useState('');
  const [conteudoSelecionado, setConteudoSelecionado] = useState(null);
  const [listaConteudosAberta, setListaConteudosAberta] = useState(false);
  const [erro, setErro] = useState('');
  const [feedback, setFeedback] = useState('');
  const [respondendoId, setRespondendoId] = useState(null);
  const [resposta, setResposta] = useState('');
  const [comentarioResposta, setComentarioResposta] = useState('');
  const [listas, setListas] = useState([]);
  const [salvandoRecomendacao, setSalvandoRecomendacao] = useState(null);
  const [novaListaNome, setNovaListaNome] = useState('');
  const [buscaHistorico, setBuscaHistorico] = useState('');
  const [filtroStatus, setFiltroStatus] = useState('TODAS');
  const [avaliandoRecomendacao, setAvaliandoRecomendacao] = useState(null);
  const [avaliacaoPosterior, setAvaliacaoPosterior] = useState({ nota: 0, comentario: '', visibilidade: 'PUBLICA' });

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
    Promise.all([
      filmeService.listar(),
      listaService.listarPublicas().catch(() => []),
      comunidadeService.listarTodas().catch(() => []),
      eventoService.listarFuturos().catch(() => []),
      noticiaService.pesquisar('', '').catch(() => []),
      metaService.listarSistema().catch(() => []),
      quizService.listar().catch(() => []),
    ]).then(([dadosFilmes, listasPublicas, comunidades, eventos, noticias, metasModelo, quizzes]) => {
      setFilmes(Array.isArray(dadosFilmes) ? dadosFilmes : []);
      setConteudos({
        LISTA: Array.isArray(listasPublicas) ? listasPublicas : [],
        COMUNIDADE: Array.isArray(comunidades) ? comunidades : [],
        EVENTO: Array.isArray(eventos) ? eventos : [],
        NOTICIA: Array.isArray(noticias) ? noticias : [],
        META_SISTEMA: Array.isArray(metasModelo) ? metasModelo : [],
        QUIZ: Array.isArray(quizzes) ? quizzes : [],
      });
    }).catch(() => setFilmes([]));
    listaService.listarPorUsuario(sessao.id, sessao.id).then(setListas).catch(() => setListas([]));
  }, [carregar, sessao.id]);

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
  const filmesPorId = useMemo(
    () => Object.fromEntries(filmes.map(f => [String(f.id), f])),
    [filmes],
  );
  const todosConteudos = useMemo(() => ({ FILME: filmes, ...conteudos }), [conteudos, filmes]);
  const tituloConteudo = useCallback((tipo, item) => {
    if (!item) return '';
    if (tipo === 'LISTA' || tipo === 'COMUNIDADE') return item.nome;
    return item.titulo;
  }, []);
  const descricaoConteudo = useCallback((tipo, item) => {
    if (!item) return '';
    if (tipo === 'FILME') return item.anoLancamento || '';
    if (tipo === 'EVENTO') return item.dataHora ? new Date(item.dataHora).toLocaleString('pt-BR') : '';
    if (tipo === 'NOTICIA') return item.categoria || '';
    if (tipo === 'META_MODELO') return `${item.quantidadeAlvo} em ${item.duracaoDias} dias`;
    return item.descricao || '';
  }, []);
  const conteudosPorChave = useMemo(() => {
    const mapa = {};
    Object.entries(todosConteudos).forEach(([tipo, itens]) => {
      (itens || []).forEach(item => {
        mapa[`${tipo}:${item.id}`] = item;
      });
    });
    return mapa;
  }, [todosConteudos]);
  const conteudosFiltrados = useMemo(() => {
    const itens = todosConteudos[tipoConteudo] || [];
    const termo = buscaConteudo.trim().toLocaleLowerCase('pt-BR');
    if (!termo) return itens;
    return itens.filter(item => [
      tituloConteudo(tipoConteudo, item),
      descricaoConteudo(tipoConteudo, item),
    ].join(' ').toLocaleLowerCase('pt-BR').includes(termo));
  }, [buscaConteudo, descricaoConteudo, tipoConteudo, tituloConteudo, todosConteudos]);
  const obterConteudo = useCallback(
    recomendacao => conteudosPorChave[`${recomendacao.tipoConteudo}:${recomendacao.conteudoId}`],
    [conteudosPorChave],
  );
  const obterTitulo = useCallback(recomendacao => {
    const item = obterConteudo(recomendacao);
    return tituloConteudo(recomendacao.tipoConteudo, item)
      || `${configuracaoTipo(recomendacao.tipoConteudo)?.rotulo || recomendacao.tipoConteudo} #${recomendacao.conteudoId}`;
  }, [obterConteudo, tituloConteudo]);
  const naoLidas = useMemo(
    () => recomendacoes.filter(recomendacao => recomendacao.status === 'PENDENTE').length,
    [recomendacoes],
  );
  const recomendacoesVisiveis = useMemo(() => {
    const origem = aba === 'recebidas' ? recomendacoes : enviadas;
    const termo = buscaHistorico.trim().replace(/^@/, '').toLocaleLowerCase('pt-BR');
    return origem.filter(recomendacao => {
      const statusCorresponde = filtroStatus === 'TODAS'
        || (filtroStatus === 'PENDENTES' && ['PENDENTE', 'VISUALIZADA'].includes(recomendacao.status))
        || (filtroStatus === 'POSITIVAS' && ['ACEITA', 'VOU_ASSISTIR', 'JA_ASSISTI'].includes(recomendacao.status))
        || (filtroStatus === 'RECUSADAS' && ['REJEITADA', 'SEM_INTERESSE'].includes(recomendacao.status));
      const pessoa = aba === 'recebidas' ? recomendacao.remetenteApelido : recomendacao.destinatarioApelido;
      const texto = [
        obterTitulo(recomendacao),
        pessoa,
        recomendacao.mensagem,
        recomendacao.comentarioResposta,
      ].filter(Boolean).join(' ').toLocaleLowerCase('pt-BR');
      return statusCorresponde && (!termo || texto.includes(termo));
    });
  }, [aba, buscaHistorico, enviadas, filtroStatus, obterTitulo, recomendacoes]);

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
        tipoConteudo,
        mensagem: form.mensagem || null,
      });
      setForm({ destinatarioId: '', conteudoId: '', mensagem: '' });
      setBuscaApelido('');
      setDestinatario(null);
      setUsuarios([]);
      setBuscaConteudo('');
      setConteudoSelecionado(null);
      setListaConteudosAberta(false);
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

  function selecionarConteudo(item) {
    setConteudoSelecionado(item);
    setBuscaConteudo(tituloConteudo(tipoConteudo, item));
    setListaConteudosAberta(false);
    setForm(atual => ({ ...atual, conteudoId: item.id }));
  }

  function limparConteudo() {
    setConteudoSelecionado(null);
    setBuscaConteudo('');
    setListaConteudosAberta(true);
    setForm(atual => ({ ...atual, conteudoId: '' }));
  }

  function trocarTipoConteudo(novoTipo) {
    setTipoConteudo(novoTipo);
    setConteudoSelecionado(null);
    setBuscaConteudo('');
    setListaConteudosAberta(false);
    setForm(atual => ({ ...atual, conteudoId: '' }));
  }

  function abrirConteudo(recomendacao) {
    const rotas = {
      FILME: `/filmes/${recomendacao.conteudoId}`,
      LISTA: `/listas/${recomendacao.conteudoId}`,
      COMUNIDADE: '/comunidades',
      EVENTO: '/eventos',
      NOTICIA: '/noticias',
      META_MODELO: '/metas',
      META_SISTEMA: '/metas',
      QUIZ: '/quiz',
    };
    navigate(rotas[recomendacao.tipoConteudo] || '/recomendacoes', {
      state: { conteudoRecomendadoId: Number(recomendacao.conteudoId) },
    });
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
        window.dispatchEvent(new Event('telascore:recomendacoes-atualizadas'));
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

  function abrirSalvar(recomendacao) {
    setSalvandoRecomendacao(recomendacao);
    setNovaListaNome('');
    setErro('');
  }

  function fecharSalvar() {
    setSalvandoRecomendacao(null);
    setNovaListaNome('');
  }

  async function adicionarEmLista(listaId) {
    try {
      setErro('');
      await listaService.adicionarFilme(listaId, {
        usuarioId: sessao.id,
        filmeId: Number(salvandoRecomendacao.conteudoId),
      });
      const lista = listas.find(item => item.id === listaId);
      setFeedback(`Filme adicionado à lista “${lista?.nome || 'selecionada'}”.`);
      fecharSalvar();
    } catch (e) {
      setErro(e.message);
    }
  }

  async function criarWatchlistEAdicionar() {
    const nome = novaListaNome.trim();
    if (!nome) {
      setErro('Digite um nome para a nova Watchlist.');
      return;
    }
    try {
      setErro('');
      const criada = await listaService.criar({
        criadorId: sessao.id,
        nome,
        descricao: 'Filmes salvos a partir de recomendações.',
        tipo: 'WATCHLIST',
        visibilidade: 'PRIVADA',
        rankeada: false,
        colaborativa: false,
      });
      await listaService.adicionarFilme(criada.id, {
        usuarioId: sessao.id,
        filmeId: Number(salvandoRecomendacao.conteudoId),
      });
      const atualizadas = await listaService.listarPorUsuario(sessao.id, sessao.id);
      setListas(atualizadas);
      setFeedback(`Watchlist “${nome}” criada e filme adicionado.`);
      fecharSalvar();
    } catch (e) {
      setErro(e.message);
    }
  }

  function abrirAvaliacao(recomendacao) {
    setAvaliandoRecomendacao(recomendacao);
    setAvaliacaoPosterior({ nota: 0, comentario: '', visibilidade: 'PUBLICA' });
    setErro('');
  }

  function fecharAvaliacao() {
    setAvaliandoRecomendacao(null);
    setAvaliacaoPosterior({ nota: 0, comentario: '', visibilidade: 'PUBLICA' });
  }

  async function enviarAvaliacaoPosterior() {
    if (!avaliacaoPosterior.nota) {
      setErro('Escolha uma nota de 1 a 5 estrelas.');
      return;
    }
    try {
      setErro('');
      await recomendacaoService.avaliarPosteriormente(avaliandoRecomendacao.id, avaliacaoPosterior);
      await carregar();
      setFeedback('Avaliação publicada e enviada como retorno da recomendação.');
      fecharAvaliacao();
    } catch (e) {
      setErro(e.message);
    }
  }

  function textoStatus(status, tipo = 'FILME') {
    return {
      PENDENTE: 'Nova',
      VISUALIZADA: 'Visualizada',
      ACEITA: 'Aceita',
      REJEITADA: 'Rejeitada',
      VOU_ASSISTIR: tipo === 'FILME' ? 'Vou assistir' : 'Tenho interesse',
      JA_ASSISTI: tipo === 'FILME' ? 'Já assisti' : 'Já conheço',
      SEM_INTERESSE: 'Não tenho interesse',
    }[status] || status.replaceAll('_', ' ');
  }

  function iconeStatus(status) {
    if (status === 'ACEITA' || status === 'VOU_ASSISTIR' || status === 'JA_ASSISTI') return <FiCheckCircle />;
    if (status === 'REJEITADA' || status === 'SEM_INTERESSE') return <FiXCircle />;
    return <FiClock />;
  }

  function iconeConteudo(tipo) {
    const Icone = configuracaoTipo(tipo)?.icone || FiZap;
    return <Icone />;
  }

  function placeholderConteudo(tipo) {
    return (
      <span className={`recommendation-placeholder recommendation-placeholder--${tipo.toLowerCase()}`}>
        <span className="recommendation-placeholder__symbol">{iconeConteudo(tipo)}</span>
        <small>{configuracaoTipo(tipo)?.rotulo || 'Conteúdo'}</small>
      </span>
    );
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
              <h2>Compartilhar uma descoberta</h2>
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

          <div className="recommendation-content-types" role="tablist" aria-label="Tipo de conteúdo">
            {Object.entries(TIPOS_CONTEUDO).map(([tipo, config]) => {
              const Icone = config.icone;
              return (
                <button key={tipo} type="button" className={tipoConteudo === tipo ? 'is-selected' : ''}
                  onClick={() => trocarTipoConteudo(tipo)}>
                  <Icone /> {config.rotulo}
                </button>
              );
            })}
          </div>

          <div className="movie-picker">
            <div className={`movie-picker__input ${conteudoSelecionado ? 'is-selected' : ''}`}>
              {(() => { const Icone = TIPOS_CONTEUDO[tipoConteudo].icone; return <Icone />; })()}
              <input type="text" placeholder={TIPOS_CONTEUDO[tipoConteudo].placeholder}
                value={buscaConteudo}
                onFocus={() => setListaConteudosAberta(true)}
                onChange={e => {
                  setBuscaConteudo(e.target.value);
                  setConteudoSelecionado(null);
                  setListaConteudosAberta(true);
                  setForm(atual => ({ ...atual, conteudoId: '' }));
                }}
                autoComplete="off" required />
              {conteudoSelecionado
                ? <button type="button" onClick={limparConteudo} aria-label="Trocar conteúdo"><FiX /></button>
                : <button type="button" onClick={() => setListaConteudosAberta(aberta => !aberta)}
                    aria-label="Abrir lista de conteúdos"><FiChevronDown /></button>}
            </div>
            {listaConteudosAberta && (
              <div className="movie-picker__results">
                {conteudosFiltrados.map(item => {
                  const Icone = TIPOS_CONTEUDO[tipoConteudo].icone;
                  return (
                    <button type="button" key={item.id} onClick={() => selecionarConteudo(item)}>
                      <Icone />
                      <span>{tituloConteudo(tipoConteudo, item)}</span>
                      {descricaoConteudo(tipoConteudo, item) && <small>{descricaoConteudo(tipoConteudo, item)}</small>}
                    </button>
                  );
                })}
                {!conteudosFiltrados.length && <p>Nenhum conteúdo encontrado.</p>}
              </div>
            )}
          </div>
          <textarea className="recommendation-message" maxLength="255" placeholder="Por que você está recomendando isso?"
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
          <div className="recommendations-filters">
            <label>
              <FiSearch />
              <input value={buscaHistorico} onChange={e => setBuscaHistorico(e.target.value)}
                placeholder={aba === 'recebidas' ? 'Buscar por conteúdo ou remetente' : 'Buscar por conteúdo ou destinatário'} />
              {buscaHistorico && <button type="button" onClick={() => setBuscaHistorico('')} aria-label="Limpar busca"><FiX /></button>}
            </label>
            <select value={filtroStatus} onChange={e => setFiltroStatus(e.target.value)}>
              <option value="TODAS">Todas as situações</option>
              <option value="PENDENTES">Aguardando resposta</option>
              <option value="POSITIVAS">Respostas positivas</option>
              <option value="RECUSADAS">Sem interesse</option>
            </select>
            {(buscaHistorico || filtroStatus !== 'TODAS') && (
              <span>{recomendacoesVisiveis.length} resultado{recomendacoesVisiveis.length === 1 ? '' : 's'}</span>
            )}
          </div>
          <div className="recommendations-list">
          {aba === 'recebidas' && recomendacoesVisiveis.map(r => (
            <article key={r.id} className={`recommendation-card ${r.status === 'PENDENTE' ? 'recommendation-card--unread' : ''}`}>
              <div className="recommendation-card__poster">
                {r.tipoConteudo === 'FILME' && filmesPorId[String(r.conteudoId)]?.imagemUrl
                  ? <img src={filmesPorId[String(r.conteudoId)].imagemUrl} alt="" loading="lazy" />
                  : placeholderConteudo(r.tipoConteudo)}
                <i><FiHeart /></i>
              </div>
              <div>
                <span className="recommendation-content-badge">{configuracaoTipo(r.tipoConteudo)?.rotulo || r.tipoConteudo}</span>
                <h3>{obterTitulo(r)}</h3>
                <span className="recommendation-card__sender">{r.remetenteApelido ? `Enviada por @${r.remetenteApelido}` : 'Sugestão da plataforma'}</span>
                {r.mensagem && <p>{r.mensagem}</p>}
              </div>
              <div className="recommendation-card__side">
                <span className={`recommendation-status recommendation-status--${r.status.toLowerCase()}`}>{textoStatus(r.status, r.tipoConteudo)}</span>
                {(r.status === 'PENDENTE' || r.status === 'VISUALIZADA') && (
                  <div className="recommendation-card__actions">
                    <button className="recommendation-accept" onClick={() => abrirResposta(r.id)}>Responder</button>
                  </div>
                )}
                {!['PENDENTE', 'VISUALIZADA', 'SEM_INTERESSE', 'REJEITADA'].includes(r.status) && r.tipoConteudo === 'FILME' && (
                  <button type="button" className="recommendation-save" onClick={() => abrirSalvar(r)}>
                    <FiBookmark /> Salvar em lista
                  </button>
                )}
                {r.status === 'JA_ASSISTI' && !r.notaPosterior && r.tipoConteudo === 'FILME' && (
                  <button type="button" className="recommendation-review" onClick={() => abrirAvaliacao(r)}>
                    <FiStar /> Avaliar filme
                  </button>
                )}
                <button type="button" className="recommendation-open" onClick={() => abrirConteudo(r)}>
                  Abrir {configuracaoTipo(r.tipoConteudo)?.rotulo.toLowerCase() || 'conteúdo'}
                </button>
              </div>
              {respondendoId === r.id && (
                <div className="recommendation-response">
                  <strong>O que você achou da indicação?</strong>
                  <div className="recommendation-response__options">
                    {[
                      ['VOU_ASSISTIR', r.tipoConteudo === 'FILME' ? 'Vou assistir' : 'Tenho interesse'],
                      ['JA_ASSISTI', r.tipoConteudo === 'FILME' ? 'Já assisti' : 'Já conheço'],
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
              {r.notaPosterior && (
                <div className="recommendation-later-review">
                  <strong>Sua avaliação posterior</strong>
                  <div className="recommendation-later-review__stars">
                    {[1, 2, 3, 4, 5].map(estrela => <FiStar key={estrela} className={estrela <= r.notaPosterior ? 'is-filled' : ''} />)}
                    <span>{r.notaPosterior}/5</span>
                  </div>
                  {r.avaliacaoPosterior && <p>{r.avaliacaoPosterior}</p>}
                </div>
              )}
            </article>
          ))}
          {aba === 'enviadas' && recomendacoesVisiveis.map(r => (
            <article key={r.id} className={`recommendation-card recommendation-card--sent recommendation-card--${r.status.toLowerCase()}`}>
              <div className="recommendation-card__poster">
                {r.tipoConteudo === 'FILME' && filmesPorId[String(r.conteudoId)]?.imagemUrl
                  ? <img src={filmesPorId[String(r.conteudoId)].imagemUrl} alt="" loading="lazy" />
                  : placeholderConteudo(r.tipoConteudo)}
                <i className={`recommendation-card__poster-status recommendation-card__poster-status--${r.status.toLowerCase()}`}>
                  {iconeStatus(r.status)}
                </i>
              </div>
              <div>
                <span className="recommendation-content-badge">{configuracaoTipo(r.tipoConteudo)?.rotulo || r.tipoConteudo}</span>
                <h3>{obterTitulo(r)}</h3>
                <span className="recommendation-card__sender">Enviada para @{r.destinatarioApelido}</span>
                {r.mensagem && <p>{r.mensagem}</p>}
              </div>
              <div className="recommendation-card__side">
                <span className={`recommendation-status recommendation-status--${r.status.toLowerCase()}`}>
                  {textoStatus(r.status, r.tipoConteudo)}
                </span>
                {!['PENDENTE', 'VISUALIZADA'].includes(r.status) && (
                  <small className="recommendation-result-text">
                    @{r.destinatarioApelido}: {textoStatus(r.status, r.tipoConteudo)}
                  </small>
                )}
                <button type="button" className="recommendation-open" onClick={() => abrirConteudo(r)}>
                  Abrir {configuracaoTipo(r.tipoConteudo)?.rotulo.toLowerCase() || 'conteúdo'}
                </button>
              </div>
              {r.comentarioResposta && (
                <div className="recommendation-response-comment recommendation-response-comment--sent">
                  <strong>Comentário de @{r.destinatarioApelido}</strong>
                  <p>{r.comentarioResposta}</p>
                </div>
              )}
              {r.notaPosterior && (
                <div className="recommendation-later-review">
                  <strong>Avaliação de @{r.destinatarioApelido}</strong>
                  <div className="recommendation-later-review__stars">
                    {[1, 2, 3, 4, 5].map(estrela => <FiStar key={estrela} className={estrela <= r.notaPosterior ? 'is-filled' : ''} />)}
                    <span>{r.notaPosterior}/5</span>
                  </div>
                  {r.avaliacaoPosterior && <p>{r.avaliacaoPosterior}</p>}
                </div>
              )}
            </article>
          ))}
          {aba === 'recebidas' && !recomendacoes.length && <div className="recommendations-empty"><FiHeart /><p>Nenhuma recomendação recebida ainda.</p><span>Quando alguém compartilhar uma descoberta com você, ela aparecerá aqui.</span></div>}
          {aba === 'enviadas' && !enviadas.length && <div className="recommendations-empty"><FiSend /><p>Nenhuma recomendação enviada ainda.</p><span>As indicações enviadas aparecerão aqui com a resposta da pessoa.</span></div>}
          {!!(aba === 'recebidas' ? recomendacoes.length : enviadas.length) && !recomendacoesVisiveis.length && (
            <div className="recommendations-empty">
              <FiSearch />
              <p>Nenhuma recomendação encontrada.</p>
              <span>Tente mudar a busca ou a situação selecionada.</span>
            </div>
          )}
          </div>
        </section>
        {salvandoRecomendacao && (
          <div className="recommendation-list-modal" role="dialog" aria-modal="true" aria-label="Salvar filme em uma lista">
            <button type="button" className="recommendation-list-modal__backdrop" onClick={fecharSalvar} aria-label="Fechar" />
            <div className="recommendation-list-modal__content">
              <div className="recommendation-list-modal__heading">
                <div>
                  <p className="page-eyebrow">Guardar para depois</p>
                  <h2>Salvar em uma lista</h2>
                  <span>{nomesFilmes[salvandoRecomendacao.conteudoId]}</span>
                </div>
                <button type="button" onClick={fecharSalvar} aria-label="Fechar"><FiX /></button>
              </div>
              <div className="recommendation-list-modal__lists">
                {listas.map(lista => (
                  <button type="button" key={lista.id} onClick={() => adicionarEmLista(lista.id)}>
                    <FiBookmark />
                    <span><strong>{lista.nome}</strong><small>{lista.tipo === 'WATCHLIST' ? 'Watchlist' : 'Lista'} · {lista.quantidadeTotalDeFilmes} filmes</small></span>
                  </button>
                ))}
                {!listas.length && <p>Você ainda não possui listas.</p>}
              </div>
              <div className="recommendation-list-modal__new">
                <strong><FiPlus /> Criar uma nova Watchlist</strong>
                <div>
                  <input maxLength="80" value={novaListaNome} onChange={e => setNovaListaNome(e.target.value)}
                    placeholder="Nome da nova Watchlist" />
                  <button type="button" onClick={criarWatchlistEAdicionar}>Criar e adicionar</button>
                </div>
              </div>
            </div>
          </div>
        )}
        {avaliandoRecomendacao && (
          <div className="recommendation-list-modal" role="dialog" aria-modal="true" aria-label="Avaliar filme recomendado">
            <button type="button" className="recommendation-list-modal__backdrop" onClick={fecharAvaliacao} aria-label="Fechar" />
            <div className="recommendation-list-modal__content recommendation-review-modal">
              <div className="recommendation-list-modal__heading">
                <div>
                  <p className="page-eyebrow">Depois da sessão</p>
                  <h2>Como foi o filme?</h2>
                  <span>{nomesFilmes[avaliandoRecomendacao.conteudoId]}</span>
                </div>
                <button type="button" onClick={fecharAvaliacao} aria-label="Fechar"><FiX /></button>
              </div>
              <div className="recommendation-review-modal__stars" aria-label="Nota">
                {[1, 2, 3, 4, 5].map(estrela => (
                  <button type="button" key={estrela} onClick={() => setAvaliacaoPosterior(atual => ({ ...atual, nota: estrela }))}
                    className={estrela <= avaliacaoPosterior.nota ? 'is-selected' : ''} aria-label={`${estrela} estrelas`}>
                    <FiStar />
                  </button>
                ))}
                <span>{avaliacaoPosterior.nota ? `${avaliacaoPosterior.nota}/5` : 'Escolha sua nota'}</span>
              </div>
              <textarea maxLength="500" value={avaliacaoPosterior.comentario}
                onChange={e => setAvaliacaoPosterior(atual => ({ ...atual, comentario: e.target.value }))}
                placeholder="Conte o que achou do filme (opcional)" />
              <div className="recommendation-review-modal__footer">
                <label>
                  Visibilidade
                  <select value={avaliacaoPosterior.visibilidade}
                    onChange={e => setAvaliacaoPosterior(atual => ({ ...atual, visibilidade: e.target.value }))}>
                    <option value="PUBLICA">Pública</option>
                    <option value="PRIVADA">Privada</option>
                  </select>
                </label>
                <span>{avaliacaoPosterior.comentario.length}/500</span>
                <button type="button" onClick={enviarAvaliacaoPosterior}>Publicar avaliação</button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
