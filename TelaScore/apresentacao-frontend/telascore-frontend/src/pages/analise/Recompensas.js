import { useState, useEffect } from 'react';
import { FiAward, FiStar, FiZap, FiFilm, FiUsers, FiTarget, FiHelpCircle } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import { recompensaService } from '../../services/api';
import '../analise/analise.css';

const HISTORICO_MOCK = [
    { id: 1, acao: 'AVALIAR_FILME', pontos: 100, dataRegistro: '2026-06-18T10:30:00' },
    { id: 2, acao: 'CONVIDAR_AMIGO', pontos: 300, dataRegistro: '2026-06-17T15:00:00' },
    { id: 3, acao: 'ACERTAR_QUIZ', pontos: 500, dataRegistro: '2026-06-16T09:00:00' },
    { id: 4, acao: 'CRIAR_LISTA', pontos: 100, dataRegistro: '2026-06-15T20:00:00' },
    { id: 5, acao: 'COMPLETAR_META', pontos: 100, dataRegistro: '2026-06-14T11:00:00' },
];

const ACOES_INFO = {
    AVALIAR_FILME:  { label: 'Avaliou um filme',   icone: <FiFilm />,      cor: '#aeb8ff', bg: 'rgba(174,184,255,0.09)' },
    CONVIDAR_AMIGO: { label: 'Convidou um amigo',  icone: <FiUsers />,     cor: '#65dc82', bg: 'rgba(70,211,105,0.09)'  },
    ACERTAR_QUIZ:   { label: 'Acertou no quiz',    icone: <FiHelpCircle />,cor: '#f6c969', bg: 'rgba(245,180,60,0.09)'  },
    CRIAR_LISTA:    { label: 'Criou uma lista',    icone: <FiStar />,      cor: '#ff9f7f', bg: 'rgba(255,130,80,0.09)'  },
    COMPLETAR_META: { label: 'Completou uma meta', icone: <FiTarget />,    cor: '#ff6975', bg: 'rgba(229,9,20,0.09)'    },
};

