import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { filmeService } from '../../services/api';

export default function EditarFilme() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    titulo: '',
    sinopse: '',
    anoLancamento: '',
  });
  const [erro, setErro] = useState(null);

  useEffect(() => {
    filmeService.obter(id).then(filme => {
      setForm({
        titulo: filme.titulo || '',
        sinopse: filme.sinopse || '',
        anoLancamento: filme.anoLancamento || '',
      });
    }).catch(() => setErro('Filme não encontrado.'));
  }, [id]);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    try {
      await filmeService.atualizar(id, {
        ...form,
        anoLancamento: parseInt(form.anoLancamento),
      });
      navigate(`/filmes/${id}`);
    } catch {
      setErro('Erro ao atualizar filme.');
    }
  }

  const inputStyle = {
    width: '100%', padding: '10px 12px', borderRadius: 6,
    border: '1px solid #ccc', fontSize: 15, marginTop: 4, boxSizing: 'border-box'
  };
  const labelStyle = { fontWeight: 600, fontSize: 14, display: 'block', marginTop: 16 };

  return (
    <div style={{ maxWidth: 520, margin: '40px auto', padding: '0 20px' }}>
      <button onClick={() => navigate(`/filmes/${id}`)} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#888', marginBottom: 16 }}>
        ← Voltar
      </button>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>Editar filme</h1>

      {erro && <p style={{ color: 'red', marginBottom: 12 }}>{erro}</p>}

      <form onSubmit={handleSubmit}>
        <label style={labelStyle}>Título *</label>
        <input name="titulo" value={form.titulo} onChange={handleChange} required style={inputStyle} />

        <label style={labelStyle}>Sinopse</label>
        <textarea name="sinopse" value={form.sinopse} onChange={handleChange} rows={3} style={inputStyle} />

        <label style={labelStyle}>Ano de lançamento *</label>
        <input name="anoLancamento" type="number" value={form.anoLancamento} onChange={handleChange} required style={inputStyle} />

        <button type="submit" style={{ marginTop: 24, width: '100%', background: '#e50914', color: '#fff', border: 'none', borderRadius: 6, padding: '12px 0', fontSize: 16, fontWeight: 600, cursor: 'pointer' }}>
          Salvar alterações
        </button>
      </form>
    </div>
  );
}