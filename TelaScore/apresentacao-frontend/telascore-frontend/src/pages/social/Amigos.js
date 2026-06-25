import { useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiSearch, FiUserCheck, FiUserPlus, FiUserX, FiUsers } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext';
import Navbar from '../../components/Navbar';
import { amigoService } from '../../services/api';
import '../analise/analise.css';

function Avatar({ usuario, size = 44 }) {
    const texto = usuario?.apelido || usuario?.nome || '?';
    if (usuario?.avatarUrl) {
        return (
            <img
                src={usuario.avatarUrl}
                alt=""
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
            {texto.charAt(0).toUpperCase()}
        </div>
    );
}

function normalizarLista(lista) {
    return Array.isArray(lista) ? lista : [];
}

export default function Amigos() {
    const { sessao } = useAuth();
    const navigate = useNavigate();

    const [aba, setAba] = useState('amigos');
    const [amigos, setAmigos] = useState([]);
    const [seguindo, setSeguindo] = useState([]);
    const [seguidores, setSeguidores] = useState([]);
    const [termoBusca, setTermoBusca] = useState('');
    const [resultadosBusca, setResultadosBusca] = useState([]);
    const [buscando, setBuscando] = useState(false);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState('');
    const [acaoId, setAcaoId] = useState(null);

    const seguindoIds = useMemo(() => new Set(seguindo.map(u => u.id)), [seguindo]);
    const seguidoresIds = useMemo(() => new Set(seguidores.map(u => u.id)), [seguidores]);

    const carregarConexoes = useCallback(async () => {
        setCarregando(true);
        setErro('');
        try {
            const [listaAmigos, listaSeguindo, listaSeguidores] = await Promise.all([
                amigoService.listarAmigos(sessao.id),
                amigoService.listarSeguindo(sessao.id),
                amigoService.listarSeguidores(sessao.id),
            ]);
            setAmigos(normalizarLista(listaAmigos));
            setSeguindo(normalizarLista(listaSeguindo));
            setSeguidores(normalizarLista(listaSeguidores));
        } catch (e) {
            setErro(e.message || 'Não foi possível carregar suas conexões.');
            setAmigos([]);
            setSeguindo([]);
            setSeguidores([]);
        } finally {
            setCarregando(false);
        }
    }, [sessao?.id]);

    useEffect(() => {
        if (sessao?.id) carregarConexoes();
    }, [sessao?.id, carregarConexoes]);

    async function buscarUsuarios() {
        const termo = termoBusca.trim();
        if (termo.length < 2) {
            setResultadosBusca([]);
            return;
        }

        setBuscando(true);
        setErro('');
        try {
            const resultado = await amigoService.buscarPorApelido(termo);
            setResultadosBusca(normalizarLista(resultado).filter(u => u.id !== sessao?.id));
        } catch (e) {
            setErro(e.message || 'Não foi possível buscar usuários.');
            setResultadosBusca([]);
        } finally {
            setBuscando(false);
        }
    }

    async function seguir(usuarioId) {
        setAcaoId(usuarioId);
        setErro('');
        try {
            await amigoService.seguir(sessao.id, usuarioId);
            await carregarConexoes();
        } catch (e) {
            setErro(e.message || 'Não foi possível seguir este usuário.');
        } finally {
            setAcaoId(null);
        }
    }

    async function deixarDeSeguir(usuarioId) {
        setAcaoId(usuarioId);
        setErro('');
        try {
            await amigoService.deixarDeSeguir(usuarioId, sessao.id);
            await carregarConexoes();
        } catch (e) {
            setErro(e.message || 'Não foi possível deixar de seguir este usuário.');
        } finally {
            setAcaoId(null);
        }
    }

    function listaDaAba() {
        if (aba === 'seguindo') return seguindo;
        if (aba === 'seguidores') return seguidores;
        return amigos;
    }

    const abas = [
        { id: 'amigos', label: 'Amigos', icone: <FiUsers size={16} />, contagem: amigos.length },
        { id: 'seguindo', label: 'Seguindo', icone: <FiUserCheck size={16} />, contagem: seguindo.length },
        { id: 'seguidores', label: 'Seguidores', icone: <FiUsers size={16} />, contagem: seguidores.length },
        { id: 'buscar', label: 'Adicionar', icone: <FiUserPlus size={16} />, contagem: null },
    ];

    return (
        <div className="analysis-page">
            <Navbar />
            <main className="goals-container" style={{ margin: '0 auto', paddingTop: '28px', paddingLeft: 0, paddingRight: '24px' }}>
                <div className="goals-heading" style={{ flexDirection: 'column', alignItems: 'flex-start', marginBottom: '5px', gap: 0 }}>
                    <p style={{ color: 'var(--brand)', fontSize: '13px', fontWeight: 400, letterSpacing: '0.1em', textTransform: 'uppercase', marginBottom: '6px' }}>
                        Suas Conexoes
                    </p>
                    <h2 className="page-title" style={{ fontSize: '32px', margin: 0 }}>
                        Amigos
                    </h2>
                    <p className="page-description">
                        Siga outros cinefilos. Quando os dois se seguem, a conexao aparece como amizade.
                    </p>
                </div>

                {erro && <div className="analysis-error" style={{ marginTop: '18px' }}>{erro}</div>}

                <div style={{ display: 'flex', gap: '10px', marginTop: '30px', marginBottom: '30px', borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
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
                                marginBottom: '-1px'
                            }}
                        >
                            {a.icone}
                            {a.label}
                            {a.contagem > 0 && (
                                <span style={{ background: aba === a.id ? 'var(--brand)' : 'rgba(255,255,255,0.1)', color: 'white', borderRadius: '20px', padding: '1px 8px', fontSize: '12px' }}>
                                    {a.contagem}
                                </span>
                            )}
                        </button>
                    ))}
                </div>

                {aba !== 'buscar' && (
                    carregando ? (
                        <p style={{ color: 'var(--muted)' }}>Carregando...</p>
                    ) : listaDaAba().length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '60px 20px', color: 'var(--muted)' }}>
                            <FiUsers size={48} style={{ marginBottom: '16px', opacity: 0.4 }} />
                            <p style={{ fontSize: '16px', marginBottom: '8px' }}>Nenhuma conexao nesta lista.</p>
                        </div>
                    ) : (
                        <div className="goals-grid">
                            {listaDaAba().map(usuario => (
                                <UsuarioCard
                                    key={usuario.id}
                                    usuario={usuario}
                                    seguindo={seguindoIds.has(usuario.id)}
                                    seguidoPor={seguidoresIds.has(usuario.id)}
                                    acaoId={acaoId}
                                    onAbrir={() => navigate(`/usuario/${usuario.id}`)}
                                    onSeguir={() => seguir(usuario.id)}
                                    onDeixarDeSeguir={() => deixarDeSeguir(usuario.id)}
                                />
                            ))}
                        </div>
                    )
                )}

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
                                disabled={buscando}
                            >
                                {buscando ? 'Buscando...' : <><FiSearch size={16} /> Buscar</>}
                            </button>
                        </div>

                        {resultadosBusca.length > 0 && (
                            <div className="goals-grid">
                                {resultadosBusca.map(usuario => (
                                    <UsuarioCard
                                        key={usuario.id}
                                        usuario={usuario}
                                        seguindo={seguindoIds.has(usuario.id)}
                                        seguidoPor={seguidoresIds.has(usuario.id)}
                                        acaoId={acaoId}
                                        onAbrir={() => navigate(`/usuario/${usuario.id}`)}
                                        onSeguir={() => seguir(usuario.id)}
                                        onDeixarDeSeguir={() => deixarDeSeguir(usuario.id)}
                                    />
                                ))}
                            </div>
                        )}

                        {resultadosBusca.length === 0 && termoBusca.trim().length >= 2 && !buscando && (
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

function UsuarioCard({ usuario, seguindo, seguidoPor, acaoId, onAbrir, onSeguir, onDeixarDeSeguir }) {
    const apelido = usuario.apelido || usuario.nome || `user-${usuario.id}`;
    const carregandoAcao = acaoId === usuario.id;

    return (
        <article className="goal-card">
            <div className="goal-card__top">
                <button onClick={onAbrir} style={{ background: 'transparent', border: 0, padding: 0, cursor: 'pointer' }}>
                    <Avatar usuario={usuario} />
                </button>
                <div className="system-goal-pill">
                    {seguindo && seguidoPor ? <><FiUsers size={12} /> Amigo</> : seguidoPor ? 'Segue voce' : seguindo ? 'Seguindo' : 'Perfil'}
                </div>
            </div>
            <div className="goal-card__content">
                <h2 style={{ cursor: 'pointer' }} onClick={onAbrir}>@{apelido}</h2>
                {usuario.nome && <p style={{ color: 'var(--muted)', fontSize: '13px', marginTop: '4px' }}>{usuario.nome}</p>}
            </div>
            <div className="goal-card__actions" style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                <button
                    className="goal-deadline-button"
                    style={{ flex: 1, justifyContent: 'center', padding: '10px' }}
                    onClick={onAbrir}
                >
                    Ver perfil
                </button>
                {seguindo ? (
                    <button
                        className="goal-deadline-button"
                        style={{ flex: 1, justifyContent: 'center', padding: '10px', color: '#ff6975', border: '1px solid rgba(255,105,117,0.3)', background: 'transparent' }}
                        onClick={onDeixarDeSeguir}
                        disabled={carregandoAcao}
                    >
                        <FiUserX size={16} /> {carregandoAcao ? '...' : 'Deixar'}
                    </button>
                ) : (
                    <button
                        className="goal-deadline-button"
                        style={{ flex: 1, justifyContent: 'center', padding: '10px', background: 'var(--brand)', color: 'white' }}
                        onClick={onSeguir}
                        disabled={carregandoAcao}
                    >
                        <FiUserPlus size={16} /> {carregandoAcao ? '...' : 'Seguir'}
                    </button>
                )}
            </div>
        </article>
    );
}
