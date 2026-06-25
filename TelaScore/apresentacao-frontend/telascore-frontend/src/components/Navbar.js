import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { FiBell, FiChevronDown, FiCompass, FiFilm, FiLogOut, FiMenu, FiStar, FiTrendingUp, FiUsers, FiShield, FiUser, FiX } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';
import { metaService, recomendacaoService, usuarioService } from '../services/api';

const links = [
  { label: 'Inicio', path: '/filmes', icon: FiStar, rotas: ['/', '/filmes'], ignorarPrefixo: ['/filmes'] },
  { label: 'Filmes', path: '/hub/filmes', icon: FiFilm, rotas: ['/hub/filmes', '/filmes/novo', '/filmes', '/listas', '/watchlist', '/solicitacoes', '/admin/solicitacoes'], ignorarPrefixo: ['/filmes'] },
  { label: 'Social', path: '/hub/social', icon: FiUsers, rotas: ['/hub/social', '/amigos', '/mensagens', '/comunidades', '/denuncias', '/admin/denuncias'] },
  { label: 'Descobrir', path: '/hub/descobrir', icon: FiCompass, rotas: ['/hub/descobrir', '/noticias', '/quiz', '/recomendacoes', '/eventos', '/calendario'] },
  { label: 'Progresso', path: '/hub/progresso', icon: FiTrendingUp, rotas: ['/hub/progresso', '/metas', '/recompensas'] },
];

