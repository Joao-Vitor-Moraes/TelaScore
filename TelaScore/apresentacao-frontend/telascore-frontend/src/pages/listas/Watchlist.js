import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowRight, FiBookmark, FiClock, FiPlus } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { listaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './listas.css';

export default function Watchlist() {
  const { sessao } = useAuth();
  const [listas, setListas] = useState([]);
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    listaService.listarPorUsuario(sessao.id, sessao.id)
      .then(todas => setListas(todas.filter(l => l.tipo === 'WATCHLIST')))
      .catch(() => setErro('Erro ao carregar watchlists.'));
  }, [sessao.id]);

  return (
    <div className="cinema-page">
      <Navbar />
      <main className="cinema-container">
        <div className="page-heading">
          <div>
            <p className="page-eyebrow">Próximas sessões</p>
            <h1 className="page-title">Minha watchlist</h1>
            <p className="page-description">Guarde os títulos que ainda estão esperando o play.</p>
          </div>
          <button className="btn-primary" onClick={() => navigate('/listas/nova?tipo=WATCHLIST')}><FiPlus /> Nova watchlist</button>
        </div>

        {erro && <div className="empty-state">{erro}</div>}
        {!erro && listas.length === 0 && (
          <div className="empty-state list-empty"><FiBookmark size={35} /><h3>Nada na fila por enquanto</h3><p>Crie uma watchlist e nunca mais esqueça uma indicação.</p></div>
        )}

        <div className="collection-grid">
          {listas.map((lista, index) => (
            <button key={lista.id} className={`collection-card watch-card palette-${(index + 2) % 4}`} onClick={() => navigate(`/listas/${lista.id}`)}>
              <div className="collection-card__art"><FiClock /><span>PLAY</span></div>
              <div className="collection-card__content">
                <span className="collection-card__label">Para assistir</span>
                <h3>{lista.nome}</h3>
                <p>{lista.quantidadeTotalDeFilmes} título(s) na fila</p>
                <span className="collection-card__open">Ver watchlist <FiArrowRight /></span>
              </div>
            </button>
          ))}
        </div>
      </main>
    </div>
  );
}
