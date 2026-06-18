import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowLeft, FiSave, FiX } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
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
  const [enviando, setEnviando] = useState(false);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      await filmeService.cadastrar({
        ...form,
        anoLancamento: parseInt(form.anoLancamento),
        diretorId: parseInt(form.diretorId),
      });
      navigate('/filmes');
    } catch {
      setErro('Erro ao cadastrar filme. Verifique os dados e tente novamente.');
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <button className="btn-secondary" style={styles.btnVoltar} onClick={() => navigate('/filmes')}>
          <FiArrowLeft size={15} />
          Voltar
        </button>
        <h2 style={styles.titulo}>CADASTRAR FILME</h2>

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.grade}>
            <div style={styles.coluna}>
              <div style={styles.campo}>
                <label style={styles.label}>TÍTULO *</label>
                <input style={styles.input} name="titulo" value={form.titulo} onChange={handleChange} required placeholder="Ex: Interestelar" />
              </div>

              <div style={styles.campo}>
                <label style={styles.label}>SINOPSE</label>
                <textarea style={styles.textarea} name="sinopse" value={form.sinopse} onChange={handleChange} rows={4} placeholder="Descrição do filme..." />
              </div>

              <div style={styles.linhaDupla}>
                <div style={styles.campo}>
                  <label style={styles.label}>ANO *</label>
                  <input style={styles.input} name="anoLancamento" type="number" value={form.anoLancamento} onChange={handleChange} required placeholder="2024" min="1888" max="2099" />
                </div>
                <div style={styles.campo}>
                  <label style={styles.label}>ID DO DIRETOR *</label>
                  <input style={styles.input} name="diretorId" type="number" value={form.diretorId} onChange={handleChange} required placeholder="Ex: 1" />
                </div>
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
            <button type="button" className="btn-secondary" onClick={() => navigate('/filmes')}>
              <FiX size={15} />
              Cancelar
            </button>
            <button type="submit" className="btn-primary" disabled={enviando}>
              {!enviando && <FiSave size={15} />}
              {enviando ? 'Cadastrando...' : 'Cadastrar'}
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
    marginBottom: '16px',
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
  linhaDupla: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '12px',
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
};
