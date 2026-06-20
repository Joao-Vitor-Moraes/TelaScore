import { useEffect, useState } from 'react';
import { FiFileText, FiFlag, FiHash, FiLink, FiSend, FiTag, FiTarget, FiX } from 'react-icons/fi';
import { denunciaService } from '../services/api';

const TIPOS_ALVO = ['USUARIO', 'MENSAGEM', 'COMUNIDADE', 'AVALIACAO'];
const MOTIVOS = ['OFENSIVO', 'SPAM', 'ASSEDIO', 'CONTEUDO_INADEQUADO', 'OUTRO'];

const formPadrao = {
  tipoAlvo: 'MENSAGEM',
  alvoId: '',
  motivo: 'OFENSIVO',
  linkOcorrencia: '',
  descricao: '',
};

export default function DenunciaFormModal({
  aberto,
  denuncianteId,
  valoresIniciais,
  onFechar,
  onRegistrada,
}) {
  const [form, setForm] = useState(formPadrao);
  const [erro, setErro] = useState(null);
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    if (!aberto) return;
    setErro(null);
    setForm({
      ...formPadrao,
      ...(valoresIniciais || {}),
      alvoId: valoresIniciais?.alvoId ? String(valoresIniciais.alvoId) : '',
    });
  }, [aberto, valoresIniciais]);

  if (!aberto) return null;

  function atualizarCampo(campo, valor) {
    setForm(atual => ({ ...atual, [campo]: valor }));
  }

  async function enviar(e) {
    e.preventDefault();
    setErro(null);

    const alvoId = Number.parseInt(form.alvoId, 10);
    if (!Number.isInteger(alvoId) || alvoId <= 0) {
      setErro('Informe um ID de alvo valido.');
      return;
    }

    if (!form.descricao.trim()) {
      setErro('Informe uma descricao para a denuncia.');
      return;
    }

    setEnviando(true);

    try {
      const denuncia = await denunciaService.registrar({
        denuncianteId,
        alvoId,
        tipoAlvo: form.tipoAlvo,
        motivo: form.motivo,
        descricao: form.descricao.trim(),
        linkOcorrencia: form.linkOcorrencia.trim() || null,
      });

      onRegistrada?.(denuncia);
      onFechar?.();
    } catch (erro) {
      setErro(erro?.message || 'Erro ao registrar denuncia.');
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div style={styles.overlay} role="dialog" aria-modal="true">
      <div style={styles.modal}>
        <div style={styles.cabecalho}>
          <div style={styles.tituloBox}>
            <FiFlag size={18} color="#e94560" />
            <h3 style={styles.titulo}>Nova denúncia</h3>
          </div>
          <button style={styles.btnFechar} onClick={onFechar} disabled={enviando} title="Fechar">
            <FiX size={18} />
          </button>
        </div>

        <form onSubmit={enviar} style={styles.form}>
          <div style={styles.grid}>
            <label style={styles.campo}>
              <span style={styles.labelIcone}><FiTarget size={13} /> Tipo da denúncia</span>
              <select
                style={styles.input}
                value={form.tipoAlvo}
                onChange={e => atualizarCampo('tipoAlvo', e.target.value)}
              >
                {TIPOS_ALVO.map(tipo => (
                  <option key={tipo} value={tipo}>{rotuloTipo(tipo)}</option>
                ))}
              </select>
            </label>

            <label style={styles.campo}>
              <span style={styles.labelIcone}><FiHash size={13} /> ID do alvo</span>
              <input
                style={styles.input}
                type="number"
                min="1"
                value={form.alvoId}
                onChange={e => atualizarCampo('alvoId', e.target.value)}
                required
              />
            </label>

            <label style={styles.campo}>
              <span style={styles.labelIcone}><FiTag size={13} /> Motivo</span>
              <select
                style={styles.input}
                value={form.motivo}
                onChange={e => atualizarCampo('motivo', e.target.value)}
              >
                {MOTIVOS.map(motivo => (
                  <option key={motivo} value={motivo}>{rotuloMotivo(motivo)}</option>
                ))}
              </select>
            </label>

            <label style={styles.campo}>
              <span style={styles.labelIcone}><FiLink size={13} /> Link da ocorrência</span>
              <input
                style={styles.input}
                value={form.linkOcorrencia}
                onChange={e => atualizarCampo('linkOcorrencia', e.target.value)}
                placeholder="https://..."
              />
            </label>

            <label style={styles.campoLargo}>
              <span style={styles.labelIcone}><FiFileText size={13} /> Descrição</span>
              <textarea
                style={{ ...styles.input, ...styles.textarea }}
                value={form.descricao}
                onChange={e => atualizarCampo('descricao', e.target.value)}
                rows={4}
                required
              />
            </label>
          </div>

          {erro && <p style={styles.erro}>{erro}</p>}

          <div style={styles.acoes}>
            <button type="button" className="btn-secondary" onClick={onFechar} disabled={enviando}>
              <FiX size={14} />
              Cancelar
            </button>
            <button type="submit" className="btn-primary" disabled={enviando}>
              <FiSend size={14} />
              {enviando ? 'Enviando...' : 'Enviar'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function rotuloTipo(tipo) {
  const rotulos = {
    USUARIO: 'Usuário',
    MENSAGEM: 'Mensagem',
    COMUNIDADE: 'Comunidade',
    AVALIACAO: 'Avaliação',
  };
  return rotulos[tipo] || tipo;
}

function rotuloMotivo(motivo) {
  const rotulos = {
    OFENSIVO: 'Ofensivo',
    SPAM: 'Spam',
    ASSEDIO: 'Assédio',
    CONTEUDO_INADEQUADO: 'Conteúdo inadequado',
    OUTRO: 'Outro',
  };
  return rotulos[motivo] || motivo;
}

const styles = {
  overlay: {
    position: 'fixed',
    inset: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.58)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px',
    zIndex: 1000,
  },
  modal: {
    width: '100%',
    maxWidth: '620px',
    backgroundColor: '#16213e',
    border: '1px solid #2a2a4a',
    borderRadius: '8px',
    padding: '20px',
    boxShadow: '0 20px 45px rgba(0, 0, 0, 0.35)',
  },
  cabecalho: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: '12px',
    marginBottom: '16px',
  },
  tituloBox: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  titulo: {
    margin: 0,
    fontSize: '16px',
    letterSpacing: '0.5px',
    color: 'white',
  },
  btnFechar: {
    width: '34px',
    height: '34px',
    borderRadius: '6px',
    border: '1px solid #2a2a4a',
    backgroundColor: 'transparent',
    color: '#aaa',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '14px',
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
    gap: '12px',
  },
  campo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
    color: '#aaa',
    fontSize: '11px',
    fontWeight: 'bold',
    letterSpacing: '0.5px',
  },
  campoLargo: {
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
    color: '#aaa',
    fontSize: '11px',
    fontWeight: 'bold',
    letterSpacing: '0.5px',
    gridColumn: '1 / -1',
  },
  labelIcone: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '6px',
  },
  input: {
    padding: '10px 12px',
    borderRadius: '6px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    outline: 'none',
    fontSize: '13px',
    fontFamily: 'inherit',
  },
  textarea: {
    minHeight: '96px',
    resize: 'vertical',
  },
  erro: {
    margin: 0,
    color: '#fb7185',
    fontSize: '13px',
  },
  acoes: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '8px',
  },
  btnCancelar: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '6px',
    padding: '8px 14px',
    borderRadius: '6px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: '#aaa',
    cursor: 'pointer',
    fontSize: '13px',
  },
  btnEnviar: {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '6px',
    padding: '8px 14px',
    borderRadius: '6px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    cursor: 'pointer',
    fontSize: '13px',
    fontWeight: 'bold',
  },
};
