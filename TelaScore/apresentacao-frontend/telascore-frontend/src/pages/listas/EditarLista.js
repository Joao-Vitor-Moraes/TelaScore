import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { listaService } from '../../services/api';

const USUARIO_ID = 3;

export default function EditarLista() {
  const { id } = useParams();
  const [form, setForm] = useState(null);
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    listaService.obter(id)
      .then(lista => setForm({
        nome: lista.nome ?? '',
        descricao: lista.descricao ?? '',
        visibilidade: lista.visibilidade ?? 'PUBLICA',
        rankeada: lista.rankeada ?? false,
      }))
      .catch(() => setErro('Erro ao carregar lista.'));
  }, [id]);

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }

  async function handleDeletar() {
    if (!window.confirm('Tem certeza que deseja deletar esta lista?')) return;
    try {
      await listaService.remover(id, USUARIO_ID);
      navigate('/listas');
    } catch {
      setErro('Erro ao deletar lista. Tente novamente.');
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    try {
      await listaService.editar(id, {
        usuarioId: USUARIO_ID,
        nome: form.nome,
        descricao: form.descricao,
        visibilidade: form.visibilidade,
        rankeada: form.rankeada,
      });
      navigate(`/listas/${id}`);
    } catch {
      setErro('Erro ao salvar. Tente novamente.');
    }
  }

  if (!form && !erro) return <div style={styles.pagina}><Navbar /><p style={styles.msg}>Carregando...</p></div>;
  if (erro && !form) return <div style={styles.pagina}><Navbar /><p style={styles.msgErro}>{erro}</p></div>;

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <h2 style={styles.titulo}>EDITAR LISTA</h2>

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.colunas}>

            <div style={styles.coluna}>
              <label style={styles.label}>NOME DA LISTA</label>
              <input
                style={styles.input}
                name="nome"
                value={form.nome}
                onChange={handleChange}
                placeholder="Digite o nome da lista"
                required
              />

              <label style={styles.label}>DESCRIÇÃO</label>
              <textarea
                style={styles.textarea}
                name="descricao"
                value={form.descricao}
                onChange={handleChange}
                placeholder="Fale sobre sua lista..."
                rows={5}
              />
            </div>

            <div style={styles.coluna}>
              <label style={styles.label}>VISIBILIDADE</label>
              <select style={styles.input} name="visibilidade" value={form.visibilidade} onChange={handleChange}>
                <option value="PUBLICA">Pública</option>
                <option value="PRIVADA">Privada</option>
              </select>

              <div style={styles.toggleRow}>
                <span style={styles.label}>LISTA RANKEADA</span>
                <div
                  style={{ ...styles.toggle, backgroundColor: form.rankeada ? '#e94560' : '#444' }}
                  onClick={() => setForm(prev => ({ ...prev, rankeada: !prev.rankeada }))}
                >
                  <div style={{ ...styles.toggleBolinha, left: form.rankeada ? '22px' : '2px' }} />
                </div>
              </div>
            </div>
          </div>

          {erro && <p style={styles.msgErro}>{erro}</p>}

          <div style={styles.rodape}>
            <button type="button" style={styles.botaoDeletar} onClick={handleDeletar}>
              Deletar Lista
            </button>
            <div style={styles.rodapeDir}>
              <button type="button" style={styles.botaoCancelar} onClick={() => navigate(`/listas/${id}`)}>
                Cancelar
              </button>
              <button type="submit" style={styles.botaoSalvar}>
                Salvar
              </button>
            </div>
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
    maxWidth: '800px',
    margin: '0 auto',
    padding: '32px',
  },
  titulo: {
    fontSize: '20px',
    marginBottom: '24px',
    letterSpacing: '1px',
  },
  form: {
    backgroundColor: '#16213e',
    borderRadius: '12px',
    padding: '32px',
  },
  colunas: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '32px',
  },
  coluna: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  label: {
    fontSize: '12px',
    fontWeight: 'bold',
    color: '#aaa',
    letterSpacing: '1px',
    marginBottom: '4px',
  },
  input: {
    padding: '10px 14px',
    borderRadius: '8px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
  },
  textarea: {
    padding: '10px 14px',
    borderRadius: '8px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
    resize: 'vertical',
    fontFamily: 'inherit',
  },
  toggleRow: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  toggle: {
    position: 'relative',
    width: '44px',
    height: '24px',
    borderRadius: '12px',
    cursor: 'pointer',
    transition: 'background-color 0.2s',
  },
  toggleBolinha: {
    position: 'absolute',
    top: '2px',
    width: '20px',
    height: '20px',
    borderRadius: '50%',
    backgroundColor: 'white',
    transition: 'left 0.2s',
  },
  rodape: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '24px',
  },
  rodapeDir: {
    display: 'flex',
    gap: '12px',
  },
  botaoDeletar: {
    padding: '10px 24px',
    borderRadius: '8px',
    border: '1px solid #e94560',
    backgroundColor: 'transparent',
    color: '#e94560',
    cursor: 'pointer',
    fontSize: '14px',
  },
  botaoCancelar: {
    padding: '10px 24px',
    borderRadius: '8px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
  },
  botaoSalvar: {
    padding: '10px 24px',
    borderRadius: '8px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold',
  },
  msg: {
    color: '#aaa',
    textAlign: 'center',
    marginTop: '60px',
  },
  msgErro: {
    color: '#e94560',
    fontSize: '13px',
    marginTop: '8px',
  },
};
