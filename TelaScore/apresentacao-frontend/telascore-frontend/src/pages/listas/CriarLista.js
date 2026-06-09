import { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { listaService } from '../../services/api';

const USUARIO_ID = 2;

export default function CriarLista() {
  const [searchParams] = useSearchParams();
  const tipo = searchParams.get('tipo') || 'NORMAL';

  const [form, setForm] = useState({
    nome: '',
    descricao: '',
    visibilidade: 'PUBLICA',
    colaborativa: false,
    rankeada: false,
  });
  const [capa, setCapa] = useState(null);
  const [erro, setErro] = useState(null);
  const navigate = useNavigate();

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  }

  function handleCapa(e) {
    const file = e.target.files[0];
    if (file) setCapa(URL.createObjectURL(file));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setErro(null);
    try {
      await listaService.criar({
        criadorId: USUARIO_ID,
        nome: form.nome,
        descricao: form.descricao,
        tipo: tipo,
        visibilidade: form.visibilidade,
        rankeada: form.rankeada,
      });
      navigate(tipo === 'WATCHLIST' ? '/watchlist' : '/listas');
    } catch {
      setErro('Erro ao criar lista. Tente novamente.');
    }
  }

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>
        <h2 style={styles.titulo}>{tipo === 'WATCHLIST' ? 'CRIAR WATCHLIST' : 'CRIAR LISTA'}</h2>

        <form onSubmit={handleSubmit} style={styles.form}>
          <div style={styles.colunas}>

            {/* Coluna esquerda */}
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
                rows={4}
              />

              <label style={styles.label}>CAPA (OPCIONAL)</label>
              <label style={styles.capaBox}>
                {capa
                  ? <img src={capa} alt="capa" style={styles.capaImg} />
                  : <span style={styles.capaTexto}>+ Adicionar imagem</span>
                }
                <input type="file" accept="image/*" onChange={handleCapa} style={{ display: 'none' }} />
              </label>
            </div>

            {/* Coluna direita */}
            <div style={styles.coluna}>
              <label style={styles.label}>VISIBILIDADE</label>
              <select style={styles.input} name="visibilidade" value={form.visibilidade} onChange={handleChange}>
                <option value="PUBLICA">Pública</option>
                <option value="PRIVADA">Privada</option>
              </select>

              <div style={styles.toggleRow}>
                <span style={styles.label}>PERMITIR COLABORAÇÃO</span>
                <div
                  style={{ ...styles.toggle, backgroundColor: form.colaborativa ? '#e94560' : '#444' }}
                  onClick={() => setForm(prev => ({ ...prev, colaborativa: !prev.colaborativa }))}
                >
                  <div style={{ ...styles.toggleBolinha, left: form.colaborativa ? '22px' : '2px' }} />
                </div>
              </div>

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

          {erro && <p style={styles.erro}>{erro}</p>}

          <div style={styles.rodape}>
            <button type="button" style={styles.botaoCancelar} onClick={() => navigate(tipo === 'WATCHLIST' ? '/watchlist' : '/listas')}>
              Cancelar
            </button>
            <button type="submit" style={styles.botaoSalvar}>
              Salvar
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
  capaBox: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    height: '100px',
    borderRadius: '8px',
    border: '2px dashed #2a2a4a',
    cursor: 'pointer',
    overflow: 'hidden',
  },
  capaTexto: {
    color: '#aaa',
    fontSize: '14px',
  },
  capaImg: {
    width: '100%',
    height: '100%',
    objectFit: 'cover',
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
    justifyContent: 'flex-end',
    gap: '12px',
    marginTop: '24px',
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
  erro: {
    color: '#e94560',
    fontSize: '13px',
    marginTop: '8px',
  },
};
