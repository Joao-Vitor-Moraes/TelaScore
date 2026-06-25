import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiArrowLeft, FiGlobe, FiUsers, FiLock, FiCheck } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { eventoService, comunidadeService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

const VISIBILIDADES = [
  { valor: 'PUBLICO', label: 'Público', icone: FiGlobe, dica: 'Qualquer pessoa pode ver e confirmar' },
  { valor: 'AMIGOS', label: 'Amigos', icone: FiUsers, dica: 'Só seus amigos (quem se segue mutuamente)' },
  { valor: 'PRIVADO', label: 'Privado', icone: FiLock, dica: 'Só quem você convidar abaixo' },
];

export default function CriarEvento() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const navigate = useNavigate();

  const [form, setForm] = useState({ titulo: '', descricao: '', data: '', hora: '' });
  const [visibilidade, setVisibilidade] = useState('PUBLICO');
  const [comunidades, setComunidades] = useState([]);
  const [amigos, setAmigos] = useState([]);
  const [comunidadesSel, setComunidadesSel] = useState(() => new Set());
  const [amigosSel, setAmigosSel] = useState(() => new Set());
  const [erro, setErro] = useState(null);
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    comunidadeService.buscarComunidadesDoUsuario(USUARIO_ID).then(setComunidades).catch(() => setComunidades([]));
    eventoService.amigos(USUARIO_ID).then(setAmigos).catch(() => setAmigos([]));
  }, [USUARIO_ID]);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }

  function alternar(setFn, set, id) {
    const novo = new Set(set);
    if (novo.has(id)) novo.delete(id); else novo.add(id);
    setFn(novo);
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);

    if (!form.titulo.trim()) { setErro('Informe o título do evento.'); return; }
    if (!form.data || !form.hora) { setErro('Informe a data e a hora do evento.'); return; }

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
        visibilidade,
        comunidadesConvidadas: [...comunidadesSel],
        convidados: [...amigosSel],
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
          <form onSubmit={handleSubmit}>
            <div style={styles.campo}>
              <label style={styles.label}>TÍTULO</label>
              <input style={styles.input} name="titulo" value={form.titulo}
                onChange={handleChange} placeholder="Ex: Maratona Senhor dos Anéis" required />
            </div>

            <div style={styles.campo}>
              <label style={styles.label}>DESCRIÇÃO</label>
              <textarea style={styles.textarea} name="descricao" value={form.descricao}
                onChange={handleChange} placeholder="Detalhes do evento..." rows={3} />
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

            {/* Visibilidade */}
            <div style={{ ...styles.campo, marginTop: '6px' }}>
              <label style={styles.label}>QUEM PODE VER</label>
              <div style={styles.visRow}>
                {VISIBILIDADES.map(v => {
                  const Icone = v.icone;
                  const ativo = visibilidade === v.valor;
                  return (
                    <button type="button" key={v.valor}
                      style={{ ...styles.visBtn, ...(ativo ? styles.visBtnAtivo : {}) }}
                      onClick={() => setVisibilidade(v.valor)} title={v.dica}>
                      <Icone size={15} /> {v.label}
                    </button>
                  );
                })}
              </div>
              <span style={styles.dica}>{VISIBILIDADES.find(v => v.valor === visibilidade)?.dica}</span>
            </div>

            {/* Convidar comunidades */}
            <div style={styles.campo}>
              <label style={styles.label}>CONVIDAR COMUNIDADES</label>
              {comunidades.length === 0 ? (
                <span style={styles.vazio}>Você não participa de nenhuma comunidade.</span>
              ) : (
                <div style={styles.chips}>
                  {comunidades.map(c => {
                    const sel = comunidadesSel.has(c.id);
                    return (
                      <button type="button" key={c.id}
                        style={{ ...styles.chip, ...(sel ? styles.chipAtivo : {}) }}
                        onClick={() => alternar(setComunidadesSel, comunidadesSel, c.id)}>
                        {sel && <FiCheck size={13} />} {c.nome}
                      </button>
                    );
                  })}
                </div>
              )}
            </div>

            {/* Convidar amigos */}
            <div style={styles.campo}>
              <label style={styles.label}>CONVIDAR AMIGOS</label>
              {amigos.length === 0 ? (
                <span style={styles.vazio}>Você ainda não tem amigos (amizade mútua) para convidar.</span>
              ) : (
                <div style={styles.chips}>
                  {amigos.map(a => {
                    const sel = amigosSel.has(a.id);
                    return (
                      <button type="button" key={a.id}
                        style={{ ...styles.chip, ...(sel ? styles.chipAtivo : {}) }}
                        onClick={() => alternar(setAmigosSel, amigosSel, a.id)}>
                        {sel && <FiCheck size={13} />} {a.nome}
                      </button>
                    );
                  })}
                </div>
              )}
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
  conteudo: { maxWidth: '600px', margin: '0 auto', padding: '24px 32px' },
  cabecalho: { display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '20px' },
  btnVoltar: { background: 'none', border: 'none', color: 'white', cursor: 'pointer', display: 'flex', alignItems: 'center' },
  titulo: { margin: 0, fontSize: '20px' },
  card: { backgroundColor: '#16213e', borderRadius: '12px', padding: '28px' },
  campo: { display: 'flex', flexDirection: 'column', gap: '6px', marginBottom: '16px' },
  linhaDouble: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' },
  label: { fontSize: '11px', fontWeight: 'bold', color: '#aaa', letterSpacing: '1px' },
  input: {
    padding: '8px 12px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none', colorScheme: 'dark',
  },
  textarea: {
    padding: '8px 12px', borderRadius: '6px', border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460', color: 'white', fontSize: '13px', outline: 'none',
    resize: 'vertical', fontFamily: 'inherit', width: '100%', boxSizing: 'border-box',
  },
  visRow: { display: 'flex', gap: '8px', flexWrap: 'wrap' },
  visBtn: {
    display: 'flex', alignItems: 'center', gap: '6px', padding: '8px 14px', borderRadius: '8px',
    border: '1px solid #2a2a4a', backgroundColor: '#0f3460', color: '#aaa', cursor: 'pointer',
    fontSize: '13px', fontWeight: 'bold',
  },
  visBtnAtivo: { backgroundColor: '#c8102e', color: '#fff', borderColor: '#c8102e' },
  dica: { fontSize: '12px', color: '#8a8aa0', marginTop: '2px' },
  chips: { display: 'flex', gap: '8px', flexWrap: 'wrap' },
  chip: {
    display: 'flex', alignItems: 'center', gap: '5px', padding: '7px 12px', borderRadius: '16px',
    border: '1px solid #2a2a4a', backgroundColor: '#0f3460', color: '#ccc', cursor: 'pointer', fontSize: '13px',
  },
  chipAtivo: { backgroundColor: '#c8102e', color: '#fff', borderColor: '#c8102e' },
  vazio: { fontSize: '13px', color: '#8a8aa0' },
  rodape: { display: 'flex', justifyContent: 'flex-end', marginTop: '12px' },
  btnEnviar: {
    padding: '10px 32px', borderRadius: '6px', border: 'none',
    backgroundColor: '#c8102e', color: '#fff', cursor: 'pointer', fontSize: '13px', fontWeight: 'bold',
  },
  erro: { color: '#ff5963', fontSize: '13px', marginTop: '8px' },
};
