import { useState, useEffect } from 'react';
import { FiUserPlus, FiUserCheck, FiUserX, FiSearch, FiUsers, FiClock } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import { amigoService } from '../../services/api';
import '../analise/analise.css';

// MOCK para quando o servidor estiver offline
const AMIGOS_MOCK = [
    { id: 1, amigoId: 2, apelido: 'luisa', urlImagem: null, status: 'ACEITO' },
    { id: 2, amigoId: 3, apelido: 'felipe', urlImagem: null, status: 'ACEITO' },
];

const SOLICITACOES_MOCK = [
    { id: 3, remetenteId: 4, apelido: 'maria_cinefila', urlImagem: null, status: 'PENDENTE' },
];

function Avatar({ apelido, urlImagem, size = 44 }) {
    if (urlImagem) {
        return (
            <img
                src={urlImagem}
                alt={apelido}
                style={{ width: size, height: size, borderRadius: '50%', objectFit: 'cover', flexShrink: 0 }}
            />
        );
    }
    return (
        <div style={{
            width: size, height: size, borderRadius: '50%', background: 'var(--brand)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontWeight: 700, fontSize: size * 0.4, color: 'white', flexShrink: 0
        }}>
            {apelido?.charAt(0).toUpperCase()}
        </div>
    );
}

