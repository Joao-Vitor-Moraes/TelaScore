import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { solicitacaoService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function SolicitarFilme() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const [form, setForm] = useState({
    nome: '', pais: '', ano: '', fotoUrl: '', descricao: '',
  });
  const [erro, setErro] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const navigate = useNavigate();

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      await solicitacaoService.criar({
        solicitanteId: USUARIO_ID,
        tituloSugerido: form.nome,
        justificativa: form.descricao || null,
        pais: form.pais || null,
        ano: form.ano ? parseInt(form.ano) : null,
        fotoUrl: form.fotoUrl || null,
      });
      navigate('/solicitacoes', { state: { sucesso: form.nome } });
    } catch {
      setErro('Erro ao enviar solicitação. Tente novamente.');
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <button style={styles.voltar} onClick={() => navigate('/solicitacoes')}>←</button>

        <div style={styles.stepper}>
          {[0, 1, 2, 3].map(i => (
            <div key={i} style={{ display: 'flex', alignItems: 'center' }}>
              <div style={{ ...styles.ponto, backgroundColor: i === 0 ? 'white' : '#2a2a4a' }} />
              {i < 3 && <div style={styles.linha} />}
            </div>
          ))}
        </div>

        <div style={styles.card}>
          <p style={styles.cardTitulo}>SOLICITE UM FILME PARA O SITE</p>

          <form onSubmit={handleSubmit}>
            <div style={styles.grade}>
              <div style={styles.colunaEsq}>
                <div style={styles.campo}>
                  <label style={styles.label}>NOME</label>
                  <input style={styles.input} name="nome" value={form.nome}
                    onChange={handleChange} placeholder="Ex: Interstellar" required />
                </div>

                <div style={styles.campo}>
                  <label style={styles.label}>PAÍS</label>
                  <input style={styles.input} name="pais" value={form.pais}
                    onChange={handleChange} placeholder="Ex: EUA" />
                </div>

                <div style={styles.campo}>
                  <label style={styles.label}>ANO</label>
                  <input style={styles.input} name="ano" value={form.ano}
                    onChange={handleChange} placeholder="Ex: 2014"
                    type="number" min="1888" max="2099" />
                </div>
              </div>

              <div style={styles.colunaDireita}>
                <label style={styles.label}>FOTO</label>
                {form.fotoUrl ? (
                  <img src={form.fotoUrl} alt="capa" style={styles.preview}
                    onError={() => setForm(prev => ({ ...prev, fotoUrl: '' }))} />
                ) : (
                  <div style={styles.fotoPlaceholder}>✕</div>
                )}
                <input style={styles.fotoInput}
                  name="fotoUrl" value={form.fotoUrl} onChange={handleChange}
                  placeholder="URL da imagem" />
              </div>
            </div>

            <div style={styles.campo}>
              <label style={styles.label}>DESCRIÇÃO</label>
              <textarea style={styles.textarea} name="descricao" value={form.descricao}
                onChange={handleChange} placeholder="Fale sobre o filme..." rows={4} />
            </div>

            {erro && <p style={styles.erro}>{erro}</p>}

            <div style={styles.rodape}>
              <button type="submit" style={styles.btnEnviar} disabled={enviando}>
                {enviando ? 'Enviando...' : 'ENVIAR'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}

const styles = {
  pagina: { minHeight: '100vh', backgroundColor: '#0f3460', color: 'white' },
  conteudo: { maxWidth: '680px', margin: '0 auto', padding: '24px 32px' },
  voltar: {
    background: 'none', border: 'none', color: 'white', cursor: 'pointer',
    fontSize: '20px', padding: 0, marginBottom: '12px',
  },
  stepper: { display: 'flex', alignItems: 'center', marginBottom: '20px' },
  ponto: { width: '12px', height: '12px', borderRadius: '50%' },
  linha: { width: '32px', height: '2px', backgroundColor: '#2a2a4a' },
  card: { backgroundColor: '#16213e', borderRadius: '12px', padding: '28px' },
  cardTitulo: {
    fontSize: '12px', fontWeight: 'bold', letterSpacing: '1px',
    color: '#aaa', marginBottom: '20px', marginTop: 0,
  },
  grade: {
    display: 'grid',
    gridTemplateColumns: 'minmax(0, 1fr) 132px',
    gap: '24px',
    alignItems: 'start',
    marginBottom: '16px',
  },
  colunaEsq: { display: 'flex', flexDirection: 'column', gap: '14px' },
  colunaDireita: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'stretch',
    minWidth: 0,
  },
  campo: { display: 'flex', flexDirection: 'column', gap: '6px' },
  label: { fontSize: '11px', fontWeight: 'bold', color: '#aaa', letterSpacing: '1px' },
  input: {
    padding: '8px 12px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none',
    width: '100%', boxSizing: 'border-box',
  },
  fotoInput: {
    padding: '8px 10px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '12px', outline: 'none',
    width: '100%', minWidth: 0, boxSizing: 'border-box', marginTop: '8px',
  },
  textarea: {
    padding: '8px 12px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none',
    resize: 'vertical', fontFamily: 'inherit', width: '100%', boxSizing: 'border-box',
  },
  fotoPlaceholder: {
    width: '100px', height: '120px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', display: 'flex', alignItems: 'center',
    justifyContent: 'center', fontSize: '24px', color: '#444', alignSelf: 'center',
  },
  preview: { width: '100px', height: '120px', borderRadius: '6px', objectFit: 'cover', alignSelf: 'center' },
  rodape: { display: 'flex', justifyContent: 'flex-end', marginTop: '20px' },
  btnEnviar: {
    minHeight: '40px',
    padding: '10px 32px',
    borderRadius: '999px',
    border: '1px solid rgba(255,255,255,0.14)',
    backgroundColor: '#ff2f48',
    color: '#fff',
    cursor: 'pointer',
    fontSize: '13px',
    fontWeight: '800',
    boxShadow: '0 12px 24px rgba(255,47,72,0.25)',
    textShadow: '0 1px 1px rgba(0,0,0,0.28)',
  },
  erro: { color: '#e94560', fontSize: '13px', marginTop: '8px' },
};
