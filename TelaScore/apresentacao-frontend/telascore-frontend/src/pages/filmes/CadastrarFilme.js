import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { filmeService } from '../../services/api';

export default function CadastrarFilme() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    titulo: '',
    sinopse: '',
    anoLancamento: '',
    diretorId: '',
    imagemUrl: '',
  });
  const [erro, setErro] = useState(null);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    try {
      await filmeService.cadastrar({
        ...form,
        anoLancamento: parseInt(form.anoLancamento),
        diretorId: parseInt(form.diretorId),
      });
      navigate('/filmes');
    } catch {
      setErro('Erro ao cadastrar filme. Verifique os dados e tente novamente.');
    }
  }

  const inputStyle = {
    width: '100%', padding: '10px 12px', borderRadius: 6,
    border: '1px solid #ccc', fontSize: 15, marginTop: 4, boxSizing: 'border-box'
  };
  const labelStyle = { fontWeight: 600, fontSize: 14, display: 'block', marginTop: 16 };

  return (
    <div style={{ maxWidth: 520, margin: '40px auto', padding: '0 20px' }}>
      <button onClick={() => navigate('/filmes')} style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#888', marginBottom: 16 }}>
        ← Voltar
      </button>
      <h1 style={{ fontSize: 24, fontWeight: 700, marginBottom: 24 }}>Cadastrar filme</h1>

      {erro && <p style={{ color: 'red', marginBottom: 12 }}>{erro}</p>}

      <form onSubmit={handleSubmit}>
        <label style={labelStyle}>Título *</label>
        <input name="titulo" value={form.titulo} onChange={handleChange} required style={inputStyle} />

        <label style={labelStyle}>Sinopse</label>
        <textarea name="sinopse" value={form.sinopse} onChange={handleChange} rows={3} style={inputStyle} />

        <label style={labelStyle}>Ano de lançamento *</label>
        <input name="anoLancamento" type="number" value={form.anoLancamento} onChange={handleChange} required style={inputStyle} />

        <label style={labelStyle}>ID do diretor *</label>
        <input name="diretorId" type="number" value={form.diretorId} onChange={handleChange} required style={inputStyle} />

        <label style={labelStyle}>URL da imagem</label>
        <input name="imagemUrl" value={form.imagemUrl} onChange={handleChange} style={inputStyle} />

        <button type="submit" style={{ marginTop: 24, width: '100%', background: '#e50914', color: '#fff', border: 'none', borderRadius: 6, padding: '12px 0', fontSize: 16, fontWeight: 600, cursor: 'pointer' }}>
          Cadastrar
        </button>
      </form>
    </div>
  );
}