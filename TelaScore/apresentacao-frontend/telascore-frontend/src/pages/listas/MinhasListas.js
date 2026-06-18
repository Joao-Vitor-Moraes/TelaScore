import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowRight, FiFilm, FiLayers, FiPlus } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { listaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './listas.css';

export default function MinhasListas() {
  const { sessao } = useAuth();
  const [listas, setListas] = useState([]);
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    listaService.listarPorUsuario(sessao.id, sessao.id)
      .then(setListas)
      .catch(() => setErro('Erro ao carregar listas.'));
  }, [sessao.id]);

  const normais = listas.filter(l => l.tipo !== 'WATCHLIST');

  return (
    <div className="cinema-page">
      <Navbar />
      <main className="cinema-container">
        <div className="page-heading">
          <div>
            <p className="page-eyebrow">Sua curadoria</p>
            <h1 className="page-title">Minhas listas</h1>
            <p className="page-description">Crie coleções temáticas e organize os filmes que definem o seu gosto.</p>
          </div>
          <button className="btn-primary" onClick={() => navigate('/listas/nova?tipo=NORMAL')}><FiPlus /> Criar lista</button>
        </div>

        {erro && <div className="empty-state">{erro}</div>}
        {!erro && normais.length === 0 && (
          <div className="empty-state list-empty"><FiLayers size={35} /><h3>Sua primeira coleção começa aqui</h3><p>Agrupe favoritos, maratonas e descobertas.</p></div>
        )}

        <div className="collection-grid">
          {normais.map((lista, index) => (
            <button key={lista.id} className={`collection-card palette-${index % 4}`} onClick={() => navigate(`/listas/${lista.id}`)}>
              <div className="collection-card__art">
                <FiFilm />
                <span>{String(index + 1).padStart(2, '0')}</span>
              </div>
              <div className="collection-card__content">
                <span className="collection-card__label">Lista pessoal</span>
                <h3>{lista.nome}</h3>
                <p>{lista.quantidadeTotalDeFilmes} filme(s)</p>
                <span className="collection-card__open">Abrir coleção <FiArrowRight /></span>
              </div>
            </button>
          ))}
        </div>
      </main>
    </div>
  );
}
