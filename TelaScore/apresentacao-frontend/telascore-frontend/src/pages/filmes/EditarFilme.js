import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { filmeService } from '../../services/api';

export default function EditarFilme() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    titulo: '',
    sinopse: '',
    anoLancamento: '',
    imagemUrl: '',
    generos: '',
  });
  const [erro, setErro] = useState(null);
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    filmeService.obter(id).then(filme => {
      setForm({
        titulo: filme.titulo || '',
        sinopse: filme.sinopse || '',
        anoLancamento: filme.anoLancamento || '',
        imagemUrl: filme.imagemUrl || '',
        generos: (filme.generos || []).join(', '),
      });
    }).catch(() => setErro('Filme não encontrado.'));
  }, [id]);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      await filmeService.atualizar(id, {
        ...form,
        anoLancamento: parseInt(form.anoLancamento),
        generos: form.generos.split(',').map(g => g.trim()).filter(Boolean),
      });
      navigate(`/filmes/${id}`);
    } catch {
      setErro('Erro ao atualizar filme.');
    } finally {
      setEnviando(false);
    }
  }

  if (erro && !form.titulo) return <div style={styles.pagina}><Navbar /><p style={styles.msgErro}>{erro}</p></div>;

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <button style={styles.btnVoltar} onClick={() => navigate(`/filmes/${id}`)}>← Voltar</button>
        <h2 style={styles.titulo}>EDITAR FILME</h2>

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.grade}>
            <div style={styles.coluna}>
              <div style={styles.campo}>
                <label style={styles.label}>TÍTULO *</label>
                <input style={styles.input} name="titulo" value={form.titulo} onChange={handleChange} required />
              </div>

              <div style={styles.campo}>
                <label style={styles.label}>SINOPSE</label>
                <textarea style={styles.textarea} name="sinopse" value={form.sinopse} onChange={handleChange} rows={4} />
              </div>

              <div style={styles.campo}>
                <label style={styles.label}>GENEROS</label>
                <input style={styles.input} name="generos" value={form.generos} onChange={handleChange} placeholder="Ex: Documentario, Musical" />
              </div>

              <div style={styles.campo}>
                <label style={styles.label}>ANO *</label>
                <input style={styles.input} name="anoLancamento" type="number" value={form.anoLancamento} onChange={handleChange} required min="1888" max="2099" />
              </div>
            </div>

            <div style={styles.colunaDir}>
              <label style={styles.label}>URL DA IMAGEM</label>
              {form.imagemUrl ? (
                <img src={form.imagemUrl} alt="capa" style={styles.preview} onError={() => setForm(f => ({ ...f, imagemUrl: '' }))} />
              ) : (
                <div style={styles.imagemPlaceholder}>🎬</div>
              )}
              <input style={{ ...styles.input, marginTop: '8px', fontSize: '12px' }} name="imagemUrl" value={form.imagemUrl} onChange={handleChange} placeholder="https://..." />
            </div>
          </div>

          {erro && <p style={styles.erro}>{erro}</p>}

          <div style={styles.rodape}>
            <button type="button" style={styles.btnCancelar} onClick={() => navigate(`/filmes/${id}`)}>Cancelar</button>
            <button type="submit" style={styles.btnSalvar} disabled={enviando}>
              {enviando ? 'Salvando...' : 'Salvar alterações'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

const styles = {
  pagina: {
    minHeight: '100vh',
    backgroundColor: '#0f3460',
    color: 'white',
  },
  conteudo: {
    maxWidth: '700px',
    margin: '0 auto',
    padding: '32px',
  },
  btnVoltar: {
    background: 'none',
    border: 'none',
    color: '#aaa',
    cursor: 'pointer',
    fontSize: '14px',
    padding: 0,
    marginBottom: '16px',
    display: 'block',
  },
  titulo: {
    fontSize: '20px',
    marginBottom: '24px',
    letterSpacing: '1px',
  },
  form: {
    backgroundColor: '#16213e',
    borderRadius: '12px',
    padding: '28px',
  },
  grade: {
    display: 'grid',
    gridTemplateColumns: '1fr auto',
    gap: '24px',
    marginBottom: '16px',
  },
  coluna: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  colunaDir: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    width: '130px',
  },
  campo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
  },
  label: {
    fontSize: '11px',
    fontWeight: 'bold',
    color: '#aaa',
    letterSpacing: '1px',
  },
  input: {
    padding: '10px 12px',
    borderRadius: '6px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
    width: '100%',
    boxSizing: 'border-box',
  },
  textarea: {
    padding: '10px 12px',
    borderRadius: '6px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
    resize: 'vertical',
    fontFamily: 'inherit',
    width: '100%',
    boxSizing: 'border-box',
  },
  imagemPlaceholder: {
    width: '110px',
    height: '150px',
    borderRadius: '6px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '28px',
  },
  preview: {
    width: '110px',
    height: '150px',
    borderRadius: '6px',
    objectFit: 'cover',
  },
  rodape: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '12px',
    marginTop: '24px',
  },
  btnCancelar: {
    padding: '10px 24px',
    borderRadius: '8px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
  },
  btnSalvar: {
    padding: '10px 24px',
    borderRadius: '8px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold',
  },
  erro: {
    color: '#e94560',
    fontSize: '13px',
    marginTop: '8px',
  },
  msgErro: {
    color: '#e94560',
    textAlign: 'center',
    marginTop: '60px',
  },
};
