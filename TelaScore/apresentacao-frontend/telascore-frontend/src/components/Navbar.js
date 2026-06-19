import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiMenu, FiSearch, FiHome, FiBell } from 'react-icons/fi';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const [menuAberto, setMenuAberto] = useState(false);
  const navigate = useNavigate();
  const { sessao, logout } = useAuth();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <nav style={styles.nav}>
      <div style={styles.esquerda}>
        <button style={styles.hamburguer} onClick={() => setMenuAberto(!menuAberto)}><FiMenu size={20} /></button>
        <span style={styles.logo}>TelaScore</span>
        {menuAberto && (
          <div style={styles.menu}>
            <button style={styles.menuItem} onClick={() => { navigate('/perfil'); setMenuAberto(false); }}>Meu Perfil</button>
            <button style={styles.menuItem} onClick={() => { navigate('/listas'); setMenuAberto(false); }}>Minhas Listas</button>
            <button style={styles.menuItem} onClick={() => { navigate('/watchlist'); setMenuAberto(false); }}>Watchlist</button>
            <button style={styles.menuItem} onClick={() => { navigate('/solicitacoes'); setMenuAberto(false); }}>Solicitações</button>
            <button style={styles.menuItem} onClick={() => { navigate('/calendario'); setMenuAberto(false); }}>Calendário</button>
            <button style={styles.menuItem} onClick={() => { navigate('/eventos'); setMenuAberto(false); }}>Eventos</button>
            {sessao?.papel === 'ADMIN' && (
              <button style={{ ...styles.menuItem, color: '#f97316' }} onClick={() => { navigate('/admin/solicitacoes'); setMenuAberto(false); }}>
                Painel Admin
              </button>
            )}
            <button style={styles.menuItem} onClick={() => { navigate('/configuracoes'); setMenuAberto(false); }}>Configurações</button>
            {sessao && (
              <div style={styles.usuarioInfo}>
                <span style={styles.usuarioPapel}>{sessao.papel}</span>
                <span style={styles.usuarioId}>ID: {sessao.id}</span>
              </div>
            )}
            <button style={{ ...styles.menuItem, color: '#e94560' }} onClick={handleLogout}>Sair</button>
          </div>
        )}
      </div>

      <div style={styles.direita}>
        <div style={styles.pesquisaWrapper}>
          <FiSearch size={16} style={{ color: '#aaa' }} />
          <input style={styles.pesquisa} type="text" placeholder="Pesquisar..." />
        </div>
        <button style={styles.icone} onClick={() => navigate('/')}><FiHome size={20} /></button>
        <button style={styles.icone}><FiBell size={20} /></button>
      </div>
    </nav>
  );
}

const styles = {
  nav: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '12px 24px',
    backgroundColor: '#1a1a2e',
    color: 'white',
    position: 'relative',
  },
  esquerda: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    position: 'relative',
  },
  logo: {
    fontWeight: 'bold',
    fontSize: '20px',
    color: '#e94560',
  },
  hamburguer: {
    background: 'none',
    border: 'none',
    color: 'white',
    fontSize: '20px',
    cursor: 'pointer',
  },
  menu: {
    position: 'absolute',
    top: '40px',
    left: '0',
    backgroundColor: '#16213e',
    borderRadius: '8px',
    padding: '8px 0',
    zIndex: 100,
    minWidth: '180px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.4)',
  },
  menuItem: {
    display: 'block',
    width: '100%',
    padding: '10px 16px',
    background: 'none',
    border: 'none',
    color: 'white',
    textAlign: 'left',
    cursor: 'pointer',
    fontSize: '14px',
  },
  direita: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
  },
  pesquisaWrapper: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    backgroundColor: '#16213e',
    borderRadius: '20px',
    padding: '6px 12px',
  },
  pesquisa: {
    border: 'none',
    backgroundColor: 'transparent',
    color: 'white',
    outline: 'none',
    fontSize: '14px',
  },
  icone: {
    background: 'none',
    border: 'none',
    fontSize: '20px',
    cursor: 'pointer',
    color: 'white',
  },
  usuarioInfo: {
    display: 'flex',
    flexDirection: 'column',
    padding: '8px 16px',
    borderTop: '1px solid #2a2a4a',
    borderBottom: '1px solid #2a2a4a',
    gap: '2px',
  },
  usuarioPapel: {
    fontSize: '11px',
    color: '#e94560',
    fontWeight: 'bold',
    letterSpacing: '1px',
  },
  usuarioId: {
    fontSize: '11px',
    color: '#aaa',
  },
};
