import { useCallback, useEffect, useMemo, useState } from 'react';
import Navbar from '../../components/Navbar';
import { metaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import {
  FiActivity, FiCalendar, FiCheckCircle, FiClock, FiMinus,
  FiPlus, FiTarget, FiTrendingUp, FiX,
} from 'react-icons/fi';
import './analise.css';

const FORM_INICIAL = { titulo: '', quantidadeAlvo: 10, dataPrazo: '' };

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
  const [modalAberto, setModalAberto] = useState(false);
  const [metaPrazo, setMetaPrazo] = useState(null);
  const [novoPrazo, setNovoPrazo] = useState('');
  const [erro, setErro] = useState('');
  const [salvando, setSalvando] = useState(false);

  const carregar = useCallback(() => {
    setErro('');
    return metaService.listar(sessao.id).then(setMetas).catch(e => setErro(e.message));
  }, [sessao.id]);

  useEffect(() => { carregar(); }, [carregar]);

  useEffect(() => {
    if (!modalAberto && !metaPrazo) return undefined;
    const fecharComEsc = (e) => {
      if (e.key === 'Escape') {
        setModalAberto(false);
        setMetaPrazo(null);
      }
    };
    document.addEventListener('keydown', fecharComEsc);
    return () => document.removeEventListener('keydown', fecharComEsc);
  }, [modalAberto, metaPrazo]);

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
        usuarioId: sessao.id,
        ...form,
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
      if (delta > 0) await metaService.adicionarProgresso(meta.id, delta);
      else await metaService.removerProgresso(meta.id, Math.abs(delta));
      await carregar();
    } catch (e) {
      setErro(e.message);
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

  function ajustarAlvo(delta) {
    setForm(atual => ({
      ...atual,
      quantidadeAlvo: Math.max(1, Number(atual.quantidadeAlvo || 1) + delta),
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
          <button className="btn-primary goals-new-button" onClick={() => setModalAberto(true)}>
            <FiPlus /> Nova meta
          </button>
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
        </section>

        {erro && <div className="analysis-error">{erro}</div>}

        <div className="goals-grid">
          {metas.map(meta => {
            const percentual = Math.min(100, Math.round(meta.quantidadeAtual * 100 / meta.quantidadeAlvo));
            const encerrada = meta.status === 'FALHADA' || meta.status === 'CANCELADA';
            return (
              <article key={meta.id} className={`goal-card goal-card--${meta.status.toLowerCase()}`}>
                <div className="goal-card__top">
                  <div className="goal-card__icon"><FiTarget /></div>
                  <span className={`status-pill status-pill--${meta.status.toLowerCase()}`}>
                    {meta.statusDescricao || meta.status.replaceAll('_', ' ').toLowerCase()}
                  </span>
                </div>

                <div className="goal-card__content">
                  <h2>{meta.titulo}</h2>
                  <div className="goal-card__progress-label">
                    <span><strong>{meta.quantidadeAtual}</strong> de {meta.quantidadeAlvo} filmes</span>
                    <strong>{percentual}%</strong>
                  </div>
                  <div className="goal-card__track" aria-label={`${percentual}% concluído`}>
                    <div style={{ width: `${percentual}%` }} />
                  </div>
                  <p className="goal-card__deadline"><FiCalendar /> Até {formatarData(meta.dataPrazo)}</p>
                </div>

                <div className="goal-card__actions">
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
              <label>
                <span>Nome da meta</span>
                <input placeholder="Ex.: Assistir 20 filmes brasileiros" value={form.titulo}
                  onChange={e => setForm({ ...form, titulo: e.target.value })} required autoFocus />
              </label>
              <label>
                <span>Quantidade de filmes</span>
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
    </div>
  );
}
