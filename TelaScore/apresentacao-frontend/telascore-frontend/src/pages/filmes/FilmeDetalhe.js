import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { filmeService, avaliacaoService } from '../../services/api';
import { FiStar, FiClock, FiUser, FiFilm } from 'react-icons/fi';

const USUARIO_ID = 3;

export default function FilmeDetalhe() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [filme, setFilme] = useState(null);
  const [avaliacoes, setAvaliacoes] = useState([]);
  const [novaAvaliacao, setNovaAvaliacao] = useState({ valorNota: 5, comentario: '', visibilidade: 'PUBLICA' });
  const [erro, setErro] = useState(null);
  const [editandoId, setEditandoId] = useState(null);
  const [edicao, setEdicao] = useState({ valorNota: 5, resenha: '' });

  useEffect(() => {
    filmeService.obter(id).then(setFilme).catch(() => setErro('Filme não encontrado.'));
    avaliacaoService.listarPorFilme(id, USUARIO_ID).then(setAvaliacoes).catch(() => {});
  }, [id]);

  async function handleAvaliar(e) {
    e.preventDefault();
    try {
      await avaliacaoService.avaliar({
        filmeId: parseInt(id),
        usuarioId: USUARIO_ID,
        valorNota: parseInt(novaAvaliacao.valorNota),
        comentario: novaAvaliacao.comentario,
        visibilidade: novaAvaliacao.visibilidade,
      });
      const atualizadas = await avaliacaoService.listarPorFilme(id, USUARIO_ID);
      setAvaliacoes(atualizadas);
      setNovaAvaliacao({ valorNota: 5, comentario: '', visibilidade: 'PUBLICA' });
    } catch {
      alert('Erro ao enviar avaliação.');
    }
  }

  async function handleRemoverAvaliacao(avaliacaoId) {
    if (!window.confirm('Remover esta avaliação?')) return;
    try {
      await avaliacaoService.remover(avaliacaoId);
      setAvaliacoes(avaliacoes.filter(a => a.avaliacaoId !== avaliacaoId));
    } catch {
      alert('Erro ao remover avaliação.');
    }
  }

  async function handleSalvarEdicao(avaliacaoId) {
  try {
    const payload = {
      valorNota: parseInt(edicao.valorNota),
      resenha: edicao.resenha,
    };
    console.log('Enviando:', avaliacaoId, payload);
    await avaliacaoService.atualizar(avaliacaoId, payload);
    const atualizadas = await avaliacaoService.listarPorFilme(id, USUARIO_ID);
    setAvaliacoes(atualizadas);
    setEditandoId(null);
  } catch {
    alert('Erro ao editar avaliação.');
  }
}

  const inputStyle = {
    width: '100%', padding: '10px 12px', borderRadius: 6,
    border: '1px solid #ccc', fontSize: 15, marginTop: 4, boxSizing: 'border-box'
  };
  const labelStyle = { fontWeight: 600, fontSize: 14, display: 'block', marginTop: 16 };

  if (erro) return <p style={{ padding: 40, color: 'red' }}>{erro}</p>;
  if (!filme) return <p style={{ padding: 40 }}>Carregando...</p>;

  return (
    <div style={{ maxWidth: 700, margin: '40px auto', padding: '0 20px' }}>
      <button onClick={() => navigate('/filmes')} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#888', marginBottom: 16 }}>
        ← Voltar
      </button>

      {/* Detalhe do filme */}
      <div style={{ display: 'flex', gap: 24, marginBottom: 32 }}>
        {filme.imagemUrl && (
          <img src={filme.imagemUrl} alt={filme.titulo} style={{ width: 140, borderRadius: 8, objectFit: 'cover' }} />
        )}
        <div>
          <h1 style={{ fontSize: 26, fontWeight: 700, marginBottom: 4 }}>{filme.titulo}</h1>
          <p style={{ color: '#888', marginBottom: 8 }}>{filme.anoLancamento} · Diretor: {filme.nomeDiretor}</p>
          <p style={{ marginBottom: 8 }}>
            {filme.mediaNotas > 0 ? `⭐ ${filme.mediaNotas.toFixed(1)} de média` : 'Sem avaliações ainda'}
          </p>
          {filme.sinopse && <p style={{ color: '#444', lineHeight: 1.6 }}>{filme.sinopse}</p>}
          <button
            onClick={() => navigate(`/filmes/${id}/editar`)}
            style={{ marginTop: 12, padding: '8px 16px', borderRadius: 6, border: '1px solid #aaa', cursor: 'pointer' }}
          >
            Editar filme
          </button>
        </div>
      </div>

      {/* Avaliações existentes */}
      <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 16 }}>Avaliações</h2>
      {avaliacoes.length === 0 && <p style={{ color: '#888', marginBottom: 24 }}>Nenhuma avaliação ainda.</p>}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 32 }}>
        {avaliacoes.map(av => (
          <div key={av.avaliacaoId} style={{ border: '1px solid #eee', borderRadius: 8, padding: 14, background: av.visibilidade === 'PRIVADA' ? '#fffbf0' : '#fff' }}>
            {editandoId === av.avaliacaoId ? (
              <div>
                <select
                  value={edicao.valorNota}
                  onChange={e => setEdicao({ ...edicao, valorNota: e.target.value })}
                  style={{ padding: '6px 10px', borderRadius: 6, border: '1px solid #ccc', marginBottom: 8, width: '100%' }}
                >
                  {[1,2,3,4,5].map(n => <option key={n} value={n}>{n} ⭐</option>)}
                </select>
                <textarea
                  value={edicao.resenha}
                  onChange={e => setEdicao({ ...edicao, resenha: e.target.value })}
                  rows={2}
                  style={{ width: '100%', padding: '8px', borderRadius: 6, border: '1px solid #ccc', boxSizing: 'border-box', marginBottom: 8 }}
                />
                <div style={{ display: 'flex', gap: 8 }}>
                  <button onClick={() => handleSalvarEdicao(av.avaliacaoId)} style={{ background: '#e50914', color: '#fff', border: 'none', borderRadius: 6, padding: '6px 16px', cursor: 'pointer', fontWeight: 600 }}>
                    Salvar
                  </button>
                  <button onClick={() => setEditandoId(null)} style={{ background: 'none', border: '1px solid #aaa', borderRadius: 6, padding: '6px 16px', cursor: 'pointer' }}>
                    Cancelar
                  </button>
                </div>
              </div>
            ) : (
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span>
                    {'⭐'.repeat(av.valorNota)}
                    <strong style={{ marginLeft: 8 }}>Usuário {av.usuarioId}</strong>
                    {av.visibilidade === 'PRIVADA' && (
                      <span style={{ marginLeft: 8, fontSize: 11, background: '#f0a500', color: '#fff', borderRadius: 4, padding: '2px 6px' }}>
                        🔒 Privada
                      </span>
                    )}
                  </span>
                  {av.usuarioId === USUARIO_ID && (
                    <div style={{ display: 'flex', gap: 8 }}>
                      <button
                        onClick={() => { setEditandoId(av.avaliacaoId); setEdicao({ valorNota: av.valorNota, resenha: av.resenha || '' }); }}
                        style={{ background: 'none', border: 'none', color: '#555', cursor: 'pointer', fontSize: 13 }}
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => handleRemoverAvaliacao(av.avaliacaoId)}
                        style={{ background: 'none', border: 'none', color: '#e50914', cursor: 'pointer', fontSize: 13 }}
                      >
                        Remover
                      </button>
                    </div>
                  )}
                </div>
                {av.resenha && <p style={{ marginTop: 6, color: '#444' }}>{av.resenha}</p>}
                <p style={{ fontSize: 11, color: '#bbb' }}>ID: {av.avaliacaoId}</p>
                <p style={{ fontSize: 12, color: '#aaa', marginTop: 4 }}>{av.dataAvaliacao}</p>
              </div>
            )}
          </div>
        ))}
      </div>

      {/* Formulário nova avaliação */}
      <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 16 }}>Deixar avaliação</h2>
      <form onSubmit={handleAvaliar}>
        <label style={labelStyle}>Nota (1 a 5)</label>
        <select
          value={novaAvaliacao.valorNota}
          onChange={e => setNovaAvaliacao({ ...novaAvaliacao, valorNota: e.target.value })}
          style={inputStyle}
        >
          {[1, 2, 3, 4, 5].map(n => (
            <option key={n} value={n}>{n} ⭐</option>
          ))}
        </select>

        <label style={labelStyle}>Comentário</label>
        <textarea
          value={novaAvaliacao.comentario}
          onChange={e => setNovaAvaliacao({ ...novaAvaliacao, comentario: e.target.value })}
          rows={3}
          style={inputStyle}
        />

        <label style={labelStyle}>Visibilidade</label>
        <select
          value={novaAvaliacao.visibilidade}
          onChange={e => setNovaAvaliacao({ ...novaAvaliacao, visibilidade: e.target.value })}
          style={inputStyle}
        >
          <option value="PUBLICA">🌍 Pública — todos podem ver</option>
          <option value="PRIVADA">🔒 Privada — só você vê</option>
        </select>

        <button type="submit" style={{ marginTop: 16, background: '#e50914', color: '#fff', border: 'none', borderRadius: 6, padding: '10px 24px', fontWeight: 600, cursor: 'pointer' }}>
          Enviar avaliação
        </button>
      </form>
    </div>
  );
}