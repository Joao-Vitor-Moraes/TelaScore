import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiFilm, FiInfo, FiPlay, FiPlus, FiSearch, FiStar, FiTrash2 } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { filmeService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './filmes.css';

export default function Filmes() {
  const { sessao } = useAuth();
  const isAdmin = sessao.papel === 'ADMIN';
  const [filmes, setFilmes] = useState([]);
  const [erro, setErro] = useState(null);
  const [busca, setBusca] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    filmeService.listar()
      .then(setFilmes)
      .catch(() => setErro('Não foi possível carregar os filmes.'));
  }, []);

  const destaque = filmes.find(f => f.imagemUrl) || filmes[0];
  const filtrados = useMemo(() => filmes.filter(f =>
    f.titulo.toLowerCase().includes(busca.trim().toLowerCase()) ||
    String(f.anoLancamento || '').includes(busca.trim())
  ), [filmes, busca]);

  async function handleRemover(id) {
    if (!window.confirm('Remover este filme?')) return;
    try {
      await filmeService.remover(id);
      setFilmes(prev => prev.filter(f => f.id !== id));
    } catch {
      alert('Erro ao remover filme.');
    }
  }

  return (
    <div className="cinema-page catalog-page">
      <Navbar />

      {destaque && (
        <section className="catalog-hero" style={destaque.imagemUrl ? { '--hero-image': `url("${destaque.imagemUrl}")` } : {}}>
          <div className="catalog-hero__content">
            <p className="page-eyebrow">Em destaque no TelaScore</p>
            <h1>{destaque.titulo}</h1>
            <div className="hero-meta">
              <span className="match">Em alta</span>
              <span>{destaque.anoLancamento}</span>
              {destaque.mediaNotas > 0 && <span><FiStar /> {destaque.mediaNotas.toFixed(1)}</span>}
            </div>
            <p>{destaque.sinopse || 'Explore, avalie e organize as histórias que merecem ficar na memória.'}</p>
            <div className="hero-actions">
              <button className="hero-play" onClick={() => navigate(`/filmes/${destaque.id}`)}><FiPlay /> Ver detalhes</button>
              <button className="hero-info" onClick={() => navigate('/listas')}><FiInfo /> Minhas listas</button>
            </div>
          </div>
        </section>
      )}

      <main className="cinema-container catalog-content">
        <div className="catalog-toolbar">
          <div>
            <p className="page-eyebrow">Catálogo completo</p>
            <h2 className="page-title">O que você vai assistir?</h2>
          </div>
          <div className="catalog-toolbar__actions">
            <label className="catalog-search">
              <FiSearch />
              <input value={busca} onChange={e => setBusca(e.target.value)} placeholder="Buscar título ou ano" />
            </label>
            {isAdmin && <button className="btn-primary" onClick={() => navigate('/filmes/novo')}><FiPlus /> Novo filme</button>}
          </div>
        </div>

        {erro && <div className="empty-state">{erro}</div>}
        {!erro && filmes.length === 0 && <div className="empty-state"><FiFilm size={32} /><p>Nenhum filme cadastrado ainda.</p></div>}

        <div className="poster-grid">
          {filtrados.map(filme => (
            <article key={filme.id} className="poster-card">
              <button className="poster-card__visual" onClick={() => navigate(`/filmes/${filme.id}`)}>
                {filme.imagemUrl
                  ? <img src={filme.imagemUrl} alt={filme.titulo} />
                  : <span className="poster-card__placeholder"><FiFilm size={38} /></span>
                }
                <span className="poster-card__play"><FiPlay /></span>
                {filme.mediaNotas > 0 && <span className="poster-card__rating"><FiStar /> {filme.mediaNotas.toFixed(1)}</span>}
              </button>
              <div className="poster-card__body">
                <div>
                  <h3>{filme.titulo}</h3>
                  <p>{filme.anoLancamento}{filme.nomeDiretor ? ` • ${filme.nomeDiretor}` : ''}</p>
                </div>
                {isAdmin && (
                  <div className="poster-card__admin">
                    <button onClick={() => navigate(`/filmes/${filme.id}/editar`)} title="Editar"><FiEdit2 /></button>
                    <button className="danger" onClick={() => handleRemover(filme.id)} title="Remover"><FiTrash2 /></button>
                  </div>
                )}
              </div>
            </article>
          ))}
        </div>

        {filmes.length > 0 && filtrados.length === 0 && <div className="empty-state">Nenhum título combina com sua busca.</div>}
      </main>
    </div>
  );
}
