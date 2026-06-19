import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiEdit2, FiFilm, FiInfo, FiPlay, FiPlus, FiSearch, FiStar, FiTrash2 } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { filmeService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './filmes.css';

const DIRETORES_GHIBLI = new Set([
  'Hayao Miyazaki', 'Isao Takahata', 'Yoshifumi Kondō', 'Hiroyuki Morita',
  'Gorō Miyazaki', 'Hiromasa Yonebayashi', 'Michaël Dudok de Wit',
]);

const TITULOS_ROMANCE = new Set([
  'La La Land: Cantando Estações', 'Antes do Amanhecer', 'Your Name',
]);

const TITULOS_NERD = new Set([
  'O Senhor dos Anéis: A Sociedade do Anel', 'Matrix', 'Interestelar',
  'Duna', 'De Volta para o Futuro', 'Blade Runner 2049',
]);

const TITULOS_RECENTES = new Set([
  'Billie Eilish - Hit Me Hard and Soft: The Tour (Live in 3D)',
  'KPop Demon Hunters', 'Project Hail Mary', 'O Diabo Veste Prada 2',
  'Hoppers', 'Pecadores', 'F1', 'Wicked: For Good', 'Zootopia 2',
  'The Super Mario Galaxy Movie', 'Star Wars: The Mandalorian e Grogu',
  'Mortal Kombat II', 'Toy Story 5',
]);

const TITULOS_FORA_DA_VITRINE = new Set([
  'Diário de uma Paixão', 'Orgulho e Preconceito', 'Questão de Tempo',
  'Brilho Eterno de uma Mente sem Lembranças', 'Titanic',
  'O Senhor dos Anéis: As Duas Torres', 'O Senhor dos Anéis: O Retorno do Rei',
  'Star Wars: Uma Nova Esperança', 'Star Wars: O Império Contra-Ataca',
  'Harry Potter e a Pedra Filosofal', 'Homem de Ferro', 'Guardiões da Galáxia',
  'Homem-Aranha 2', 'Homem-Aranha 3', 'O Espetacular Homem-Aranha',
  'O Espetacular Homem-Aranha 2', 'Homem-Aranha: Longe de Casa',
]);

const TRAILER_CASTELO_ANIMADO = 'ARCQf2CEr8k';
const PREFIXO_ID_TMDB = 2000000;

export default function Filmes() {
  const { sessao } = useAuth();
  const isAdmin = sessao.papel === 'ADMIN';
  const [filmes, setFilmes] = useState([]);
  const [erro, setErro] = useState(null);
  const [busca, setBusca] = useState('');
  const [trailerKey, setTrailerKey] = useState(null);
  const [trailerPronto, setTrailerPronto] = useState(false);
  const [mostrarTrailer, setMostrarTrailer] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    filmeService.listar()
      .then(setFilmes)
      .catch(() => setErro('Não foi possível carregar os filmes.'));
  }, []);

  const destaque = filmes.find(f => f.titulo === 'O Castelo Animado')
    || filmes.find(f => f.titulo === "Howl's Moving Castle")
    || filmes.find(f => f.titulo === 'A Viagem de Chihiro')
    || filmes.find(f => f.imagemUrl)
    || filmes[0];
  const imagemDestaque = destaque?.titulo === 'O Castelo Animado'
    || destaque?.titulo === "Howl's Moving Castle"
    ? '/howls-moving-castle-hero.jpg'
    : destaque?.imagemUrl;

  useEffect(() => {
    const ehCastelo = destaque
      && ['O Castelo Animado', "Howl's Moving Castle"].includes(destaque.titulo);
    setTrailerKey(ehCastelo ? TRAILER_CASTELO_ANIMADO : null);
    setTrailerPronto(false);
    setMostrarTrailer(false);
  }, [destaque]);

  useEffect(() => {
    if (!trailerKey || !trailerPronto) return undefined;
    const inicio = window.setTimeout(() => setMostrarTrailer(true), 1800);
    return () => window.clearTimeout(inicio);
  }, [trailerKey, trailerPronto]);
  const filtrados = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    return filmes.filter(f =>
      (termo || !TITULOS_FORA_DA_VITRINE.has(f.titulo)) &&
      (f.titulo.toLowerCase().includes(termo) ||
        String(f.anoLancamento || '').includes(termo))
    );
  }, [filmes, busca]);

  const colecoes = useMemo(() => {
    const recentes = filtrados.filter(f => TITULOS_RECENTES.has(f.titulo));
    const ghibli = filtrados.filter(f => DIRETORES_GHIBLI.has(f.nomeDiretor));
    const aranha = filtrados.filter(f => f.titulo.includes('Homem-Aranha'));
    const nerd = filtrados.filter(f => TITULOS_NERD.has(f.titulo));
    const romance = filtrados.filter(f => TITULOS_ROMANCE.has(f.titulo));
    const usadosCuradoria = new Set([...recentes, ...ghibli, ...aranha, ...nerd, ...romance].map(f => f.id));
    const catalogoTmdb = filtrados.filter(f =>
      Number(f.id) >= PREFIXO_ID_TMDB && !usadosCuradoria.has(f.id)
    );
    const sucessosRecentes = catalogoTmdb.filter(f => Number(f.anoLancamento) >= 2020);
    const anos2010 = catalogoTmdb.filter(f => Number(f.anoLancamento) >= 2010 && Number(f.anoLancamento) < 2020);
    const anos2000 = catalogoTmdb.filter(f => Number(f.anoLancamento) >= 2000 && Number(f.anoLancamento) < 2010);
    const classicos = catalogoTmdb.filter(f => Number(f.anoLancamento) < 2000);
    const usados = new Set([
      ...usadosCuradoria,
      ...catalogoTmdb.map(f => f.id),
    ]);
    const outros = filtrados.filter(f => !usados.has(f.id));

    return [
      { titulo: 'Novidades que estão dando o que falar', subtitulo: 'Música, animação e grandes lançamentos de 2025 e 2026', filmes: recentes },
      { titulo: 'Mundos do Studio Ghibli', subtitulo: 'Fantasia, delicadeza e aventuras inesquecíveis', filmes: ghibli },
      { titulo: 'Sucessos recentes', subtitulo: 'Filmes populares lançados a partir de 2020', filmes: sucessosRecentes },
      { titulo: 'Favoritos dos anos 2010', subtitulo: 'Grandes títulos que marcaram a década', filmes: anos2010 },
      { titulo: 'Cinema dos anos 2000', subtitulo: 'Histórias que atravessaram os anos e continuam em alta', filmes: anos2000 },
      { titulo: 'Clássicos para descobrir', subtitulo: 'Filmes populares lançados antes dos anos 2000', filmes: classicos },
      { titulo: 'O seu amigo da vizinhança', subtitulo: 'Todas as eras do Homem-Aranha', filmes: aranha },
      { titulo: 'Uma dose de ficção e aventura', subtitulo: 'Só alguns favoritos para equilibrar a seleção', filmes: nerd },
      { titulo: 'Histórias para se apaixonar', subtitulo: 'Uma seleção menor de romances marcantes', filmes: romance },
      { titulo: 'Grandes filmes, muitas histórias', subtitulo: 'Clássicos e favoritos de diferentes estilos', filmes: outros },
    ].filter(colecao => colecao.filmes.length > 0);
  }, [filtrados]);

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
        <section className="catalog-hero">
          {imagemDestaque && (
            <div className={`catalog-hero__art ${mostrarTrailer ? 'is-playing' : ''}`} aria-hidden="true">
              <img className="catalog-hero__image" src={imagemDestaque} alt="" />
              {trailerKey && (
                <iframe
                  className="catalog-hero__video"
                  src={`https://www.youtube-nocookie.com/embed/${trailerKey}?autoplay=1&mute=1&controls=0&loop=1&playlist=${trailerKey}&playsinline=1&rel=0&modestbranding=1&disablekb=1&start=4`}
                  title="Trailer de O Castelo Animado"
                  allow="autoplay; encrypted-media; picture-in-picture"
                  onLoad={() => setTrailerPronto(true)}
                  tabIndex="-1"
                />
              )}
            </div>
          )}
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

        <div className="catalog-collections">
          {colecoes.map(colecao => (
            <section key={colecao.titulo} className="catalog-section">
              <div className="catalog-section__heading">
                <div>
                  <h2>{colecao.titulo}</h2>
                  <p>{colecao.subtitulo}</p>
                </div>
                <span>{colecao.filmes.length} títulos</span>
              </div>
              <div className="poster-grid">
                {colecao.filmes.map(filme => (
                  <article key={filme.id} className="poster-card">
                    <button className="poster-card__visual" onClick={() => navigate(`/filmes/${filme.id}`)}>
                      {filme.imagemUrl
                        ? <img src={filme.imagemUrl} alt={filme.titulo} loading="lazy" />
                        : <span className="poster-card__placeholder"><FiFilm size={38} /></span>
                      }
                      <span className="poster-card__play"><FiPlay /></span>
                      {filme.mediaNotas > 0 && <span className="poster-card__rating"><FiStar /> {filme.mediaNotas.toFixed(1)}</span>}
                    </button>
                    {isAdmin && (
                      <div className="poster-card__admin">
                        <button onClick={() => navigate(`/filmes/${filme.id}/editar`)} title="Editar"><FiEdit2 /></button>
                        <button className="danger" onClick={() => handleRemover(filme.id)} title="Remover"><FiTrash2 /></button>
                      </div>
                    )}
                    <div className="poster-card__body">
                      <div className="poster-card__text">
                        <h3>{filme.titulo}</h3>
                        <p>{filme.anoLancamento}{filme.nomeDiretor ? ` • ${filme.nomeDiretor}` : ''}</p>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            </section>
          ))}
        </div>

        {filmes.length > 0 && filtrados.length === 0 && <div className="empty-state">Nenhum título combina com sua busca.</div>}
        <p className="tmdb-attribution">
          Este produto usa a API do TMDB, mas não é endossado nem certificado pelo TMDB.
        </p>
      </main>
    </div>
  );
}
