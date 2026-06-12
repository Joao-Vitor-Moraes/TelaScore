import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { filmeService } from '../../services/api';

export default function Filmes() {
  const [filmes, setFilmes] = useState([]);
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    filmeService.listar()
      .then(setFilmes)
      .catch(() => setErro('Não foi possível carregar os filmes.'));
  }, []);

  async function handleRemover(id) {
    if (!window.confirm('Remover este filme?')) return;
    try {
      await filmeService.remover(id);
      setFilmes(filmes.filter(f => f.id !== id));
    } catch {
      alert('Erro ao remover filme.');
    }
  }

  return (
    <div style={{ maxWidth: 800, margin: '40px auto', padding: '0 20px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1 style={{ fontSize: 28, fontWeight: 700 }}>Filmes</h1>
        <button
          onClick={() => navigate('/filmes/novo')}
          style={{ background: '#e50914', color: '#fff', border: 'none', borderRadius: 6, padding: '10px 20px', cursor: 'pointer', fontWeight: 600 }}
        >
          + Cadastrar filme
        </button>
      </div>

      {erro && <p style={{ color: 'red' }}>{erro}</p>}

      {filmes.length === 0 && !erro && (
        <p style={{ color: '#888' }}>Nenhum filme cadastrado ainda.</p>
      )}

      <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
        {filmes.map(filme => (
          <div key={filme.id} style={{ border: '1px solid #ddd', borderRadius: 8, padding: 16, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <strong style={{ fontSize: 16 }}>{filme.titulo}</strong>
              <span style={{ marginLeft: 10, color: '#888', fontSize: 14 }}>{filme.anoLancamento}</span>
              <div style={{ fontSize: 13, color: '#555', marginTop: 4 }}>
                Diretor: {filme.nomeDiretor} · Nota: {filme.mediaNotas > 0 ? `⭐ ${filme.mediaNotas.toFixed(1)}` : 'sem avaliações'}
              </div>
            </div>
            <div style={{ display: 'flex', gap: 8 }}>
              <button onClick={() => navigate(`/filmes/${filme.id}`)} style={{ padding: '6px 14px', borderRadius: 6, border: '1px solid #aaa', cursor: 'pointer' }}>
                Ver
              </button>
              <button onClick={() => navigate(`/filmes/${filme.id}/editar`)} style={{ padding: '6px 14px', borderRadius: 6, border: '1px solid #aaa', cursor: 'pointer' }}>
                Editar
              </button>
              <button onClick={() => handleRemover(filme.id)} style={{ padding: '6px 14px', borderRadius: 6, border: '1px solid #e50914', color: '#e50914', cursor: 'pointer', background: 'none' }}>
                Remover
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}