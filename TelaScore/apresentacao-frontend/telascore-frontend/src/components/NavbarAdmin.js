import { useLocation, useNavigate } from 'react-router-dom';
import { FiFileText, FiUsers } from 'react-icons/fi';

export default function NavbarAdmin() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <nav className="admin-nav">
      <button className={location.pathname === '/admin/solicitacoes' ? 'is-active' : ''}
        onClick={() => navigate('/admin/solicitacoes')}>
        <FiFileText /> Solicitações
      </button>
      <button className={location.pathname === '/admin/usuarios' ? 'is-active' : ''}
        onClick={() => navigate('/admin/usuarios')}>
        <FiUsers /> Usuários
      </button>
    </nav>
  );
}
