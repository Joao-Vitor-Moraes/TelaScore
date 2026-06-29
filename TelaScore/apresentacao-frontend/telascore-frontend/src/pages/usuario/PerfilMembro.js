import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiTarget, FiList, FiArrowLeft, FiAward, FiCheckCircle, FiClock, FiXCircle, FiUserPlus, FiUserX, FiUsers } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { usuarioService, metaService, listaService, amigoService, recompensaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './usuario.css';

const NIVEIS_PERFIL = [
    { nome: 'Explorador', minPontos: 0, cor: '#aeb4c4' },
    { nome: 'Espectador atento', minPontos: 150, cor: '#8fd7ff' },
    { nome: 'Cinefilo', minPontos: 450, cor: '#72e49a' },
    { nome: 'Critico', minPontos: 900, cor: '#f6d66f' },
    { nome: 'Curador', minPontos: 1600, cor: '#ff9f7f' },
    { nome: 'Lenda TelaScore', minPontos: 2600, cor: '#ff6f8a' }
];

export default function PerfilMembro() {
    const { usuarioId } = useParams();
    const { sessao } = useAuth();
    const navigate = useNavigate();

    const [perfil, setPerfil] = useState(null);
    const [metas, setMetas] = useState([]);
    const [listas, setListas] = useState([]);
    const [totalPontos, setTotalPontos] = useState(0);
    const [statusConexao, setStatusConexao] = useState(null);
    const [carregando, setCarregando] = useState(true);
    const [salvandoConexao, setSalvandoConexao] = useState(false);
    const [erro, setErro] = useState('');
    const [erroAcao, setErroAcao] = useState('');

    useEffect(() => {
        async function carregar() {
            setCarregando(true);
            try {
                const carregamentos = [
                    usuarioService.obterPorId(usuarioId),
                    metaService.listarPorUsuario(usuarioId),
                    listaService.listarPorUsuario(usuarioId, sessao.id),
                    recompensaService.consultarTotal(usuarioId).catch(() => 0),
                ];
                if (Number(usuarioId) !== Number(sessao.id)) {
                    carregamentos.push(amigoService.status(sessao.id, usuarioId));
                }
                const [p, m, l, pontos, status] = await Promise.all(carregamentos);
                setPerfil(p);
                setMetas(Array.isArray(m) ? m : []);
                setListas(Array.isArray(l) ? l : []);
                setTotalPontos(Number(pontos) || 0);
                setStatusConexao(status || null);
            } catch {
                setErro('Não foi possível carregar o perfil deste usuário.');
            } finally {
                setCarregando(false);
            }
        }
        carregar();
    }, [usuarioId, sessao.id]);

    async function alternarConexao() {
        setSalvandoConexao(true);
        setErroAcao('');
        try {
            if (statusConexao?.seguindo) {
                await amigoService.deixarDeSeguir(usuarioId, sessao.id);
            } else {
                await amigoService.seguir(sessao.id, usuarioId);
            }
            const novoStatus = await amigoService.status(sessao.id, usuarioId);
            setStatusConexao(novoStatus);
        } catch (e) {
            setErroAcao(e.message || 'Não foi possível atualizar a conexão.');
        } finally {
            setSalvandoConexao(false);
        }
    }

    function iconeStatus(status) {
        if (status === 'CONCLUIDA') return <FiCheckCircle style={{ color: '#65dc82' }} />;
        if (status === 'EM_ANDAMENTO') return <FiClock style={{ color: '#f6c969' }} />;
        return <FiXCircle style={{ color: 'var(--brand)' }} />;
    }

    if (carregando) {
        return (
            <div className="cinema-page">
                <Navbar />
                <main className="cinema-container" style={{ display: 'grid', placeItems: 'center', minHeight: '60vh' }}>
                    <p style={{ color: 'var(--muted)' }}>Carregando perfil...</p>
                </main>
            </div>
        );
    }

    if (erro || !perfil) {
        return (
            <div className="cinema-page">
                <Navbar />
                <main className="cinema-container" style={{ display: 'grid', placeItems: 'center', minHeight: '60vh' }}>
                    <div className="analysis-error">{erro || 'Usuário não encontrado.'}</div>
                </main>
            </div>
        );
    }

    const inicial = (perfil.apelido || perfil.nome || '?')[0].toUpperCase();
    const perfilDoUsuarioLogado = Number(usuarioId) === Number(sessao.id);
    const papelDescricao = perfil.papel === 'ADMIN' ? 'Administrador' : 'Cinéfilo';
    const { atual: nivelAtual, proximo: nivelProximo } = calcularNivelPerfil(totalPontos);
    const progressoNivel = nivelProximo
        ? Math.min(((totalPontos - nivelAtual.minPontos) / (nivelProximo.minPontos - nivelAtual.minPontos)) * 100, 100)
        : 100;

    return (
        <div className="cinema-page">
            <Navbar />
            <main className="cinema-container goals-container">
                <button
                    onClick={() => navigate(-1)}
                    style={{ background: 'transparent', border: 0, color: 'var(--muted)', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '6px', fontSize: '13px', marginBottom: '16px', padding: 0 }}
                >
                    <FiArrowLeft /> Voltar
                </button>

                {erroAcao && <div className="analysis-error" style={{ marginBottom: '16px' }}>{erroAcao}</div>}

                {/* Cabeçalho do perfil */}
                <div className="admin-card" style={{ display: 'flex', alignItems: 'center', gap: '20px', marginBottom: '28px', padding: '24px' }}>
                    <div style={{
                        width: '72px', height: '72px', borderRadius: '50%', flexShrink: 0,
                        background: perfil.avatarUrl ? `url(${perfil.avatarUrl}) center/cover` : 'var(--brand)',
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontSize: '28px', fontWeight: 'bold', color: '#fff'
                    }}>
                        {!perfil.avatarUrl && inicial}
                    </div>
                    <div style={{ flex: 1 }}>
                        <div className="public-profile-name">
                            <h1 style={{ fontSize: '22px', marginBottom: '4px' }}>{perfil.nome}</h1>
                            <span className={`user-badge ${perfil.papel === 'ADMIN' ? 'is-admin' : 'is-user'}`}>
                                {papelDescricao}
                            </span>
                        </div>
                        <p style={{ color: 'var(--brand-light)', fontSize: '14px', marginBottom: '6px' }}>@{perfil.apelido}</p>
                        {perfil.biografia && (
                            <p style={{ color: 'var(--muted)', fontSize: '13px' }}>{perfil.biografia}</p>
                        )}
                    </div>
                    <div style={{ display: 'grid', gap: '10px', justifyItems: 'end', minWidth: '190px' }}>
                        <div style={{
                            width: '100%',
                            padding: '12px',
                            border: '1px solid var(--line)',
                            borderRadius: '8px',
                            background: 'rgba(255,255,255,0.04)'
                        }}>
                            <span style={{ display: 'flex', alignItems: 'center', gap: '6px', color: nivelAtual.cor, fontSize: '12px', fontWeight: 800 }}>
                                <FiAward /> {nivelAtual.nome}
                            </span>
                            <strong style={{ display: 'block', color: 'white', marginTop: '6px', fontSize: '18px' }}>
                                {totalPontos.toLocaleString('pt-BR')} pts
                            </strong>
                            <div style={{ height: '6px', marginTop: '8px', borderRadius: '999px', background: 'rgba(255,255,255,0.08)', overflow: 'hidden' }}>
                                <div style={{ width: `${progressoNivel}%`, height: '100%', background: nivelAtual.cor }} />
                            </div>
                        </div>
                        {!perfilDoUsuarioLogado && (
                            <>
                            {statusConexao?.amigo && (
                                <span className="user-badge is-user" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
                                    <FiUsers /> Amigos
                                </span>
                            )}
                            {statusConexao?.seguidoPor && !statusConexao?.amigo && (
                                <span style={{ color: 'var(--muted)', fontSize: '12px' }}>Segue você</span>
                            )}
                            <button
                                className={statusConexao?.seguindo ? 'btn-secondary' : 'btn-primary'}
                                onClick={alternarConexao}
                                disabled={salvandoConexao}
                            >
                                {statusConexao?.seguindo ? <FiUserX /> : <FiUserPlus />}
                                {salvandoConexao ? 'Atualizando...' : statusConexao?.seguindo ? 'Deixar de seguir' : 'Seguir'}
                            </button>
                            </>
                        )}
                    </div>
                </div>

                <div className="public-profile-grid">
                    {/* Metas */}
                    <section>
                        <div className="goals-heading" style={{ marginBottom: '16px' }}>
                            <div>
                                <p className="page-eyebrow">Progresso</p>
                                <h2 className="page-title" style={{ fontSize: '20px' }}>
                                    <FiTarget style={{ marginRight: '8px' }} />
                                    Metas ({metas.length})
                                </h2>
                            </div>
                        </div>
                        <div style={{ display: 'grid', gap: '10px' }}>
                            {metas.length === 0 && (
                                <div className="recommendations-empty"><FiTarget /><p>Nenhuma meta cadastrada.</p></div>
                            )}
                            {metas.map(meta => {
                                const pct = meta.quantidadeAlvo > 0
                                    ? Math.min(100, Math.round((meta.quantidadeAtual / meta.quantidadeAlvo) * 100))
                                    : 0;
                                return (
                                    <div key={meta.id} className="admin-card" style={{ padding: '14px 16px' }}>
                                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '8px' }}>
                                            <span style={{ fontSize: '13px', fontWeight: 'bold' }}>{meta.titulo}</span>
                                            <span style={{ display: 'flex', alignItems: 'center', gap: '4px', fontSize: '11px', color: 'var(--muted)' }}>
                                                {iconeStatus(meta.status)} {meta.statusDescricao}
                                            </span>
                                        </div>
                                        <div style={{ background: 'rgba(255,255,255,0.08)', borderRadius: '4px', height: '6px', overflow: 'hidden' }}>
                                            <div style={{ width: `${pct}%`, height: '100%', background: meta.status === 'CONCLUIDA' ? '#65dc82' : 'var(--brand)', borderRadius: '4px', transition: 'width 0.3s' }} />
                                        </div>
                                        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '4px', fontSize: '11px', color: 'var(--muted)' }}>
                                            <span>{meta.quantidadeAtual} / {meta.quantidadeAlvo}</span>
                                            <span>{pct}%</span>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </section>

                    {/* Listas públicas */}
                    <section>
                        <div className="goals-heading" style={{ marginBottom: '16px' }}>
                            <div>
                                <p className="page-eyebrow">Coleções</p>
                                <h2 className="page-title" style={{ fontSize: '20px' }}>
                                    <FiList style={{ marginRight: '8px' }} />
                                    Listas ({listas.length})
                                </h2>
                            </div>
                        </div>
                        <div style={{ display: 'grid', gap: '10px' }}>
                            {listas.length === 0 && (
                                <div className="recommendations-empty"><FiList /><p>Nenhuma lista pública.</p></div>
                            )}
                            {listas.map(lista => (
                                <div key={lista.id} className="admin-card" style={{ padding: '14px 16px', cursor: 'pointer' }}
                                    onClick={() => navigate(`/listas/${lista.id}`, { state: { voltarPara: `/usuario/${usuarioId}` } })}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                        <div>
                                            <span style={{ fontSize: '13px', fontWeight: 'bold', display: 'block' }}>{lista.nome}</span>
                                            {lista.descricao && (
                                                <span style={{ fontSize: '11px', color: 'var(--muted)' }}>{lista.descricao}</span>
                                            )}
                                        </div>
                                        {lista.rankeada && (
                                            <span style={{ fontSize: '10px', background: 'rgba(229,9,20,0.2)', color: 'var(--brand)', padding: '2px 8px', borderRadius: '10px' }}>Rankeada</span>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    </section>
                </div>
            </main>
        </div>
    );
}

function calcularNivelPerfil(total) {
    let atual = NIVEIS_PERFIL[0];
    let proximo = NIVEIS_PERFIL[1];
    for (let i = NIVEIS_PERFIL.length - 1; i >= 0; i--) {
        if (total >= NIVEIS_PERFIL[i].minPontos) {
            atual = NIVEIS_PERFIL[i];
            proximo = NIVEIS_PERFIL[i + 1] || null;
            break;
        }
    }
    return { atual, proximo };
}
