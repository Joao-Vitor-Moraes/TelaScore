import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { solicitacaoService } from '../../services/api';

const USUARIO_ID = 2;

const GENEROS = [
  'Ação', 'Aventura', 'Animação', 'Biografia', 'Comédia', 'Crime',
  'Documentário', 'Drama', 'Fantasia', 'Ficção Científica', 'Musical',
  'Romance', 'Suspense', 'Terror',
];

export default function EditarSolicitacao() {
  const { id } = useParams();
  const [form, setForm] = useState(null);
  const [feedbackAdmin, setFeedbackAdmin] = useState('');
  const [erro, setErro] = useState(null);
  const [enviando, setEnviando] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    solicitacaoService.obter(id)
      .then(s => {
        setFeedbackAdmin(s.feedbackAdmin ?? '');
        setForm({
          nome: s.tituloSugerido ?? '',
          pais: s.pais ?? '',
          ano: s.ano ? String(s.ano) : '',
          genero: s.genero ?? '',
          fotoUrl: s.fotoUrl ?? '',
          descricao: s.justificativa ?? '',
        });
      })
      .catch(() => setErro('Erro ao carregar solicitação.'));
  }, [id]);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    setEnviando(true);
    try {
      await solicitacaoService.editar(id, {
        solicitanteId: USUARIO_ID,
        tituloSugerido: form.nome,
        justificativa: form.descricao || null,
        pais: form.pais || null,
        ano: form.ano ? parseInt(form.ano) : null,
        genero: form.genero || null,
        fotoUrl: form.fotoUrl || null,
      });
      navigate('/solicitacoes');
    } catch {
      setErro('Erro ao salvar. Tente novamente.');
    } finally {
      setEnviando(false);
    }
  }

  if (!form && !erro) return <div style={styles.pagina}><Navbar /><p style={styles.msg}>Carregando...</p></div>;
  if (erro && !form) return <div style={styles.pagina}><Navbar /><p style={styles.msgErro}>{erro}</p></div>;

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <button style={styles.voltar} onClick={() => navigate('/solicitacoes')}>←</button>

        {feedbackAdmin && (
          <div style={styles.feedbackBox}>
            <span style={styles.feedbackLabel}>FEEDBACK DO ADMIN</span>
            <p style={styles.feedbackTexto}>{feedbackAdmin}</p>
          </div>
        )}

        <div style={styles.card}>
          <p style={styles.cardTitulo}>EDITAR SOLICITAÇÃO</p>

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

                <div style={styles.linhaDouble}>
                  <div style={styles.campo}>
                    <label style={styles.label}>ANO</label>
                    <input style={styles.input} name="ano" value={form.ano}
                      onChange={handleChange} placeholder="Ex: 2014"
                      type="number" min="1888" max="2099" />
                  </div>
                  <div style={styles.campo}>
                    <label style={styles.label}>GÊNERO</label>
                    <select style={styles.input} name="genero" value={form.genero} onChange={handleChange}>
                      <option value="">Selecione</option>
                      {GENEROS.map(g => <option key={g} value={g}>{g}</option>)}
                    </select>
                  </div>
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
                <input style={{ ...styles.input, marginTop: '8px', fontSize: '12px' }}
                  name="fotoUrl" value={form.fotoUrl} onChange={handleChange}
                  placeholder="URL da imagem" />
              </div>
            </div>

            <div style={styles.campo}>
              <label style={styles.label}>DESCRIÇÃO</label>
              <textarea style={styles.textarea} name="descricao" value={form.descricao}
                onChange={handleChange} placeholder="Fale sobre o filme..." rows={4} />
            </div>

            {erro && <p style={styles.msgErro}>{erro}</p>}

            <div style={styles.rodape}>
              <button type="button" style={styles.btnCancelar} onClick={() => navigate('/solicitacoes')}>
                Cancelar
              </button>
              <button type="submit" style={styles.btnEnviar} disabled={enviando}>
                {enviando ? 'Salvando...' : 'REENVIAR'}
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
  voltar: { background: 'none', border: 'none', color: 'white', cursor: 'pointer', fontSize: '20px', padding: 0, marginBottom: '16px' },
  feedbackBox: {
    backgroundColor: '#f97316' + '11', border: '1px solid #f97316',
    borderRadius: '8px', padding: '14px 16px', marginBottom: '20px',
  },
  feedbackLabel: { fontSize: '11px', fontWeight: 'bold', color: '#f97316', letterSpacing: '1px', display: 'block', marginBottom: '6px' },
  feedbackTexto: { color: '#ddd', fontSize: '14px', margin: 0, lineHeight: '1.5' },
  card: { backgroundColor: '#16213e', borderRadius: '12px', padding: '28px' },
  cardTitulo: { fontSize: '12px', fontWeight: 'bold', letterSpacing: '1px', color: '#aaa', marginBottom: '20px', marginTop: 0 },
  grade: { display: 'grid', gridTemplateColumns: '1fr auto', gap: '24px', marginBottom: '16px' },
  colunaEsq: { display: 'flex', flexDirection: 'column', gap: '14px' },
  colunaDireita: { display: 'flex', flexDirection: 'column', alignItems: 'center', width: '120px' },
  linhaDouble: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' },
  campo: { display: 'flex', flexDirection: 'column', gap: '6px' },
  label: { fontSize: '11px', fontWeight: 'bold', color: '#aaa', letterSpacing: '1px' },
  input: { padding: '8px 12px', borderRadius: '6px', border: '1px solid #2a2a4a', backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none' },
  textarea: { padding: '8px 12px', borderRadius: '6px', border: '1px solid #2a2a4a', backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none', resize: 'vertical', fontFamily: 'inherit', width: '100%', boxSizing: 'border-box' },
  fotoPlaceholder: { width: '100px', height: '120px', borderRadius: '6px', border: '1px solid #2a2a4a', backgroundColor: '#0f3460', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '24px', color: '#444' },
  preview: { width: '100px', height: '120px', borderRadius: '6px', objectFit: 'cover' },
  rodape: { display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '20px' },
  btnCancelar: { padding: '10px 24px', borderRadius: '6px', border: '1px solid #aaa', backgroundColor: 'transparent', color: 'white', cursor: 'pointer', fontSize: '13px' },
  btnEnviar: { padding: '10px 32px', borderRadius: '6px', border: 'none', backgroundColor: '#e94560', color: 'white', cursor: 'pointer', fontSize: '13px', fontWeight: 'bold' },
  msg: { color: '#aaa', textAlign: 'center', marginTop: '60px' },
  msgErro: { color: '#e94560', fontSize: '13px', marginTop: '8px' },
};
