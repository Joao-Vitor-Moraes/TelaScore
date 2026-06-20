import { useCallback, useEffect, useMemo, useState } from 'react';
import Navbar from '../../components/Navbar';
import { metaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import {
  FiActivity, FiAward, FiBell, FiCalendar, FiCheckCircle, FiClock, FiEyeOff, FiMinus,
  FiEdit2, FiFilm, FiFileText, FiPlus, FiStar, FiTarget, FiTrash2, FiTrendingUp, FiUsers, FiX,
} from 'react-icons/fi';
import './analise.css';

const TIPOS_META = {
  FILMES: { titulo: 'Filmes assistidos', descricao: 'Crie um ritmo para descobrir e assistir filmes.', unidade: 'filmes', exemplo: 'Ex.: Assistir 12 filmes neste mês', icone: FiFilm },
  AVALIACOES: { titulo: 'Avaliações', descricao: 'Transforme o que assistiu em notas e opiniões.', unidade: 'avaliações', exemplo: 'Ex.: Avaliar 8 filmes que assisti', icone: FiStar },
  RESENHAS: { titulo: 'Resenhas', descricao: 'Desenvolva o hábito de escrever sobre cinema.', unidade: 'resenhas', exemplo: 'Ex.: Escrever 4 resenhas completas', icone: FiFileText },
};

const FORM_INICIAL = { titulo: '', quantidadeAlvo: 10, dataPrazo: '', tipo: 'FILMES' };
const FORM_SISTEMA_INICIAL = { titulo: '', quantidadeAlvo: 10, duracaoDias: 30 };

function inferirTipoMeta(meta) {
  if (meta.tipo && TIPOS_META[meta.tipo]) return meta.tipo;
  const texto = (meta.titulo || '').toLowerCase();
  if (texto.includes('resenha') || texto.includes('escrever')) return 'RESENHAS';
  if (texto.includes('avalia') || texto.includes('nota')) return 'AVALIACOES';
  return 'FILMES';
}

function formatarData(data) {
  if (!data) return 'Sem prazo';
  const [ano, mes, dia] = data.split('-').map(Number);
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  }).format(new Date(ano, mes - 1, dia));
}

function dataMinima() {
  const hoje = new Date();
  const ano = hoje.getFullYear();
  const mes = String(hoje.getMonth() + 1).padStart(2, '0');
  const dia = String(hoje.getDate()).padStart(2, '0');
  return `${ano}-${mes}-${dia}`;
}

function diaSeguinte(data) {
  const [ano, mes, dia] = data.split('-').map(Number);
  const seguinte = new Date(ano, mes - 1, dia + 1);
  return [
    seguinte.getFullYear(),
    String(seguinte.getMonth() + 1).padStart(2, '0'),
    String(seguinte.getDate()).padStart(2, '0'),
  ].join('-');
}

