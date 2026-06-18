import { useLocation, useNavigate } from 'react-router-dom';
import { FiFileText, FiFlag, FiUsers } from 'react-icons/fi';

export default function NavbarAdmin() {
  const navigate = useNavigate();
  const location = useLocation();

  function estiloItem(path) {
    return location.pathname === path
      ? { ...styles.menuItem, ...styles.menuItemAtivo }
      : styles.menuItem;
  }

  return (
    <nav style={styles.nav}>
      <div style={styles.links}>
        <button style={estiloItem('/admin/solicitacoes')} onClick={() => navigate('/admin/solicitacoes')}>
          <FiFileText size={15} />
          Solicitações
        </button>
        <button style={estiloItem('/admin/usuarios')} onClick={() => navigate('/admin/usuarios')}>
          <FiUsers size={15} />
          Usuários
        </button>
        <button style={estiloItem('/admin/denuncias')} onClick={() => navigate('/admin/denuncias')}>
          <FiFlag size={15} />
          Denúncias
        </button>
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
    gap: '16px',
    flexWrap: 'wrap',
  },
  logo: {
    fontWeight: 'bold',
    fontSize: '20px',
    color: '#e94560',
  },
  links: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
    flexWrap: 'wrap',
  },
  menuItem: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '8px',
    padding: '8px 12px',
    background: 'transparent',
    border: '1px solid transparent',
    borderRadius: '6px',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
  },
  menuItemAtivo: {
    color: '#e94560',
    borderColor: '#e94560',
    backgroundColor: 'rgba(233, 69, 96, 0.08)',
  },
};
