import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FiUser, FiTarget, FiList, FiArrowLeft, FiCheckCircle, FiClock, FiXCircle } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { usuarioService, metaService, listaService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function PerfilMembro() {
    const { usuarioId } = useParams();
    const { sessao } = useAuth();
    const navigate = useNavigate();

    const [perfil, setPerfil] = useState(null);
    const [metas, setMetas] = useState([]);
    const [listas, setListas] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState('');

    useEffect(() => {
        async function carregar() {
            setCarregando(true);
            try {
                const [p, m, l] = await Promise.all([
                    usuarioService.obterPorId(usuarioId),
                    metaService.listarPorUsuario(usuarioId),
                    listaService.listarPorUsuario(usuarioId, sessao.id),
                ]);
                setPerfil(p);
                setMetas(Array.isArray(m) ? m : []);
                setListas(Array.isArray(l) ? l : []);
            } catch {
                setErro('Não foi possível carregar o perfil deste usuário.');
            } finally {
                setCarregando(false);
            }
        }
        carregar();
    }, [usuarioId, sessao.id]);

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
                    <div>
                        <h1 style={{ fontSize: '22px', marginBottom: '4px' }}>{perfil.nome}</h1>
                        <p style={{ color: 'var(--brand-light)', fontSize: '14px', marginBottom: '6px' }}>@{perfil.apelido}</p>
                        {perfil.biografia && (
                            <p style={{ color: 'var(--muted)', fontSize: '13px' }}>{perfil.biografia}</p>
                        )}
                    </div>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px', alignItems: 'start' }}>
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
                                    onClick={() => navigate(`/listas/${lista.id}`)}>
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