const NIVEIS = [
    { nome: 'Iniciante',    minPontos: 0,    cor: '#aaaab3' },
    { nome: 'Cinéfilo',     minPontos: 500,  cor: '#aeb8ff' },
    { nome: 'Crítico',      minPontos: 1500, cor: '#65dc82' },
    { nome: 'Especialista', minPontos: 3000, cor: '#f6c969' },
    { nome: 'Lendário',     minPontos: 6000, cor: '#ff6975' },
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

function formatarAcao(acao) {
    return ACOES_INFO[acao] || { label: acao, icone: <FiZap />, cor: '#d0d0d7', bg: 'rgba(255,255,255,0.06)' };
}

function formatarData(valor) {
    if (!valor) return '';
    const data = new Date(valor);
    return new Intl.DateTimeFormat('pt-BR', { day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit' }).format(data);
}

export default function Recompensas() {
    const { sessao } = useAuth();
    const [totalPontos, setTotalPontos] = useState(0);
    const [historico, setHistorico] = useState([]);
    const [carregando, setCarregando] = useState(true);

    useEffect(() => {
        async function carregar() {
            setCarregando(true);
            try {
                const [total, lista] = await Promise.all([
                    recompensaService.consultarTotal(sessao?.id),
                    recompensaService.listarHistorico(sessao?.id),
                ]);
                setTotalPontos(total || 0);
                setHistorico(lista?.length > 0 ? lista : HISTORICO_MOCK);
            } catch (error) {
                console.error('Erro ao carregar recompensas:', error);
                setTotalPontos(1000);
                setHistorico(HISTORICO_MOCK);
            } finally {
                setCarregando(false);
            }
        }
        if (sessao?.id) carregar();
    }, [sessao]);

    const { atual: nivelAtual, proximo: nivelProximo } = calcularNivel(totalPontos);
    const progressoNivel = nivelProximo
        ? Math.min(((totalPontos - nivelAtual.minPontos) / (nivelProximo.minPontos - nivelAtual.minPontos)) * 100, 100)
        : 100;

    return (
        <div className="analysis-page">
            <Navbar />
            <main className="goals-container" style={{ margin: '0 auto' }}>

                {/* Cabeçalho */}
                <div className="goals-heading" style={{ flexDirection: 'column', alignItems: 'flex-start' }}>
                    <p style={{ color: 'var(--brand)', fontSize: '13px', fontWeight: 700, letterSpacing: '0.1em', textTransform: 'uppercase', marginBottom: '8px' }}>
                        Seu Progresso
                    </p>
                    <h2 className="page-title" style={{ fontSize: '32px', margin: '0 0 10px 0' }}>
                        Recompensas
                    </h2>
                    <p className="page-description">
                        Ganhe pontos assistindo filmes, completando metas e interagindo com a comunidade.
                    </p>
                </div>

                {/* Cards de resumo */}
                <div className="goals-summary" style={{ gridTemplateColumns: 'repeat(3, 1fr)', marginTop: '30px' }}>
                    <div className="goals-summary__item goals-summary__item--points">
                        <FiAward size={39} />
                        <div>
                            <strong>{totalPontos.toLocaleString('pt-BR')}</strong>
                            <span>Pontos totais</span>
                        </div>
                    </div>
                    <div className="goals-summary__item">
                        <FiZap size={39} />
                        <div>
                            <strong style={{ color: nivelAtual.cor }}>{nivelAtual.nome}</strong>
                            <span>Nível atual</span>
                        </div>
                    </div>
                    <div className="goals-summary__item">
                        <FiStar size={39} />
                        <div>
                            <strong>{historico.length}</strong>
                            <span>Ações realizadas</span>
                        </div>
                    </div>
                </div>

                {/* Card de nível */}
                <div className="goal-card" style={{ marginTop: '24px', minHeight: 'auto' }}>
                    <div className="goal-card__top">
                        <div className="goal-card__icon" style={{ color: nivelAtual.cor, background: `${nivelAtual.cor}18` }}>
                            <FiAward />
                        </div>
                        <div className="system-goal-pill" style={{ color: nivelAtual.cor }}>
                            {nivelAtual.nome}
                        </div>
                    </div>
                    <div className="goal-card__content">
                        <h2 style={{ margin: '0 0 16px' }}>
                            {nivelProximo ? `Próximo nível: ${nivelProximo.nome}` : 'Nível máximo atingido!'}
                        </h2>
                        <div className="goal-card__progress-label">
                            <span>{totalPontos.toLocaleString('pt-BR')} pts</span>
                            <strong>{nivelProximo ? `${nivelProximo.minPontos.toLocaleString('pt-BR')} pts` : '🏆'}</strong>
                        </div>
                        <div className="goal-card__track">
                            <div style={{ width: `${progressoNivel}%`, background: `linear-gradient(90deg, ${nivelAtual.cor}, ${nivelProximo?.cor || nivelAtual.cor})` }} />
                        </div>
                        {nivelProximo && (
                            <p style={{ color: 'var(--muted)', fontSize: '12px', marginTop: '10px' }}>
                                Faltam <strong style={{ color: 'white' }}>{(nivelProximo.minPontos - totalPontos).toLocaleString('pt-BR')} pontos</strong> para {nivelProximo.nome}
                            </p>
                        )}
                    </div>
                </div>

                {/* Como ganhar pontos */}
                <h3 style={{ fontSize: '18px', margin: '36px 0 16px', color: 'white' }}>Como ganhar pontos</h3>
                <div className="goals-grid">
                    {Object.entries(ACOES_INFO).map(([chave, info]) => (
                        <article key={chave} className="goal-card" style={{ minHeight: 'auto', padding: '20px' }}>
                            <div className="goal-card__top" style={{ marginBottom: '12px' }}>
                                <div className="goal-card__icon" style={{ color: info.cor, background: info.bg }}>
                                    {info.icone}
                                </div>
                            </div>
                            <div className="goal-card__content">
                                <h2 style={{ fontSize: '15px', margin: '0 0 6px' }}>{info.label}</h2>
                                <p style={{ color: 'var(--muted)', fontSize: '12px', margin: 0 }}>
                                    + <strong style={{ color: info.cor }}>
                                        {chave === 'CONVIDAR_AMIGO' ? '300' : chave === 'ACERTAR_QUIZ' ? '500' : '100'} pts
                                    </strong>
                                </p>
                            </div>
                        </article>
                    ))}
                </div>

                {/* Histórico */}
                <h3 style={{ fontSize: '18px', margin: '36px 0 16px', color: 'white' }}>Histórico de pontos</h3>

                {carregando ? (
                    <p style={{ color: 'var(--muted)' }}>Carregando...</p>
                ) : historico.length === 0 ? (
                    <div className="recommendations-empty">
                        <FiAward />
                        <p>Nenhum ponto ganho ainda</p>
                        <span>Comece avaliando filmes, completando metas ou convidando amigos.</span>
                    </div>
                ) : (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        {historico.map(item => {
                            const info = formatarAcao(item.acao);
                            return (
                                <div
                                    key={item.id}
                                    style={{
                                        display: 'flex', alignItems: 'center', gap: '16px',
                                        padding: '16px 20px', borderRadius: '14px',
                                        border: '1px solid var(--line)',
                                        background: 'linear-gradient(145deg, #1b1b22, #101014)',
                                    }}
                                >
                                    <div style={{
                                        display: 'grid', placeItems: 'center',
                                        width: '40px', height: '40px', borderRadius: '11px',
                                        color: info.cor, background: info.bg, flexShrink: 0
                                    }}>
                                        {info.icone}
                                    </div>
                                    <div style={{ flex: 1 }}>
                                        <p style={{ margin: 0, fontSize: '14px', fontWeight: 600 }}>{info.label}</p>
                                        <p style={{ margin: '3px 0 0', fontSize: '12px', color: 'var(--muted)' }}>
                                            {formatarData(item.dataRegistro)}
                                        </p>
                                    </div>
                                    <strong style={{ color: info.cor, fontSize: '16px', flexShrink: 0 }}>
                                        +{item.pontos} pts
                                    </strong>
                                </div>
                            );
                        })}
                    </div>
                )}
            </main>
        </div>
    );
}