export default function Metas() {
  const { sessao } = useAuth();
  const [metas, setMetas] = useState([]);
  const [form, setForm] = useState(FORM_INICIAL);
  const [formSistema, setFormSistema] = useState(FORM_SISTEMA_INICIAL);
  const [modalAberto, setModalAberto] = useState(false);
  const [modalSistemaAberto, setModalSistemaAberto] = useState(false);
  const [metaPrazo, setMetaPrazo] = useState(null);
  const [metaEditando, setMetaEditando] = useState(null);
  const [metaExcluindo, setMetaExcluindo] = useState(null);
  const [formEdicao, setFormEdicao] = useState(FORM_INICIAL);
  const [novoPrazo, setNovoPrazo] = useState('');
  const [totalPontos, setTotalPontos] = useState(0);
  const [feedback, setFeedback] = useState('');
  const [modosAtualizacao, setModosAtualizacao] = useState({});
  const [erro, setErro] = useState('');
  const [salvando, setSalvando] = useState(false);

  const carregar = useCallback(() => {
    setErro('');
    return Promise.all([metaService.listar(), metaService.pontuacao()])
      .then(([metasRecebidas, pontuacao]) => {
        setMetas(metasRecebidas);
        setTotalPontos(pontuacao.totalPontos);
      })
      .catch(e => setErro(e.message));
  }, []);

  useEffect(() => { carregar(); }, [carregar]);

  useEffect(() => {
    if (!modalAberto && !modalSistemaAberto && !metaPrazo && !metaEditando && !metaExcluindo) return undefined;
    const fecharComEsc = (e) => {
      if (e.key === 'Escape') {
        setModalAberto(false);
        setModalSistemaAberto(false);
        setMetaPrazo(null);
        setMetaEditando(null);
        setMetaExcluindo(null);
      }
    };
    document.addEventListener('keydown', fecharComEsc);
    return () => document.removeEventListener('keydown', fecharComEsc);
  }, [modalAberto, modalSistemaAberto, metaPrazo, metaEditando, metaExcluindo]);

  useEffect(() => {
    if (!feedback) return undefined;
    const timeout = setTimeout(() => setFeedback(''), 4200);
    return () => clearTimeout(timeout);
  }, [feedback]);

  const resumo = useMemo(() => ({
    andamento: metas.filter(meta => meta.status === 'EM_ANDAMENTO').length,
    concluidas: metas.filter(meta => meta.status === 'CONCLUIDA').length,
    progresso: metas.length
      ? Math.round(metas.reduce((total, meta) =>
        total + Math.min(100, meta.quantidadeAtual * 100 / meta.quantidadeAlvo), 0) / metas.length)
      : 0,
  }), [metas]);

  async function criar(e) {
    e.preventDefault();
    setErro('');
    setSalvando(true);
    try {
      await metaService.criar({
        titulo: form.titulo,
        dataPrazo: form.dataPrazo,
        tipo: form.tipo,
        quantidadeAlvo: Number(form.quantidadeAlvo),
      });
      setForm(FORM_INICIAL);
      setModalAberto(false);
      await carregar();
    } catch (e) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  async function alterarProgresso(meta, delta) {
    setErro('');
    try {
      if (delta > 0) {
        const modo = modosAtualizacao[meta.id] || 'FEEDBACK';
        const resultado = await metaService.adicionarProgresso(meta.id, delta, modo);
        if (modo === 'FEEDBACK') {
          setFeedback(resultado.status === 'CONCLUIDA' ? '' : resultado.mensagem);
          setTotalPontos(resultado.totalPontos);
          if (resultado.status === 'CONCLUIDA') {
            window.dispatchEvent(new Event('telascore:notificacoes-atualizadas'));
          }
        } else {
          setFeedback('');
          setTotalPontos(resultado.totalPontos);
        }
      } else {
        await metaService.removerProgresso(meta.id, Math.abs(delta));
      }
      await carregar();
    } catch (e) {
      setErro(e.message);
    }
  }

  async function criarMetaSistema(e) {
    e.preventDefault();
    setErro('');
    setSalvando(true);
    try {
      await metaService.criarSistema({
        ...formSistema,
        quantidadeAlvo: Number(formSistema.quantidadeAlvo),
        duracaoDias: Number(formSistema.duracaoDias),
      });
      setFormSistema(FORM_SISTEMA_INICIAL);
      setModalSistemaAberto(false);
      setFeedback('Meta do sistema criada para todos os usuários.');
      await carregar();
    } catch (e) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  function abrirPrazo(meta) {
    setMetaPrazo(meta);
    setNovoPrazo(meta.dataPrazo);
  }

  async function estender(e) {
    e.preventDefault();
    setErro('');
    setSalvando(true);
    try {
      await metaService.estenderPrazo(metaPrazo.id, novoPrazo);
      setMetaPrazo(null);
      await carregar();
    } catch (e) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  function abrirEdicao(meta) {
    setMetaEditando(meta);
    setFormEdicao({
      titulo: meta.titulo,
      quantidadeAlvo: meta.quantidadeAlvo,
      dataPrazo: meta.dataPrazo,
    });
    setErro('');
  }

  async function editarMeta(e) {
    e.preventDefault();
    setSalvando(true);
    setErro('');
    try {
      await metaService.editar(metaEditando.id, {
        ...formEdicao,
        quantidadeAlvo: Number(formEdicao.quantidadeAlvo),
      });
      setMetaEditando(null);
      setFeedback('Meta atualizada com sucesso.');
      await carregar();
    } catch (e) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  async function excluirMeta() {
    setSalvando(true);
    setErro('');
    try {
      await metaService.remover(metaExcluindo.id);
      setMetaExcluindo(null);
      setFeedback('Meta excluída.');
      await carregar();
    } catch (e) {
      setErro(e.message);
    } finally {
      setSalvando(false);
    }
  }

  function ajustarAlvo(delta) {
    setForm(atual => ({
      ...atual,
      quantidadeAlvo: Math.max(1, Number(atual.quantidadeAlvo || 1) + delta),
    }));
  }

  function ajustarCampoSistema(campo, delta) {
    setFormSistema(atual => ({
      ...atual,
      [campo]: Math.max(1, Number(atual[campo] || 1) + delta),
    }));
  }

  return (
    <div className="cinema-page analysis-page">
      <Navbar />
      <main className="cinema-container goals-container">
        <div className="goals-heading">
          <div>
            <p className="page-eyebrow">Desafio pessoal</p>
            <h1 className="page-title">Minhas metas</h1>
            <p className="page-description">Pequenos desafios, grandes histórias. Acompanhe seu ritmo e celebre cada filme.</p>
          </div>
          <div className="goals-heading__actions">
            {sessao.papel === 'ADMIN' && (
              <button className="btn-secondary goals-new-button" onClick={() => setModalSistemaAberto(true)}>
                <FiUsers /> Meta do sistema
              </button>
            )}
            <button className="btn-primary goals-new-button" onClick={() => setModalAberto(true)}>
              <FiPlus /> Nova meta
            </button>
          </div>
        </div>

        <section className="goals-summary" aria-label="Resumo das metas">
          <div className="goals-summary__item">
            <FiActivity />
            <div><strong>{resumo.andamento}</strong><span>Em andamento</span></div>
          </div>
          <div className="goals-summary__item">
            <FiCheckCircle />
            <div><strong>{resumo.concluidas}</strong><span>Concluídas</span></div>
          </div>
          <div className="goals-summary__item">
            <FiTrendingUp />
            <div><strong>{resumo.progresso}%</strong><span>Progresso médio</span></div>
          </div>
          <div className="goals-summary__item goals-summary__item--points">
            <FiAward />
            <div><strong>{totalPontos}</strong><span>Pontos de gamificação</span></div>
          </div>
        </section>

        {erro && <div className="analysis-error">{erro}</div>}
        {feedback && <div className="analysis-feedback"><FiAward /> {feedback}</div>}

        <div className="goals-grid">
          {metas.map(meta => {
            const percentual = Math.min(100, Math.round(meta.quantidadeAtual * 100 / meta.quantidadeAlvo));
            const encerrada = meta.status === 'FALHADA' || meta.status === 'CANCELADA';
            const tipoMeta = TIPOS_META[inferirTipoMeta(meta)];
            const IconeTipo = tipoMeta.icone;
            return (
              <article key={meta.id} className={`goal-card goal-card--${meta.status.toLowerCase()}`}>
                <div className="goal-card__top">
                  <div className="goal-card__icon"><IconeTipo /></div>
                  <div className="goal-card__top-right">
                    {!meta.metaDoSistema && (
                      <div className="goal-card__manage">
                        <button onClick={() => abrirEdicao(meta)} title="Editar meta" aria-label="Editar meta"><FiEdit2 /></button>
                        <button className="is-danger" onClick={() => setMetaExcluindo(meta)}
                          title="Excluir meta" aria-label="Excluir meta"><FiTrash2 /></button>
                      </div>
                    )}
                    <div className="goal-card__badges">
                      {meta.metaDoSistema && <span className="system-goal-pill"><FiUsers /> Meta do sistema</span>}
                      <span className={`status-pill status-pill--${meta.status.toLowerCase()}`}>
                        {meta.statusDescricao || meta.status.replaceAll('_', ' ').toLowerCase()}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="goal-card__content">
                  <span className="goal-card__type">{tipoMeta.titulo}</span>
                  <h2>{meta.titulo}</h2>
                  <div className="goal-card__progress-label">
                    <span><strong>{meta.quantidadeAtual}</strong> de {meta.quantidadeAlvo} {tipoMeta.unidade}</span>
                    <strong>{percentual}%</strong>
                  </div>
                  <div className="goal-card__track" aria-label={`${percentual}% concluído`}>
                    <div style={{ width: `${percentual}%` }} />
                  </div>
                  <p className="goal-card__deadline"><FiCalendar /> Até {formatarData(meta.dataPrazo)}</p>
                </div>

                <div className="goal-card__actions">
                  <div className="goal-update-mode" aria-label="Modo de atualização">
                    <button
                      type="button"
                      className={(modosAtualizacao[meta.id] || 'FEEDBACK') === 'FEEDBACK' ? 'is-active' : ''}
                      onClick={() => setModosAtualizacao(atual => ({ ...atual, [meta.id]: 'FEEDBACK' }))}
                      title="Ao concluir, envia uma notificação para o sininho. Os pontos são concedidos nos dois modos."
                    >
                      <FiBell /> Notificar
                    </button>
                    <button
                      type="button"
                      className={modosAtualizacao[meta.id] === 'SILENCIOSO' ? 'is-active' : ''}
                      onClick={() => setModosAtualizacao(atual => ({ ...atual, [meta.id]: 'SILENCIOSO' }))}
                      title="Concede os pontos normalmente, mas não cria notificação no sininho"
                    >
                      <FiEyeOff /> Silencioso
                    </button>
                  </div>
                  <div className="goal-card__action-row">
                  <div className="goal-counter" aria-label="Alterar progresso">
                    <button
                      onClick={() => alterarProgresso(meta, -1)}
                      disabled={meta.quantidadeAtual === 0 || encerrada}
                      title="Diminuir progresso"
                    ><FiMinus /></button>
                    <span>{meta.quantidadeAtual}</span>
                    <button
                      onClick={() => alterarProgresso(meta, 1)}
                      disabled={meta.status !== 'EM_ANDAMENTO'}
                      title="Aumentar progresso"
                    ><FiPlus /></button>
                  </div>
                  {meta.status === 'EM_ANDAMENTO' && (
                    <button className="goal-deadline-button" onClick={() => abrirPrazo(meta)}>
                      <FiClock /> Alterar prazo
                    </button>
                  )}
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      </main>

      {modalAberto && (
        <div className="analysis-modal-backdrop" onMouseDown={() => setModalAberto(false)}>
          <section className="analysis-modal" role="dialog" aria-modal="true" aria-labelledby="nova-meta-titulo" onMouseDown={e => e.stopPropagation()}>
            <button className="analysis-modal__close" onClick={() => setModalAberto(false)} aria-label="Fechar"><FiX /></button>
            <div className="analysis-modal__header">
              <span><FiTarget /></span>
              <div><p className="page-eyebrow">Novo desafio</p><h2 id="nova-meta-titulo">Criar uma meta</h2></div>
            </div>
            <form onSubmit={criar} className="analysis-modal__form">
              <fieldset className="goal-type-picker">
                <legend>O que você quer transformar em hábito?</legend>
                <div>
                  {Object.entries(TIPOS_META).map(([chave, tipo]) => {
                    const Icone = tipo.icone;
                    return (
                      <button key={chave} type="button" className={form.tipo === chave ? 'is-selected' : ''}
                        onClick={() => setForm({ ...form, tipo: chave })}>
                        <Icone />
                        <span><strong>{tipo.titulo}</strong><small>{tipo.descricao}</small></span>
                      </button>
                    );
                  })}
                </div>
              </fieldset>
              <label>
                <span>Nome da meta</span>
                <input placeholder={TIPOS_META[form.tipo].exemplo} value={form.titulo}
                  onChange={e => setForm({ ...form, titulo: e.target.value })} required autoFocus />
              </label>
              <label>
                <span>Quantidade de {TIPOS_META[form.tipo].unidade}</span>
                <div className="number-stepper">
                  <button type="button" onClick={() => ajustarAlvo(-1)} disabled={Number(form.quantidadeAlvo) <= 1}><FiMinus /></button>
                  <input type="number" min="1" value={form.quantidadeAlvo}
                    onChange={e => setForm({ ...form, quantidadeAlvo: e.target.value })} required />
                  <button type="button" onClick={() => ajustarAlvo(1)}><FiPlus /></button>
                </div>
              </label>
              <label>
                <span>Quero concluir até</span>
                <input type="date" min={dataMinima()} value={form.dataPrazo}
                  onChange={e => setForm({ ...form, dataPrazo: e.target.value })} required />
              </label>
              <div className="analysis-modal__footer">
                <button type="button" className="btn-secondary" onClick={() => setModalAberto(false)}>Cancelar</button>
                <button className="btn-primary" disabled={salvando}><FiPlus /> {salvando ? 'Criando...' : 'Criar meta'}</button>
              </div>
            </form>
          </section>
        </div>
      )}

      {modalSistemaAberto && (
        <div className="analysis-modal-backdrop" onMouseDown={() => setModalSistemaAberto(false)}>
          <section className="analysis-modal" role="dialog" aria-modal="true" aria-labelledby="nova-meta-sistema-titulo" onMouseDown={e => e.stopPropagation()}>
            <button className="analysis-modal__close" onClick={() => setModalSistemaAberto(false)} aria-label="Fechar"><FiX /></button>
            <div className="analysis-modal__header">
              <span><FiUsers /></span>
              <div>
                <p className="page-eyebrow">Administração</p>
                <h2 id="nova-meta-sistema-titulo">Criar meta para todos</h2>
              </div>
            </div>
            <p className="analysis-modal__hint">
              Cada usuário receberá uma cópia individual desta meta. O progresso de uma pessoa não altera o de outra.
            </p>
            <form onSubmit={criarMetaSistema} className="analysis-modal__form">
              <label>
                <span>Nome da meta do sistema</span>
                <input placeholder="Ex.: Maratona de cinema nacional" value={formSistema.titulo}
                  onChange={e => setFormSistema({ ...formSistema, titulo: e.target.value })} required autoFocus />
              </label>
              <label>
                <span>Quantidade de filmes</span>
                <div className="number-stepper">
                  <button type="button" onClick={() => ajustarCampoSistema('quantidadeAlvo', -1)}
                    disabled={Number(formSistema.quantidadeAlvo) <= 1}><FiMinus /></button>
                  <input type="number" min="1" value={formSistema.quantidadeAlvo}
                    onChange={e => setFormSistema({ ...formSistema, quantidadeAlvo: e.target.value })} required />
                  <button type="button" onClick={() => ajustarCampoSistema('quantidadeAlvo', 1)}><FiPlus /></button>
                </div>
              </label>
              <label>
                <span>Duração em dias</span>
                <div className="number-stepper">
                  <button type="button" onClick={() => ajustarCampoSistema('duracaoDias', -1)}
                    disabled={Number(formSistema.duracaoDias) <= 1}><FiMinus /></button>
                  <input type="number" min="1" value={formSistema.duracaoDias}
                    onChange={e => setFormSistema({ ...formSistema, duracaoDias: e.target.value })} required />
                  <button type="button" onClick={() => ajustarCampoSistema('duracaoDias', 1)}><FiPlus /></button>
                </div>
              </label>
              <div className="analysis-modal__footer">
                <button type="button" className="btn-secondary" onClick={() => setModalSistemaAberto(false)}>Cancelar</button>
                <button className="btn-primary" disabled={salvando}>
                  <FiUsers /> {salvando ? 'Criando...' : 'Criar para todos'}
                </button>
              </div>
            </form>
          </section>
        </div>
      )}

      {metaPrazo && (
        <div className="analysis-modal-backdrop" onMouseDown={() => setMetaPrazo(null)}>
          <section className="analysis-modal analysis-modal--small" role="dialog" aria-modal="true" aria-labelledby="prazo-meta-titulo" onMouseDown={e => e.stopPropagation()}>
            <button className="analysis-modal__close" onClick={() => setMetaPrazo(null)} aria-label="Fechar"><FiX /></button>
            <div className="analysis-modal__header">
              <span><FiCalendar /></span>
              <div><p className="page-eyebrow">Novo prazo</p><h2 id="prazo-meta-titulo">{metaPrazo.titulo}</h2></div>
            </div>
            <form onSubmit={estender} className="analysis-modal__form">
              <label>
                <span>Escolha uma data posterior a {formatarData(metaPrazo.dataPrazo)}</span>
                <input type="date" min={diaSeguinte(metaPrazo.dataPrazo)} value={novoPrazo}
                  onChange={e => setNovoPrazo(e.target.value)} required autoFocus />
              </label>
              <div className="analysis-modal__footer">
                <button type="button" className="btn-secondary" onClick={() => setMetaPrazo(null)}>Cancelar</button>
                <button className="btn-primary" disabled={salvando}>{salvando ? 'Salvando...' : 'Salvar prazo'}</button>
              </div>
            </form>
          </section>
        </div>
      )}

      {metaEditando && (
        <div className="analysis-modal-backdrop" onMouseDown={() => setMetaEditando(null)}>
          <section className="analysis-modal" role="dialog" aria-modal="true" aria-labelledby="editar-meta-titulo"
            onMouseDown={e => e.stopPropagation()}>
            <button className="analysis-modal__close" onClick={() => setMetaEditando(null)} aria-label="Fechar"><FiX /></button>
            <div className="analysis-modal__header">
              <span><FiEdit2 /></span>
              <div><p className="page-eyebrow">Ajustar desafio</p><h2 id="editar-meta-titulo">Editar meta</h2></div>
            </div>
            <form onSubmit={editarMeta} className="analysis-modal__form">
              <label>
                <span>Nome da meta</span>
                <input value={formEdicao.titulo}
                  onChange={e => setFormEdicao({ ...formEdicao, titulo: e.target.value })} required autoFocus />
              </label>
              <label>
                <span>Quantidade de filmes</span>
                <input type="number" min="1" value={formEdicao.quantidadeAlvo}
                  onChange={e => setFormEdicao({ ...formEdicao, quantidadeAlvo: e.target.value })} required />
              </label>
              <label>
                <span>Quero concluir até</span>
                <input type="date" min={dataMinima()} value={formEdicao.dataPrazo}
                  onChange={e => setFormEdicao({ ...formEdicao, dataPrazo: e.target.value })} required />
              </label>
              <div className="analysis-modal__footer">
                <button type="button" className="btn-secondary" onClick={() => setMetaEditando(null)}>Cancelar</button>
                <button className="btn-primary" disabled={salvando}>
                  <FiEdit2 /> {salvando ? 'Salvando...' : 'Salvar alterações'}
                </button>
              </div>
            </form>
          </section>
        </div>
      )}

      {metaExcluindo && (
        <div className="analysis-modal-backdrop" onMouseDown={() => setMetaExcluindo(null)}>
          <section className="analysis-modal analysis-modal--small" role="dialog" aria-modal="true"
            aria-labelledby="excluir-meta-titulo" onMouseDown={e => e.stopPropagation()}>
            <button className="analysis-modal__close" onClick={() => setMetaExcluindo(null)} aria-label="Fechar"><FiX /></button>
            <div className="analysis-modal__header analysis-modal__header--danger">
              <span><FiTrash2 /></span>
              <div><p className="page-eyebrow">Excluir desafio</p><h2 id="excluir-meta-titulo">Excluir meta?</h2></div>
            </div>
            <p className="analysis-modal__hint">
              A meta “{metaExcluindo.titulo}” e todo o progresso registrado nela serão removidos.
            </p>
            <div className="analysis-modal__footer">
              <button type="button" className="btn-secondary" onClick={() => setMetaExcluindo(null)}>Manter meta</button>
              <button type="button" className="goal-delete-confirm" onClick={excluirMeta} disabled={salvando}>
                <FiTrash2 /> {salvando ? 'Excluindo...' : 'Excluir definitivamente'}
              </button>
            </div>
          </section>
        </div>
      )}
    </div>
  );
}
