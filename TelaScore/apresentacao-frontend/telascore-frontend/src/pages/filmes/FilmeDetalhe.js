import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../../components/Navbar';
import { filmeService, avaliacaoService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function FilmeDetalhe() {
  const { sessao } = useAuth();
  const USUARIO_ID = sessao.id;
  const isAdmin = sessao.papel === 'ADMIN';
  const { id } = useParams();
  const navigate = useNavigate();
  const [filme, setFilme] = useState(null);
  const [avaliacoes, setAvaliacoes] = useState([]);
  const [novaAvaliacao, setNovaAvaliacao] = useState({ valorNota: 5, comentario: '', visibilidade: 'PUBLICA' });
  const [erro, setErro] = useState(null);
  const [editandoId, setEditandoId] = useState(null);
  const [edicao, setEdicao] = useState({ valorNota: 5, resenha: '' });

  useEffect(() => {
    filmeService.obter(id).then(setFilme).catch(() => setErro('Filme não encontrado.'));
    avaliacaoService.listarPorFilme(id, USUARIO_ID).then(setAvaliacoes).catch(() => {});
  }, [id, USUARIO_ID]);

  async function recarregar() {
    const [atualizadas, filmeAtualizado] = await Promise.all([
      avaliacaoService.listarPorFilme(id, USUARIO_ID),
      filmeService.obter(id),
    ]);
    setAvaliacoes(atualizadas);
    setFilme(filmeAtualizado);
  }

  async function handleAvaliar(e) {
    e.preventDefault();
    try {
      await avaliacaoService.avaliar({
        filmeId: parseInt(id),
        usuarioId: USUARIO_ID,
        valorNota: parseInt(novaAvaliacao.valorNota),
        comentario: novaAvaliacao.comentario,
        visibilidade: novaAvaliacao.visibilidade,
      });
      await recarregar();
      setNovaAvaliacao({ valorNota: 5, comentario: '', visibilidade: 'PUBLICA' });
    } catch {
      alert('Erro ao enviar avaliação.');
    }
  }

  async function handleRemoverAvaliacao(avaliacaoId) {
    if (!window.confirm('Remover esta avaliação?')) return;
    try {
      await avaliacaoService.remover(avaliacaoId);
      await recarregar();
    } catch {
      alert('Erro ao remover avaliação.');
    }
  }

  async function handleSalvarEdicao(avaliacaoId) {
    try {
      await avaliacaoService.atualizar(avaliacaoId, {
        valorNota: parseInt(edicao.valorNota),
        resenha: edicao.resenha,
      });
      await recarregar();
      setEditandoId(null);
    } catch {
      alert('Erro ao editar avaliação.');
    }
  }

  if (erro) return <div style={styles.pagina}><Navbar /><p style={styles.msgErro}>{erro}</p></div>;
  if (!filme) return <div style={styles.pagina}><Navbar /><p style={styles.msg}>Carregando...</p></div>;

  const minhaAvaliacao = avaliacoes.find(a => a.usuarioId === USUARIO_ID);
  const outrasAvaliacoes = avaliacoes.filter(a => a.usuarioId !== USUARIO_ID);

  return (
    <div style={styles.pagina}>
      <Navbar />
      <div style={styles.conteudo}>

        {/* Cabeçalho do filme */}
        <button style={styles.btnVoltar} onClick={() => navigate('/filmes')}>← Voltar</button>

        <div style={styles.filmeHeader}>
          {filme.imagemUrl && (
            <img src={filme.imagemUrl} alt={filme.titulo} style={styles.poster} />
          )}
          <div style={styles.filmeInfo}>
            <h1 style={styles.filmeTitulo}>{filme.titulo}</h1>
            <p style={styles.filmeSub}>{filme.anoLancamento}{filme.nomeDiretor ? ` · ${filme.nomeDiretor}` : ''}</p>
            <p style={styles.filmeNota}>
              {filme.mediaNotas > 0 ? `⭐ ${filme.mediaNotas.toFixed(1)} de média` : 'Sem avaliações ainda'}
            </p>
            {filme.sinopse && <p style={styles.filmeSinopse}>{filme.sinopse}</p>}
            {isAdmin && (
              <button style={styles.btnEditar} onClick={() => navigate(`/filmes/${id}/editar`)}>
                Editar filme
              </button>
            )}
          </div>
        </div>

        {/* Minha avaliação */}
        <div style={styles.secao}>
          <h2 style={styles.secaoTitulo}>Minha avaliação</h2>

          {minhaAvaliacao && editandoId !== minhaAvaliacao.avaliacaoId ? (
            <div style={styles.cardAvaliacao}>
              <div style={styles.avaliacaoTopo}>
                <span style={styles.estrelas}>{'⭐'.repeat(minhaAvaliacao.valorNota)}</span>
                {minhaAvaliacao.visibilidade === 'PRIVADA' && (
                  <span style={styles.badgePrivada}>🔒 Privada</span>
                )}
                <div style={styles.acoesAvaliacao}>
                  <button
                    style={styles.btnAcaoTexto}
                    onClick={() => { setEditandoId(minhaAvaliacao.avaliacaoId); setEdicao({ valorNota: minhaAvaliacao.valorNota, resenha: minhaAvaliacao.resenha || '' }); }}
                  >
                    Editar
                  </button>
                  <button
                    style={{ ...styles.btnAcaoTexto, color: '#e94560' }}
                    onClick={() => handleRemoverAvaliacao(minhaAvaliacao.avaliacaoId)}
                  >
                    Remover
                  </button>
                </div>
              </div>
              {minhaAvaliacao.resenha && <p style={styles.avaliacaoResenha}>{minhaAvaliacao.resenha}</p>}
              <p style={styles.avaliacaoData}>{minhaAvaliacao.dataAvaliacao}</p>
            </div>
          ) : editandoId === minhaAvaliacao?.avaliacaoId ? (
            <div style={styles.cardAvaliacao}>
              <select
                value={edicao.valorNota}
                onChange={e => setEdicao({ ...edicao, valorNota: e.target.value })}
                style={styles.select}
              >
                {[1, 2, 3, 4, 5].map(n => <option key={n} value={n}>{n} ⭐</option>)}
              </select>
              <textarea
                value={edicao.resenha}
                onChange={e => setEdicao({ ...edicao, resenha: e.target.value })}
                rows={3}
                placeholder="Sua resenha..."
                style={styles.textarea}
              />
              <div style={styles.formBotoes}>
                <button style={styles.btnCancelar} onClick={() => setEditandoId(null)}>Cancelar</button>
                <button style={styles.btnSalvar} onClick={() => handleSalvarEdicao(minhaAvaliacao.avaliacaoId)}>Salvar</button>
              </div>
            </div>
          ) : (
            <form onSubmit={handleAvaliar} style={styles.formAvaliacao}>
              <div style={styles.formLinha}>
                <div style={styles.formCampo}>
                  <label style={styles.label}>NOTA</label>
                  <select
                    value={novaAvaliacao.valorNota}
                    onChange={e => setNovaAvaliacao({ ...novaAvaliacao, valorNota: e.target.value })}
                    style={styles.select}
                  >
                    {[1, 2, 3, 4, 5].map(n => <option key={n} value={n}>{n} ⭐</option>)}
                  </select>
                </div>
                <div style={styles.formCampo}>
                  <label style={styles.label}>VISIBILIDADE</label>
                  <select
                    value={novaAvaliacao.visibilidade}
                    onChange={e => setNovaAvaliacao({ ...novaAvaliacao, visibilidade: e.target.value })}
                    style={styles.select}
                  >
                    <option value="PUBLICA">🌍 Pública</option>
                    <option value="PRIVADA">🔒 Privada</option>
                  </select>
                </div>
              </div>
              <label style={styles.label}>COMENTÁRIO</label>
              <textarea
                value={novaAvaliacao.comentario}
                onChange={e => setNovaAvaliacao({ ...novaAvaliacao, comentario: e.target.value })}
                rows={3}
                placeholder="Escreva sua resenha..."
                style={styles.textarea}
              />
              <button type="submit" style={styles.btnEnviar}>Enviar avaliação</button>
            </form>
          )}
        </div>

        {/* Avaliações de outros usuários */}
        {outrasAvaliacoes.length > 0 && (
          <div style={styles.secao}>
            <h2 style={styles.secaoTitulo}>Outras avaliações</h2>
            <div style={styles.listaAvaliacoes}>
              {outrasAvaliacoes.map(av => (
                <div key={av.avaliacaoId} style={styles.cardAvaliacao}>
                  <div style={styles.avaliacaoTopo}>
                    <span style={styles.estrelas}>{'⭐'.repeat(av.valorNota)}</span>
                    <span style={styles.avaliacaoUsuario}>Usuário {av.usuarioId}</span>
                    {av.visibilidade === 'PRIVADA' && (
                      <span style={styles.badgePrivada}>🔒 Privada</span>
                    )}
                  </div>
                  {av.resenha && <p style={styles.avaliacaoResenha}>{av.resenha}</p>}
                  <p style={styles.avaliacaoData}>{av.dataAvaliacao}</p>
                </div>
              ))}
            </div>
          </div>
        )}
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
    maxWidth: '760px',
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
    marginBottom: '24px',
    display: 'block',
  },
  filmeHeader: {
    display: 'flex',
    gap: '24px',
    marginBottom: '40px',
    backgroundColor: '#16213e',
    borderRadius: '12px',
    padding: '24px',
  },
  poster: {
    width: '120px',
    borderRadius: '8px',
    objectFit: 'cover',
    flexShrink: 0,
  },
  filmeInfo: {
    flex: 1,
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
  },
  filmeTitulo: {
    margin: 0,
    fontSize: '24px',
    fontWeight: 'bold',
  },
  filmeSub: {
    color: '#aaa',
    margin: 0,
    fontSize: '14px',
  },
  filmeNota: {
    color: '#f0a500',
    margin: 0,
    fontSize: '14px',
  },
  filmeSinopse: {
    color: '#ccc',
    margin: 0,
    fontSize: '14px',
    lineHeight: '1.6',
  },
  btnEditar: {
    marginTop: '8px',
    alignSelf: 'flex-start',
    padding: '8px 16px',
    borderRadius: '6px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: 'white',
    cursor: 'pointer',
    fontSize: '13px',
  },
  secao: {
    marginBottom: '40px',
  },
  secaoTitulo: {
    fontSize: '18px',
    fontWeight: 'bold',
    marginBottom: '16px',
    borderBottom: '1px solid #2a2a4a',
    paddingBottom: '8px',
  },
  cardAvaliacao: {
    backgroundColor: '#16213e',
    borderRadius: '10px',
    padding: '16px',
    marginBottom: '12px',
  },
  avaliacaoTopo: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    marginBottom: '8px',
    flexWrap: 'wrap',
  },
  estrelas: {
    fontSize: '15px',
  },
  avaliacaoUsuario: {
    color: '#aaa',
    fontSize: '13px',
  },
  badgePrivada: {
    fontSize: '11px',
    backgroundColor: '#f0a50033',
    border: '1px solid #f0a500',
    color: '#f0a500',
    borderRadius: '4px',
    padding: '2px 6px',
  },
  acoesAvaliacao: {
    marginLeft: 'auto',
    display: 'flex',
    gap: '12px',
  },
  btnAcaoTexto: {
    background: 'none',
    border: 'none',
    color: '#aaa',
    cursor: 'pointer',
    fontSize: '13px',
    padding: 0,
  },
  avaliacaoResenha: {
    color: '#ccc',
    fontSize: '14px',
    margin: '0 0 8px 0',
    lineHeight: '1.5',
  },
  avaliacaoData: {
    color: '#555',
    fontSize: '12px',
    margin: 0,
  },
  formAvaliacao: {
    backgroundColor: '#16213e',
    borderRadius: '10px',
    padding: '20px',
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
  },
  formLinha: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '16px',
  },
  formCampo: {
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
  select: {
    padding: '10px 12px',
    borderRadius: '6px',
    border: '1px solid #2a2a4a',
    backgroundColor: '#0f3460',
    color: 'white',
    fontSize: '14px',
    outline: 'none',
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
  btnEnviar: {
    alignSelf: 'flex-end',
    padding: '10px 24px',
    borderRadius: '6px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold',
  },
  formBotoes: {
    display: 'flex',
    justifyContent: 'flex-end',
    gap: '10px',
  },
  btnCancelar: {
    padding: '8px 18px',
    borderRadius: '6px',
    border: '1px solid #aaa',
    backgroundColor: 'transparent',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
  },
  btnSalvar: {
    padding: '8px 18px',
    borderRadius: '6px',
    border: 'none',
    backgroundColor: '#e94560',
    color: 'white',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: 'bold',
  },
  listaAvaliacoes: {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
  },
  msg: {
    color: '#aaa',
    textAlign: 'center',
    marginTop: '60px',
  },
  msgErro: {
    color: '#e94560',
    textAlign: 'center',
    marginTop: '60px',
  },
};