export default function Navbar() {
  const [perfilAberto, setPerfilAberto] = useState(false);
  const [mobileAberto, setMobileAberto] = useState(false);
  const [usuario, setUsuario] = useState(null);
  const [notificacoes, setNotificacoes] = useState([]);
  const [notificacoesAbertas, setNotificacoesAbertas] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { sessao, logout } = useAuth();

  useEffect(() => {
    let ativo = true;
    async function carregarPerfil() {
      try {
        const dados = await usuarioService.meuUsuario();
        if (ativo) setUsuario(dados);
      } catch {
        if (ativo) setUsuario(null);
      }
    }

    carregarPerfil();
    window.addEventListener('telascore:perfil-atualizado', carregarPerfil);
    return () => {
      ativo = false;
      window.removeEventListener('telascore:perfil-atualizado', carregarPerfil);
    };
  }, [sessao?.id]);

  useEffect(() => {
    let ativo = true;
    async function carregarNotificacoes() {
      try {
        const [recebidas, sistema] = await Promise.all([
          recomendacaoService.listar(),
          metaService.listarNotificacoes(),
        ]);
        if (!ativo) return;

        const recomendacoes = recebidas
          .filter(recomendacao => recomendacao.status === 'PENDENTE')
          .map(recomendacao => ({
            ...recomendacao,
            chave: `recomendacao-${recomendacao.id}`,
            tipoNotificacao: 'RECOMENDACAO',
            titulo: recomendacao.tituloConteudo || tituloConteudoRecomendado(recomendacao),
            subtitulo: `Recomendado por @${recomendacao.remetenteApelido || 'usuário'}`,
            rota: '/recomendacoes',
          }));
        const notificacoesSistema = sistema.map(notificacao => ({
          ...notificacao,
          chave: `sistema-${notificacao.id}`,
          tipoNotificacao: notificacao.tipo || 'META',
          titulo: notificacao.titulo || 'Notificação',
          subtitulo: subtituloNotificacao(notificacao.tipo || 'META'),
          mensagem: notificacao.mensagem || `${notificacao.tituloMeta} - status: concluída.`,
          dataGeracao: notificacao.dataCriacao,
          rota: notificacao.rota || '/metas',
        }));

        setNotificacoes([...recomendacoes, ...notificacoesSistema]
          .sort((a, b) => new Date(b.dataGeracao) - new Date(a.dataGeracao)));
      } catch {
        if (ativo) setNotificacoes([]);
      }
    }

    carregarNotificacoes();
    const intervalo = window.setInterval(carregarNotificacoes, 30000);
    window.addEventListener('telascore:recomendacoes-atualizadas', carregarNotificacoes);
    window.addEventListener('telascore:notificacoes-atualizadas', carregarNotificacoes);
    return () => {
      ativo = false;
      window.clearInterval(intervalo);
      window.removeEventListener('telascore:recomendacoes-atualizadas', carregarNotificacoes);
      window.removeEventListener('telascore:notificacoes-atualizadas', carregarNotificacoes);
    };
  }, [sessao?.id]);

  const navegar = path => {
    navigate(path);
    setPerfilAberto(false);
    setNotificacoesAbertas(false);
    setMobileAberto(false);
  };

  async function abrirNotificacao(notificacao) {
    try {
      if (notificacao.tipoNotificacao === 'RECOMENDACAO') {
        await recomendacaoService.visualizar(notificacao.id);
      } else {
        await metaService.visualizarNotificacao(notificacao.id);
      }
      setNotificacoes(atuais => atuais.filter(item => item.chave !== notificacao.chave));
    } catch {
    }
    navegar(notificacao.rota || (notificacao.tipoNotificacao === 'RECOMENDACAO' ? '/recomendacoes' : '/metas'));
  }

  function handleLogout() {
    logout();
    navigate('/login');
  }

  const nomeExibicao = usuario?.nome || (sessao?.papel === 'ADMIN' ? 'Administrador' : 'Usuário');
  const detalheExibicao = usuario?.apelido ? `@${usuario.apelido.replace(/^@/, '')}` : usuario?.papel;
  const inicial = (usuario?.apelido || usuario?.nome || '?').trim().charAt(0).toUpperCase();

  return (
    <header className="site-header">
      <div className="site-header__inner">
        <div className="mobile-menu-wrap">
          <button className="mobile-trigger" onClick={() => setMobileAberto(v => !v)} aria-label="Abrir menu">
            {mobileAberto ? <FiX size={21} /> : <FiMenu size={21} />}
          </button>
          {mobileAberto && (
            <div className="header-dropdown mobile-dropdown">
              {links.map(link => {
                const Icone = link.icon;
                return (
                <button
                  key={link.path}
                  className="dropdown-action"
                  onClick={() => navegar(link.path)}
                >
                  <Icone />
                  {link.label}
                </button>
              );})}
            </div>
          )}
        </div>

        <button className="brand" onClick={() => navegar('/filmes')}>
          <span className="brand__mark"><FiFilm /></span>
          <span className="brand__name">TelaScore</span>
        </button>

        <nav className="desktop-nav">
          {links.map(link => {
            const path = link.path;
            const ativo = link.rotas.some(rota => {
              const prefixoBloqueado = link.ignorarPrefixo?.includes(rota);
              return location.pathname === rota || (!prefixoBloqueado && location.pathname.startsWith(`${rota}/`));
            });
            const Icone = link.icon;
            return (
              <button key={link.path} className={`nav-link ${ativo ? 'is-active' : ''}`} onClick={() => navegar(path)}>
                <Icone />
                {link.label}
              </button>
            );
          })}
        </nav>

        <div className="site-header__actions">
          <div className="notification-wrap">
            <button
              className={`header-icon ${notificacoesAbertas ? 'is-active' : ''}`}
              aria-label={`Notificações${notificacoes.length ? `: ${notificacoes.length} novas` : ''}`}
              onClick={() => {
                setNotificacoesAbertas(abertas => !abertas);
                setPerfilAberto(false);
              }}
            >
              <FiBell size={18} />
              {notificacoes.length > 0 && (
                <span className="notification-badge">{notificacoes.length > 9 ? '9+' : notificacoes.length}</span>
              )}
            </button>
            {notificacoesAbertas && (
              <div className="header-dropdown notification-dropdown">
                <div className="notification-heading">
                  <div>
                    <strong>Notificações</strong>
                    <span>{notificacoes.length ? `${notificacoes.length} nova${notificacoes.length > 1 ? 's' : ''}` : 'Tudo em dia'}</span>
                  </div>
                  <FiBell />
                </div>
                <div className="notification-list">
                  {notificacoes.length === 0 ? (
                    <div className="notification-empty">Nenhuma notificação nova por enquanto.</div>
                  ) : notificacoes.slice(0, 5).map(notificacao => (
                    <button
                      key={notificacao.chave}
                      className="notification-item"
                      onClick={() => abrirNotificacao(notificacao)}
                    >
                      <span className="notification-dot" />
                      <span className="notification-content">
                        <strong>{notificacao.titulo}</strong>
                        <span>{notificacao.subtitulo || 'Atualização do sistema'}</span>
                        {notificacao.mensagem && <small>"{notificacao.mensagem}"</small>}
                        <time>{formatarDataNotificacao(notificacao.dataGeracao)}</time>
                      </span>
                    </button>
                  ))}
                </div>
                <button className="notification-all" onClick={() => navegar(notificacoes[0]?.rota || '/recomendacoes')}>
                  Abrir mais recente
                </button>
              </div>
            )}
          </div>
          <div className="profile-wrap">
            <button className="profile-trigger" onClick={() => setPerfilAberto(v => !v)}>
              <span className="profile-avatar">
                {usuario?.avatarUrl ? <img src={usuario.avatarUrl} alt="" /> : inicial}
              </span>
              <span className="profile-meta">
                <strong>{nomeExibicao}</strong>
                {detalheExibicao && <span>{detalheExibicao}</span>}
              </span>
              <FiChevronDown size={14} />
            </button>
            {perfilAberto && (
              <div className="header-dropdown">
                <div className="dropdown-user">
                  <strong>{nomeExibicao}</strong>
                  {detalheExibicao && <span>{detalheExibicao}</span>}
                </div>
                <button className="dropdown-action" onClick={() => navegar('/hub/perfil')}><FiUser /> Perfil e conta</button>
                <button className="dropdown-action" onClick={() => navegar('/meuusuario')}><FiUser /> Meu perfil</button>
                {sessao?.papel === 'ADMIN' && (
                  <button className="dropdown-action" onClick={() => navegar('/admin/solicitacoes')}><FiShield /> Administração</button>
                )}
                <button className="dropdown-action danger" onClick={handleLogout}><FiLogOut /> Sair</button>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}

function subtituloNotificacao(tipo) {
  const subtitulos = {
    META: 'Conquista nas suas metas',
    META_SISTEMA: 'Nova meta disponível',
    MENSAGEM: 'Mensagem privada',
    AMIZADE: 'Nova conexão social',
    SOLICITACAO: 'Atualização de solicitação',
    RECOMENDACAO_RESPOSTA: 'Resposta de recomendação',
  };
  return subtitulos[tipo] || 'Atualização do sistema';
}

function tituloConteudoRecomendado(recomendacao) {
  if (recomendacao.tipoConteudo === 'FILME') return `Filme #${recomendacao.conteudoId}`;
  return 'Conteúdo recomendado';
}

function formatarDataNotificacao(valor) {
  if (!valor) return '';
  const data = new Date(valor);
  if (Number.isNaN(data.getTime())) return '';
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  }).format(data);
}
