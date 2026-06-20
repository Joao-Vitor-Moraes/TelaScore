import { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { FiBell, FiChevronDown, FiFilm, FiLogOut, FiMenu, FiShield, FiUser, FiX } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';
import { filmeService, metaService, recomendacaoService, usuarioService } from '../services/api';

const links = [
  { label: 'Filmes', path: '/filmes' },
  { label: 'Listas', path: '/listas' },
  { label: 'Watchlist', path: '/watchlist' },
  { label: 'Metas', path: '/metas' },
  { label: 'Recomendações', path: '/recomendacoes' },
  { label: 'Comunidades', path: '/comunidades' },
  { label: 'Notícias', path: '/noticias' },
  { label: 'Calendário', path: '/calendario' },
  { label: 'Eventos', path: '/eventos' },
  { label: 'Solicitações', path: '/solicitacoes' },
  { label: 'Denúncias', path: '/denuncias' },
  { label: 'Quizzes', path: '/quiz' },
  { label: 'Mensagens', path: '/mensagens' },
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
        const [recebidas, filmes, metasConcluidas] = await Promise.all([
          recomendacaoService.listar(),
          filmeService.listar(),
          metaService.listarNotificacoes(),
        ]);
        if (!ativo) return;
        const titulos = Object.fromEntries(filmes.map(filme => [String(filme.id), filme.titulo]));
        const recomendacoes = recebidas
            .filter(recomendacao => recomendacao.status === 'PENDENTE')
            .map(recomendacao => ({
              ...recomendacao,
              chave: `recomendacao-${recomendacao.id}`,
              tipoNotificacao: 'RECOMENDACAO',
              titulo: titulos[String(recomendacao.conteudoId)] || 'Filme recomendado',
            }));
        const notificacoesMetas = metasConcluidas.map(notificacao => ({
          ...notificacao,
          chave: `meta-${notificacao.id}`,
          tipoNotificacao: 'META',
          titulo: 'Meta concluída',
          mensagem: `${notificacao.tituloMeta} — você ganhou ${notificacao.pontosGanhos} pontos.`,
          dataGeracao: notificacao.dataCriacao,
        }));
        setNotificacoes([...recomendacoes, ...notificacoesMetas]
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
      if (notificacao.tipoNotificacao === 'META') {
        await metaService.visualizarNotificacao(notificacao.id);
      } else {
        await recomendacaoService.visualizar(notificacao.id);
      }
      setNotificacoes(atuais => atuais.filter(item => item.chave !== notificacao.chave));
    } catch {
    }
    navegar(notificacao.tipoNotificacao === 'META' ? '/metas' : '/recomendacoes');
  }

  function handleLogout() {
    logout();
    navigate('/login');
  }

  const rotaSolicitacoes = sessao?.papel === 'ADMIN' ? '/admin/solicitacoes' : '/solicitacoes';
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
                  {links.map(link => (
                      <button key={link.path} className="dropdown-action"
                              onClick={() => navegar(link.label === 'Solicitações' ? rotaSolicitacoes : link.path)}>
                        {link.label}
                      </button>
                  ))}
                </div>
            )}
          </div>

          <button className="brand" onClick={() => navegar('/filmes')}>
            <span className="brand__mark"><FiFilm /></span>
            <span className="brand__name">TelaScore</span>
          </button>

          <nav className="desktop-nav">
            {links.map(link => {
              const path = link.label === 'Solicitações' ? rotaSolicitacoes : link.path;
              const ativo = location.pathname === path || (path !== '/filmes' && location.pathname.startsWith(`${path}/`));
              return (
                  <button key={link.path} className={`nav-link ${ativo ? 'is-active' : ''}`} onClick={() => navegar(path)}>
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
                        <span>
                          {notificacao.tipoNotificacao === 'META'
                            ? 'Conquista nas suas metas'
                            : `Recomendado por @${notificacao.remetenteApelido || 'usuário'}`}
                        </span>
                              {notificacao.mensagem && <small>“{notificacao.mensagem}”</small>}
                              <time>{formatarDataNotificacao(notificacao.dataGeracao)}</time>
                      </span>
                          </button>
                      ))}
                    </div>
                    <button className="notification-all" onClick={() => navegar('/recomendacoes')}>
                      Ver recomendações
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
