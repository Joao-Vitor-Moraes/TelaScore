import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { eventoService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function CriarEvento() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const navigate = useNavigate();

  const [form, setForm] = useState({ titulo: '', descricao: '', data: '', hora: '' });
  const [erro, setErro] = useState(null);
  const [enviando, setEnviando] = useState(false);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);

    if (!form.titulo.trim()) {
      setErro('Informe o título do evento.');
      return;
    }
    if (!form.data || !form.hora) {
      setErro('Informe a data e a hora do evento.');
      return;
    }

    const dataHora = `${form.data}T${form.hora}:00`;
    if (new Date(dataHora) <= new Date()) {
      setErro('A data e hora do evento devem ser no futuro.');
      return;
    }

    setEnviando(true);
    try {
      await eventoService.agendar({
        criadorId: USUARIO_ID,
        titulo: form.titulo.trim(),
        descricao: form.descricao.trim() || null,
        dataHora,
      });
      navigate('/eventos', { state: { sucesso: form.titulo.trim() } });
    } catch {
      setErro('Erro ao agendar evento. Verifique os dados e tente novamente.');
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <div style={styles.cabecalho}>
          <button style={styles.btnVoltar} onClick={() => navigate('/eventos')}>
            <FiArrowLeft size={20} />
          </button>
          <h2 style={styles.titulo}>Novo Evento</h2>
        </div>

        <div style={styles.card}>
          <p style={styles.cardTitulo}>AGENDE UM NOVO EVENTO</p>

          <form onSubmit={handleSubmit}>
            <div style={styles.campo}>
              <label style={styles.label}>TÍTULO</label>
              <input style={styles.input} name="titulo" value={form.titulo}
                onChange={handleChange} placeholder="Ex: Maratona Senhor dos Anéis" required />
            </div>

            <div style={styles.campo}>
              <label style={styles.label}>DESCRIÇÃO</label>
              <textarea style={styles.textarea} name="descricao" value={form.descricao}
                onChange={handleChange} placeholder="Detalhes do evento..." rows={4} />
            </div>

            <div style={styles.linhaDouble}>
              <div style={styles.campo}>
                <label style={styles.label}>DATA</label>
                <input style={styles.input} type="date" name="data"
                  value={form.data} onChange={handleChange} required />
              </div>
              <div style={styles.campo}>
                <label style={styles.label}>HORA</label>
                <input style={styles.input} type="time" name="hora"
                  value={form.hora} onChange={handleChange} required />
              </div>
            </div>

            {erro && <p style={styles.erro}>{erro}</p>}

            <div style={styles.rodape}>
              <button type="submit" style={styles.btnEnviar} disabled={enviando}>
                {enviando ? 'Agendando...' : 'AGENDAR'}
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
  conteudo: { maxWidth: '560px', margin: '0 auto', padding: '24px 32px' },
  cabecalho: { display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '20px' },
  btnVoltar: { background: 'none', border: 'none', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center' },
  titulo: { margin: 0, fontSize: '20px' },
  card: { backgroundColor: '#16213e', borderRadius: '12px', padding: '28px' },
  cardTitulo: { fontSize: '12px', fontWeight: 'bold', letterSpacing: '1px', color: '#aaa', marginBottom: '20px', marginTop: 0 },
  campo: { display: 'flex', flexDirection: 'column', gap: '6px', marginBottom: '16px' },
  linhaDouble: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' },
  label: { fontSize: '11px', fontWeight: 'bold', color: '#aaa', letterSpacing: '1px' },
  input: {
    padding: '8px 12px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none',
    colorScheme: 'dark',
  },
  textarea: {
    padding: '8px 12px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none',
    resize: 'vertical', fontFamily: 'inherit', width: '100%', boxSizing: 'border-box',
  },
  rodape: { display: 'flex', justifyContent: 'flex-end', marginTop: '20px' },
  btnEnviar: {
    padding: '10px 32px', borderRadius: '6px', border: 'none',
    backgroundColor: '#e94560', color: 'white', cursor: 'pointer', fontSize: '13px', fontWeight: 'bold',
  },
  erro: { color: '#e94560', fontSize: '13px', marginTop: '8px' },
};
