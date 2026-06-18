import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { FiBell, FiChevronDown, FiFilm, FiLogOut, FiMenu, FiShield, FiUser, FiX } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';

const links = [
  { label: 'Filmes', path: '/filmes' },
  { label: 'Listas', path: '/listas' },
  { label: 'Watchlist', path: '/watchlist' },
  { label: 'Metas', path: '/metas' },
  { label: 'Recomendações', path: '/recomendacoes' },
  { label: 'Solicitações', path: '/solicitacoes' },
];

export default function Navbar() {
  const [perfilAberto, setPerfilAberto] = useState(false);
  const [mobileAberto, setMobileAberto] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { sessao, logout } = useAuth();

  const navegar = path => {
    navigate(path);
    setPerfilAberto(false);
    setMobileAberto(false);
  };

  function handleLogout() {
    logout();
    navigate('/login');
  }

  const rotaSolicitacoes = sessao?.papel === 'ADMIN' ? '/admin/solicitacoes' : '/solicitacoes';

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
          <button className="header-icon" aria-label="Notificações"><FiBell size={18} /></button>
          <div className="profile-wrap">
            <button className="profile-trigger" onClick={() => setPerfilAberto(v => !v)}>
              <span className="profile-avatar">{sessao?.papel === 'ADMIN' ? 'A' : 'C'}</span>
              <span className="profile-meta">
                <strong>{sessao?.papel === 'ADMIN' ? 'Administrador' : 'Cinéfilo'}</strong>
                <span>Conta #{sessao?.id}</span>
              </span>
              <FiChevronDown size={14} />
            </button>
            {perfilAberto && (
              <div className="header-dropdown">
                <div className="dropdown-user">
                  <strong>{sessao?.papel === 'ADMIN' ? 'Painel administrativo' : 'Sua experiência'}</strong>
                  <span>{sessao?.papel}</span>
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
