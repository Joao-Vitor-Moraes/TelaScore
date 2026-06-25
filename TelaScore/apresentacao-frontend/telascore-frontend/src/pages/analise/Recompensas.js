import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  FiAward, FiBarChart2, FiCheckCircle, FiFilm, FiHelpCircle, FiList,
  FiRefreshCw, FiShield, FiStar, FiTarget, FiTrendingUp, FiUsers, FiZap
} from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import { recompensaService } from '../../services/api';
import '../analise/analise.css';

const ACOES_INFO = {
  AVALIAR_FILME: {
    label: 'Avaliou um filme',
    descricao: 'Uma nova nota ou resenha publicada.',
    pontos: 25,
    icone: FiFilm,
    cor: '#8fd7ff'
  },
  CRIAR_LISTA: {
    label: 'Criou uma lista',
    descricao: 'Organizou filmes em uma lista propria.',
    pontos: 40,
    icone: FiList,
    cor: '#ffb86b'
  },
  CONVIDAR_AMIGO: {
    label: 'Fez uma amizade',
    descricao: 'Quando duas pessoas passam a se seguir.',
    pontos: 60,
    icone: FiUsers,
    cor: '#72e49a'
  },
  ACERTAR_QUIZ: {
    label: 'Concluiu um quiz',
    descricao: 'Tentativa aprovada em um desafio.',
    pontos: 80,
    icone: FiHelpCircle,
    cor: '#f6d66f'
  },
  COMPLETAR_META: {
    label: 'Completou uma meta',
    descricao: 'Meta pessoal ou desafio do sistema finalizado.',
    pontos: 150,
    icone: FiTarget,
    cor: '#ff6f8a'
  }
};

const NIVEIS = [
  { nome: 'Explorador', minPontos: 0, cor: '#aeb4c4', icone: FiStar },
  { nome: 'Espectador atento', minPontos: 150, cor: '#8fd7ff', icone: FiFilm },
  { nome: 'Cinefilo', minPontos: 450, cor: '#72e49a', icone: FiZap },
  { nome: 'Critico', minPontos: 900, cor: '#f6d66f', icone: FiAward },
  { nome: 'Curador', minPontos: 1600, cor: '#ff9f7f', icone: FiShield },
  { nome: 'Lenda TelaScore', minPontos: 2600, cor: '#ff6f8a', icone: FiTrendingUp }
];

function calcularNivel(total) {
  let atual = NIVEIS[0];
  let proximo = NIVEIS[1];
  for (let i = NIVEIS.length - 1; i >= 0; i--) {
    if (total >= NIVEIS[i].minPontos) {
      atual = NIVEIS[i];
      proximo = NIVEIS[i + 1] || null;
      break;
    }
  }
  return { atual, proximo };
}

function formatarData(valor) {
  if (!valor) return '';
  const data = new Date(valor);
  if (Number.isNaN(data.getTime())) return '';
  return new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit'
  }).format(data);
}

function infoDaAcao(acao) {
  return ACOES_INFO[acao] || {
    label: acao,
    descricao: 'Atividade concluida no TelaScore.',
    pontos: 0,
    icone: FiCheckCircle,
    cor: '#d6d8e4'
  };
}