export default function Amigos() {
    const { sessao } = useAuth();

    const [aba, setAba] = useState('amigos'); // 'amigos' | 'solicitacoes' | 'buscar'
    const [amigos, setAmigos] = useState([]);
    const [solicitacoes, setSolicitacoes] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [resultadosBusca, setResultadosBusca] = useState([]);
    const [buscando, setBuscando] = useState(false);
    const [carregando, setCarregando] = useState(true);

    useEffect(() => {
        async function carregar() {
            setCarregando(true);
            try {
                const [listaAmigos, listaSolicitacoes] = await Promise.all([
                    amigoService.listarAmigos(sessao?.id),
                    amigoService.listarSolicitacoesPendentes(sessao?.id),
                ]);
                setAmigos(listaAmigos?.length > 0 ? listaAmigos : AMIGOS_MOCK);
                setSolicitacoes(listaSolicitacoes?.length > 0 ? listaSolicitacoes : SOLICITACOES_MOCK);
            } catch (error) {
                console.error('Erro ao carregar amigos:', error);
                setAmigos(AMIGOS_MOCK);
                setSolicitacoes(SOLICITACOES_MOCK);
            } finally {
                setCarregando(false);
            }
        }
        if (sessao?.id) carregar();
    }, [sessao]);

    const buscarUsuarios = async () => {
        if (!termoBusca.trim()) return;
        setBuscando(true);
        try {
            const resultado = await amigoService.buscarPorApelido(termoBusca.trim());
            setResultadosBusca(resultado || []);
        } catch (error) {
            console.error('Erro ao buscar:', error);
            setResultadosBusca([]);
        } finally {
            setBuscando(false);
        }
    };

    const enviarSolicitacao = async (destinatarioId) => {
        try {
            await amigoService.enviarSolicitacao({ remetenteId: sessao?.id, destinatarioId });
            setResultadosBusca(prev =>
                prev.map(u => u.id === destinatarioId ? { ...u, solicitacaoEnviada: true } : u)
            );
        } catch (error) {
            console.error('Erro ao enviar solicitação:', error);
            alert('Não foi possível enviar a solicitação.');
        }
    };

    const aceitarSolicitacao = async (solicitacaoId, remetenteId) => {
        try {
            await amigoService.aceitarSolicitacao(solicitacaoId, { usuarioId: sessao?.id });
            setSolicitacoes(prev => prev.filter(s => s.id !== solicitacaoId));
            setAmigos(prev => [...prev, { id: solicitacaoId, amigoId: remetenteId, apelido: solicitacoes.find(s => s.id === solicitacaoId)?.apelido, status: 'ACEITO' }]);
        } catch (error) {
            console.error('Erro ao aceitar solicitação:', error);
            alert('Não foi possível aceitar a solicitação.');
        }
    };

    const recusarSolicitacao = async (solicitacaoId) => {
        try {
            await amigoService.recusarSolicitacao(solicitacaoId, { usuarioId: sessao?.id });
            setSolicitacoes(prev => prev.filter(s => s.id !== solicitacaoId));
        } catch (error) {
            console.error('Erro ao recusar solicitação:', error);
            alert('Não foi possível recusar a solicitação.');
        }
    };

    const removerAmigo = async (amigoId) => {
        if (!window.confirm('Tem certeza que deseja remover este amigo?')) return;
        try {
            await amigoService.removerAmigo(sessao?.id, amigoId);
            setAmigos(prev => prev.filter(a => a.amigoId !== amigoId));
        } catch (error) {
            console.error('Erro ao remover amigo:', error);
            alert('Não foi possível remover o amigo.');
        }
    };

    const abas = [
        { id: 'amigos', label: 'Meus Amigos', icone: <FiUsers size={16} />, contagem: amigos.length },
        { id: 'solicitacoes', label: 'Solicitações', icone: <FiClock size={16} />, contagem: solicitacoes.length },
        { id: 'buscar', label: 'Adicionar', icone: <FiUserPlus size={16} />, contagem: null },
    ];

    return (
        <div className="analysis-page">
            <Navbar />
            <main className="goals-container" style={{ margin: '0 auto', paddingTop: '28px', paddingLeft: '0px', paddingRight: '24px' }}>

                {/* Cabeçalho */}
                <div className="goals-heading" style={{ flexDirection: 'column', alignItems: 'flex-start', marginBottom: '5px', gap: '0' }}>
                    <p style={{ color: 'var(--brand)', fontSize: '13px', fontWeight: 400, letterSpacing: '0.1em', textTransform: 'uppercase', marginBottom: '6px' }}>
                        Suas Conexões
                    </p>
                    <h2 className="page-title" style={{ fontSize: '32px', margin: '0 0 0px 0' }}>
                        Amigos
                    </h2>
                    <p className="page-description">
                        Conecte-se com outros cinéfilos, acompanhe avaliações e compartilhe recomendações.
                    </p>
                </div>

                {/* Abas */}
                <div style={{ display: 'flex', gap: '10px', marginTop: '30px', marginBottom: '30px', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '0' }}>
                    {abas.map(a => (
                        <button
                            key={a.id}
                            onClick={() => setAba(a.id)}
                            style={{
                                display: 'flex', alignItems: 'center', gap: '8px',
                                padding: '10px 20px', background: 'transparent', border: 'none',
                                color: aba === a.id ? 'var(--brand)' : 'var(--muted)',
                                borderBottom: aba === a.id ? '2px solid var(--brand)' : '2px solid transparent',
                                cursor: 'pointer', fontSize: '14px', fontWeight: aba === a.id ? 600 : 400,
                                marginBottom: '-1px', transition: 'color 0.2s'
                            }}
                        >
                            {a.icone}
                            {a.label}
                            {a.contagem > 0 && (
                                <span style={{
                                    background: aba === a.id ? 'var(--brand)' : 'rgba(255,255,255,0.1)',
                                    color: 'white', borderRadius: '20px', padding: '1px 8px', fontSize: '12px'
                                }}>
                                    {a.contagem}
                                </span>
                            )}
                        </button>
                    ))}
                </div>

                {/* ABA: MEUS AMIGOS */}
                {aba === 'amigos' && (
                    <>
                        {carregando ? (
                            <p style={{ color: 'var(--muted)' }}>Carregando...</p>
                        ) : amigos.length === 0 ? (
                            <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--muted)' }}>
                                <FiUsers size={48} style={{ marginBottom: '16px', opacity: 0.4 }} />
                                <p style={{ fontSize: '16px', marginBottom: '8px' }}>Você ainda não tem amigos adicionados.</p>
                                <p style={{ fontSize: '14px' }}>
                                    Use a aba <strong style={{ color: 'white' }}>Adicionar</strong> para encontrar pessoas.
                                </p>
                            </div>
                        ) : (
                            <div className="goals-grid">
                                {amigos.map(amigo => (
                                    <article key={amigo.id} className="goal-card">
                                        <div className="goal-card__top">
                                            <Avatar apelido={amigo.apelido} urlImagem={amigo.urlImagem} />
                                            <div className="system-goal-pill">
                                                <FiUserCheck size={12} /> Amigo
                                            </div>
                                        </div>
                                        <div className="goal-card__content">
                                            <h2>@{amigo.apelido}</h2>
                                        </div>
                                        <div className="goal-card__actions" style={{ marginTop: '10px' }}>
                                            <button
                                                className="goal-deadline-button"
                                                style={{ width: '100%', justifyContent: 'center', padding: '10px', color: '#ff6975', border: '1px solid rgba(255,105,117,0.3)', background: 'transparent' }}
                                                onClick={() => removerAmigo(amigo.amigoId)}
                                            >
                                                <FiUserX size={16} /> Remover
                                            </button>
                                        </div>
                                    </article>
                                ))}
                            </div>
                        )}
                    </>
                )}

                {/* ABA: SOLICITAÇÕES */}
                {aba === 'solicitacoes' && (
                    <>
                        {solicitacoes.length === 0 ? (
                            <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--muted)' }}>
                                <FiClock size={48} style={{ marginBottom: '16px', opacity: 0.4 }} />
                                <p style={{ fontSize: '16px' }}>Nenhuma solicitação pendente.</p>
                            </div>
                        ) : (
                            <div className="goals-grid">
                                {solicitacoes.map(s => (
                                    <article key={s.id} className="goal-card">
                                        <div className="goal-card__top">
                                            <Avatar apelido={s.apelido} urlImagem={s.urlImagem} />
                                            <div className="system-goal-pill">
                                                <FiClock size={12} /> Pendente
                                            </div>
                                        </div>
                                        <div className="goal-card__content">
                                            <h2>@{s.apelido}</h2>
                                            <p style={{ color: 'var(--muted)', fontSize: '13px', marginTop: '4px' }}>
                                                Quer se conectar com você
                                            </p>
                                        </div>
                                        <div className="goal-card__actions" style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                                            <button
                                                className="goal-deadline-button"
                                                style={{ flex: 1, justifyContent: 'center', padding: '10px', background: 'var(--brand)', color: 'white' }}
                                                onClick={() => aceitarSolicitacao(s.id, s.remetenteId)}
                                            >
                                                <FiUserCheck size={16} /> Aceitar
                                            </button>
                                            <button
                                                className="goal-deadline-button"
                                                style={{ flex: 1, justifyContent: 'center', padding: '10px', background: 'transparent', color: '#ff6975', border: '1px solid rgba(255,105,117,0.3)' }}
                                                onClick={() => recusarSolicitacao(s.id)}
                                            >
                                                <FiUserX size={16} /> Recusar
                                            </button>
                                        </div>
                                    </article>
                                ))}
                            </div>
                        )}
                    </>
                )}

                {/* ABA: BUSCAR */}
                {aba === 'buscar' && (
                    <>
                        <div style={{ display: 'flex', gap: '12px', marginBottom: '30px' }}>
                            <input
                                type="text"
                                placeholder="Buscar por apelido..."
                                value={termoBusca}
                                onChange={(e) => setTermoBusca(e.target.value)}
                                onKeyDown={(e) => e.key === 'Enter' && buscarUsuarios()}
                                style={{
                                    flex: 1, padding: '12px 16px', borderRadius: '8px',
                                    border: '1px solid rgba(255,255,255,0.1)',
                                    background: 'rgba(255,255,255,0.05)', color: 'white', fontSize: '15px'
                                }}
                            />
                            <button
                                className="goal-deadline-button"
                                style={{ background: 'var(--brand)', color: 'white', padding: '12px 24px' }}
                                onClick={buscarUsuarios}
                            >
                                {buscando ? '...' : <><FiSearch size={16} /> Buscar</>}
                            </button>
                        </div>

                        {resultadosBusca.length > 0 && (
                            <div className="goals-grid">
                                {resultadosBusca
                                    .filter(u => u.id !== sessao?.id)
                                    .map(u => (
                                        <article key={u.id} className="goal-card">
                                            <div className="goal-card__top">
                                                <Avatar apelido={u.apelido} urlImagem={u.urlImagem} />
                                            </div>
                                            <div className="goal-card__content">
                                                <h2>@{u.apelido}</h2>
                                            </div>
                                            <div className="goal-card__actions" style={{ marginTop: '10px' }}>
                                                {u.solicitacaoEnviada ? (
                                                    <button
                                                        className="goal-deadline-button"
                                                        disabled
                                                        style={{ width: '100%', justifyContent: 'center', padding: '10px', opacity: 0.5 }}
                                                    >
                                                        <FiClock size={16} /> Solicitação enviada
                                                    </button>
                                                ) : (
                                                    <button
                                                        className="goal-deadline-button"
                                                        style={{ width: '100%', justifyContent: 'center', padding: '10px', background: 'var(--brand)', color: 'white' }}
                                                        onClick={() => enviarSolicitacao(u.id)}
                                                    >
                                                        <FiUserPlus size={16} /> Adicionar amigo
                                                    </button>
                                                )}
                                            </div>
                                        </article>
                                    ))}
                            </div>
                        )}

                        {resultadosBusca.length === 0 && termoBusca && !buscando && (
                            <div style={{ textAlign: 'center', padding: '40px', color: 'var(--muted)' }}>
                                <p>Nenhum usuário encontrado para "<strong style={{ color: 'white' }}>{termoBusca}</strong>".</p>
                            </div>
                        )}
                    </>
                )}
            </main>
        </div>
    );
}