export default function Recompensas() {
  const { sessao } = useAuth();
  const [totalPontos, setTotalPontos] = useState(0);
  const [historico, setHistorico] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');

  const carregar = useCallback(async () => {
    if (!sessao?.id) return;
    setCarregando(true);
    setErro('');
    try {
      const [total, lista] = await Promise.all([
        recompensaService.consultarTotal(sessao.id),
        recompensaService.listarHistorico(sessao.id),
      ]);
      setTotalPontos(Number(total) || 0);
      setHistorico(Array.isArray(lista) ? lista : []);
    } catch {
      setTotalPontos(0);
      setHistorico([]);
      setErro('Não foi possível carregar seu progresso agora.');
    } finally {
      setCarregando(false);
    }
  }, [sessao?.id]);

  useEffect(() => {
    carregar();
  }, [carregar]);

  const { atual: nivelAtual, proximo: nivelProximo } = calcularNivel(totalPontos);
  const IconeNivel = nivelAtual.icone;
  const progressoNivel = nivelProximo
    ? Math.min(((totalPontos - nivelAtual.minPontos) / (nivelProximo.minPontos - nivelAtual.minPontos)) * 100, 100)
    : 100;
  const faltam = nivelProximo ? Math.max(nivelProximo.minPontos - totalPontos, 0) : 0;

  const pontosRecentes = useMemo(() => {
    const seteDias = Date.now() - 7 * 24 * 60 * 60 * 1000;
    return historico
      .filter(item => new Date(item.dataRegistro).getTime() >= seteDias)
      .reduce((total, item) => total + (Number(item.pontos) || 0), 0);
  }, [historico]);

  const acoesUnicas = useMemo(() => new Set(historico.map(item => item.acao)).size, [historico]);

  return (
    <div className="analysis-page level-page">
      <Navbar />
      <main className="goals-container level-shell">
        <section className="level-hero">
          <div className="level-hero__content">
            <p className="page-eyebrow">Progresso</p>
            <h1 className="page-title">Nível TelaScore</h1>
            <p className="page-description">
              Seu nível cresce conforme você avalia filmes, cria listas, vence quizzes, conclui metas e faz amizades.
            </p>
          </div>
          <button className="goal-deadline-button" onClick={carregar} disabled={carregando}>
            <FiRefreshCw /> Atualizar
          </button>
        </section>

        {erro && <div className="analysis-error">{erro}</div>}

        <section className="level-status">
          <div className="level-emblem" style={{ '--level-color': nivelAtual.cor }}>
            <IconeNivel />
          </div>
          <div className="level-status__body">
            <div className="level-status__title">
              <div>
                <span>Nível atual</span>
                <h2>{nivelAtual.nome}</h2>
              </div>
              <strong>{totalPontos.toLocaleString('pt-BR')} pts</strong>
            </div>
            <div className="level-progress">
              <div style={{ width: `${progressoNivel}%`, background: `linear-gradient(90deg, ${nivelAtual.cor}, ${nivelProximo?.cor || nivelAtual.cor})` }} />
            </div>
            <p>
              {nivelProximo
                ? `Faltam ${faltam.toLocaleString('pt-BR')} pontos para ${nivelProximo.nome}.`
                : 'Você chegou ao maior nível disponível.'}
            </p>
          </div>
        </section>

        <section className="level-summary">
          <article>
            <FiAward />
            <strong>{totalPontos.toLocaleString('pt-BR')}</strong>
            <span>Pontos totais</span>
          </article>
          <article>
            <FiBarChart2 />
            <strong>{historico.length}</strong>
            <span>Atividades pontuadas</span>
          </article>
          <article>
            <FiTrendingUp />
            <strong>{pontosRecentes.toLocaleString('pt-BR')}</strong>
            <span>Pontos nos ultimos 7 dias</span>
          </article>
          <article>
            <FiCheckCircle />
            <strong>{acoesUnicas}</strong>
            <span>Tipos de conquista</span>
          </article>
        </section>

        <section className="level-section">
          <div className="level-section__heading">
            <h2>Como subir de nivel</h2>
            <span>Pontuacao por atividade concluida</span>
          </div>
          <div className="level-rule-grid">
            {Object.entries(ACOES_INFO).map(([acao, info]) => {
              const Icone = info.icone;
              return (
                <article key={acao} style={{ '--rule-color': info.cor }}>
                  <div><Icone /></div>
                  <strong>{info.label}</strong>
                  <p>{info.descricao}</p>
                  <span>+{info.pontos} pts</span>
                </article>
              );
            })}
          </div>
        </section>

        <section className="level-section">
          <div className="level-section__heading">
            <h2>Historico</h2>
            <span>Ultimas atividades que renderam pontos</span>
          </div>

          {carregando ? (
            <div className="recommendations-empty"><FiAward /><p>Carregando progresso...</p></div>
          ) : historico.length === 0 ? (
            <div className="level-empty">
              <FiStar />
              <h2>Nenhum ponto ainda</h2>
              <p>Avalie um filme, crie uma lista ou conclua um quiz para iniciar seu nivel.</p>
            </div>
          ) : (
            <div className="level-history">
              {historico.map(item => {
                const info = infoDaAcao(item.acao);
                const Icone = info.icone;
                return (
                  <article key={item.id} style={{ '--history-color': info.cor }}>
                    <div className="level-history__icon"><Icone /></div>
                    <div>
                      <strong>{info.label}</strong>
                      <span>{formatarData(item.dataRegistro)}</span>
                    </div>
                    <em>+{Number(item.pontos || 0).toLocaleString('pt-BR')} pts</em>
                  </article>
                );
              })}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}
