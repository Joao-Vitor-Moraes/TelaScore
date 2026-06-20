import { useCallback, useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
    FiUsers, FiPlus, FiSearch, FiMessageSquare, FiX, FiLogIn,
    FiLogOut, FiSend, FiTrash2, FiShield, FiUserX, FiUserCheck, FiUser
} from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { comunidadeService, usuarioService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import './comunidades.css';

export default function Comunidades() {
    const { sessao } = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const [comunidades, setComunidades] = useState([]);
    const [minhasComunidades, setMinhasComunidades] = useState([]);
    const [usuariosMap, setUsuariosMap] = useState({});
    const [meuPerfil, setMeuPerfil] = useState(null);
    const [aba, setAba] = useState('todas');
    const [busca, setBusca] = useState('');

    const [comunidadeAtiva, setComunidadeAtiva] = useState(null);
    const [mensagens, setMensagens] = useState([]);
    const [novaMensagem, setNovaMensagem] = useState('');
    const [membros, setMembros] = useState([]);
    const [menuPerfilAberto, setMenuPerfilAberto] = useState('');

    const [modalCriarAberto, setModalCriarAberto] = useState(false);
    const [formCriar, setFormCriar] = useState({ nome: '', descricao: '' });

    const [erro, setErro] = useState('');
    const [feedback, setFeedback] = useState('');
    const [carregando, setCarregando] = useState(false);

    const extrairIdString = (alvo) => {
        if (!alvo) return '';
        if (typeof alvo === 'object') {
            return String(alvo.value || alvo.id || '');
        }
        return String(alvo);
    };

    const carregarDados = useCallback(async () => {
        try {
            setErro('');
            const [todas, doUsuario, listaUsers, dadosMeuUsuario] = await Promise.all([
                comunidadeService.listarTodas(),
                comunidadeService.buscarComunidadesDoUsuario(sessao.id),
                usuarioService.listar().catch(() => []),
                usuarioService.meuUsuario().catch(() => null)
            ]);

            setComunidades(Array.isArray(todas) ? todas : []);
            setMinhasComunidades(Array.isArray(doUsuario) ? doUsuario : []);
            setMeuPerfil(dadosMeuUsuario);
            const comunidadeRecomendada = (Array.isArray(todas) ? todas : [])
                .find(item => Number(extrairIdString(item.id)) === Number(location.state?.conteudoRecomendadoId));
            if (comunidadeRecomendada) setComunidadeAtiva(comunidadeRecomendada);

            let usersArray = [];
            if (Array.isArray(listaUsers)) {
                usersArray = listaUsers;
            } else if (listaUsers) {
                if (Array.isArray(listaUsers.content)) usersArray = listaUsers.content;
                else if (Array.isArray(listaUsers.data)) usersArray = listaUsers.data;
                else if (Array.isArray(listaUsers.usuarios)) usersArray = listaUsers.usuarios;
            }

            const mapa = {};
            usersArray.forEach(u => {
                const uId = extrairIdString(u.id) || extrairIdString(u.usuarioId);
                if (uId) {
                    mapa[uId] = u.apelido || u.username || u.nome;
                }
            });
            setUsuariosMap(mapa);
        } catch (e) {
            setErro('Erro ao carregar dados do ecossistema de comunidades.');
        }
    }, [location.state?.conteudoRecomendadoId, sessao.id]);

    useEffect(() => {
        carregarDados();
    }, [carregarDados]);

    useEffect(() => {
        if (!comunidadeAtiva) return;
        const cIdStr = extrairIdString(comunidadeAtiva.id);
        const buscarDadosPeriodicamente = async () => {
            try {
                const [msg, memb] = await Promise.all([
                    comunidadeService.listarMensagens(cIdStr),
                    comunidadeService.listarMembros(cIdStr)
                ]);
                setMensagens(msg || []);
                setMembros(memb || []);
            } catch (e) {
            }
        };
        const intervalo = setInterval(buscarDadosPeriodicamente, 3000);
        return () => clearInterval(intervalo);
    }, [comunidadeAtiva]);

    const minhasComunidadesIds = useMemo(() => {
        return minhasComunidades.map(m => extrairIdString(m.id));
    }, [minhasComunidades]);

    const comunidadesFiltradas = useMemo(() => {
        const listOrigem = aba === 'todas'
            ? comunidades
            : comunidades.filter(c => minhasComunidadesIds.includes(extrairIdString(c.id)));

        const termo = busca.trim().toLowerCase();
        if (!termo) return listOrigem;
        return listOrigem.filter(c =>
            c.nome.toLowerCase().includes(termo) || c.descricao?.toLowerCase().includes(termo)
        );
    }, [aba, comunidades, minhasComunidadesIds, busca]);

    const carregarDetalhesComunidade = useCallback(async (comunidade) => {
        try {
            setComunidadeAtiva(comunidade);
            const msg = await comunidadeService.listarMensagens(extrairIdString(comunidade.id));
            setMensagens(msg || []);
            const memb = await comunidadeService.listarMembros(extrairIdString(comunidade.id));
            setMembros(memb || []);
        } catch (e) {
            setErro('Não foi possível carregar os detalhes desta comunidade.');
        }
    }, []);

    async function handleCriarComunidade(e) {
        e.preventDefault();
        setCarregando(true);
        try {
            const idSugerido = Math.floor(Math.random() * 10000);
            await comunidadeService.criar({
                idSugerido,
                nome: formCriar.nome,
                descricao: formCriar.descricao,
                criadorId: sessao.id
            });
            setFormCriar({ nome: '', descricao: '' });
            setModalCriarAberto(false);
            setFeedback('Comunidade fundada com sucesso!');
            await carregarDados();
        } catch (e) {
            setErro(e.message);
        } finally {
            setCarregando(false);
        }
    }

    async function handleEntrarComunidade(comunidadeId) {
        const cIdStr = extrairIdString(comunidadeId);
        try {
            setErro('');
            await comunidadeService.entrar(cIdStr, { usuarioId: sessao.id });
            setFeedback('Você entrou na comunidade!');
            await carregarDados();
            if (comunidadeAtiva && extrairIdString(comunidadeAtiva.id) === cIdStr) {
                const memb = await comunidadeService.listarMembros(cIdStr);
                setMembros(memb || []);
            }
        } catch (e) {
            const dadosErro = e.response?.data;

            if (typeof dadosErro === 'string') {
                setErro(dadosErro);
            } else if (dadosErro?.message) {
                setErro(dadosErro.message);
            } else if (dadosErro?.error) {
                setErro(dadosErro.error);
            } else {
                setErro('Acesso negado: Limite de requisições atingido. Aguarde um minuto.');
            }
        }
    }

    async function handleEnviarMensagem(e) {
        e.preventDefault();
        if (!novaMensagem.trim() || !comunidadeAtiva) return;
        const cIdStr = extrairIdString(comunidadeAtiva.id);
        try {
            await comunidadeService.enviarMensagem({
                comunidadeId: cIdStr,
                usuarioId: sessao.id,
                conteudo: novaMensagem
            });
            setNovaMensagem('');
            const msg = await comunidadeService.listarMensagens(cIdStr);
            setMensagens(msg || []);
        } catch (e) {
            setErro('Falha ao enviar mensagem.');
        }
    }

    async function handleRemoverMembro(usuarioId) {
        if (!comunidadeAtiva) return;
        const cIdStr = extrairIdString(comunidadeAtiva.id);
        const uIdStr = extrairIdString(usuarioId);
        try {
            await comunidadeService.removerMembro(cIdStr, uIdStr, sessao.id);
            setFeedback('Membro de sala atualizado.');
            const memb = await comunidadeService.listarMembros(cIdStr);
            setMembros(memb || []);
            if (uIdStr === extrairIdString(sessao.id)) {
                setComunidadeAtiva(null);
                await carregarDados();
            }
        } catch (e) {
            setErro('Ação de moderação negada.');
        }
    }

    async function handleMudarPapel(usuarioId, acao) {
        if (!comunidadeAtiva) return;
        const cIdStr = extrairIdString(comunidadeAtiva.id);
        const uIdStr = extrairIdString(usuarioId);
        try {
            if (acao === 'promover') {
                await comunidadeService.promoverMembro(cIdStr, uIdStr, sessao.id);
            } else {
                await comunidadeService.rebaixarMembro(cIdStr, uIdStr, sessao.id);
            }
            const memb = await comunidadeService.listarMembros(cIdStr);
            setMembros(memb || []);
        } catch (e) {
            setErro('Permissão insuficiente.');
        }
    }

    async function handleExcluirComunidade(comunidadeId) {
        if (!window.confirm('Tem certeza que deseja apagar esta comunidade permanentemente?')) return;
        try {
            await comunidadeService.excluirComunidade(extrairIdString(comunidadeId), sessao.id);
            setComunidadeAtiva(null);
            setFeedback('Comunidade encerrada.');
            await carregarDados();
        } catch (e) {
            setErro('Apenas o criador pode apagar a comunidade.');
        }
    }

    const papelNaAtiva = useMemo(() => {
        if (!comunidadeAtiva) return null;
        return membros.find(m => extrairIdString(m.usuarioId) === extrairIdString(sessao.id))?.papel || null;
    }, [comunidadeAtiva, membros, sessao.id]);

    if (!sessao) {
        return (
            <div className="cinema-page" style={{ display: 'grid', placeItems: 'center', minHeight: '100vh' }}>
                <div className="recommendations-empty">
                    <p>Autenticando usuário...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="cinema-page">
            <Navbar />
            <main className="cinema-container goals-container">
                <div className="goals-heading">
                    <div>
                        <p className="page-eyebrow">Rede Social</p>
                        <h1 className="page-title">Comunidades</h1>
                        <p className="page-description">Encontre cinéfilos, compartilhar opiniões, monte debates e viva o cinema juntos.</p>
                    </div>
                    <button className="btn-primary goals-new-button" onClick={() => setModalCriarAberto(true)}>
                        <FiPlus /> Nova Comunidade
                    </button>
                </div>

                <section className="recommendations-tabs" role="tablist">
                    <button className={aba === 'todas' ? 'is-active' : ''} onClick={() => setAba('todas')}>
                        <FiUsers /> Todas as Comunidades ({comunidades.length})
                    </button>
                    <button className={aba === 'minhas' ? 'is-active' : ''} onClick={() => setAba('minhas')}>
                        <FiMessageSquare /> Minhas Comunidades ({minhasComunidades.length})
                    </button>
                </section>

                <div className="recommendations-filters">
                    <label>
                        <FiSearch />
                        <input value={busca} onChange={e => setBusca(e.target.value)} placeholder="Filtrar salas por título ou tema..." />
                        {busca && <button onClick={() => setBusca('')}><FiX /></button>}
                    </label>
                </div>

                {erro && <div className="analysis-error">{erro}</div>}
                {feedback && <div className="analysis-feedback"><FiUserCheck /> {feedback}</div>}

                <div className="goals-grid" style={{ gridTemplateColumns: comunidadeAtiva ? '1fr 1.2fr' : 'repeat(auto-fit, minmax(320px, 1fr))' }}>

                    <div className="admin-list" style={{ display: 'grid', gap: '14px', alignContent: 'start' }}>
                        {comunidadesFiltradas.map(c => {
                            const cIdStr = extrairIdString(c.id);
                            const participo = minhasComunidadesIds.includes(cIdStr);
                            const ativa = comunidadeAtiva && extrairIdString(comunidadeAtiva.id) === cIdStr;
                            return (
                                <article key={cIdStr} className="admin-card" style={{ borderColor: ativa ? 'var(--brand)' : '' }}>
                                    <div className="admin-card__main" style={{ gridTemplateColumns: '1fr auto' }}>
                                        <div className="admin-card__body">
                                            <h3 style={{ cursor: 'pointer' }} onClick={() => carregarDetalhesComunidade(c)}>{c.nome}</h3>
                                            <p className="admin-description">{c.descricao || 'Sem descrição cadastrada.'}</p>
                                        </div>
                                        <div className="admin-actions">
                                            {participo ? (
                                                <button className="goal-deadline-button" onClick={() => carregarDetalhesComunidade(c)}>
                                                    <FiMessageSquare /> Entrar no Chat
                                                </button>
                                            ) : (
                                                <button className="btn-primary" style={{ padding: '6px 12px', minHeight: '34px' }} onClick={() => handleEntrarComunidade(c.id)}>
                                                    <FiLogIn /> Participar
                                                </button>
                                            )}
                                        </div>
                                    </div>
                                </article>
                            );
                        })}
                        {comunidadesFiltradas.length === 0 && (
                            <div className="recommendations-empty"><FiUsers /><p>Nenhuma comunidade por aqui.</p></div>
                        )}
                    </div>

                    {comunidadeAtiva && (
                        <div className="recommendation-compose" style={{ margin: 0, height: '620px', display: 'grid', gridTemplateRows: 'auto 1fr auto' }}>
                            <div className="recommendation-compose__heading" style={{ justifyContent: 'space-between' }}>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                                    <span><FiUsers /></span>
                                    <div>
                                        <h2 style={{ fontSize: '18px' }}>{comunidadeAtiva.nome}</h2>
                                        <small className="admin-muted">{membros.length} membros participando</small>
                                    </div>
                                </div>
                                <div style={{ display: 'flex', gap: '8px' }}>
                                    {(papelNaAtiva === 'CRIADOR' || sessao.papel === 'ADMIN') && (
                                        <button className="recommend-reject" style={{ padding: '6px' }} title="Excluir Comunidade" onClick={() => handleExcluirComunidade(comunidadeAtiva.id)}>
                                            <FiTrash2 />
                                        </button>
                                    )}
                                    <button className="goal-deadline-button" style={{ padding: '6px' }} onClick={() => setComunidadeAtiva(null)}><FiX /></button>
                                </div>
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '14px', overflow: 'hidden', height: '100%', padding: '10px 0' }}>
                                <div style={{ overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '10px', paddingRight: '6px', borderRight: '1px solid var(--line)' }}>
                                    {mensagens.map(m => {
                                        const mIdStr = extrairIdString(m.usuarioId);
                                        const deSessao = mIdStr === extrairIdString(sessao.id);
                                        const apelidoExibicao = deSessao && meuPerfil?.apelido
                                            ? meuPerfil.apelido
                                            : usuariosMap[mIdStr] || m.usuarioApelido || m.apelido || `User #${mIdStr}`;
                                        return (
                                            <div key={m.id} style={{ position: 'relative', alignSelf: deSessao ? 'flex-end' : 'flex-start', background: deSessao ? 'rgba(229, 9, 20, 0.15)' : '#202025', padding: '10px 14px', borderRadius: '12px', maxWidth: '85%' }}>
                                                <button
                                                    type="button"
                                                    className="community-profile-link"
                                                    title={`Ver opções de @${apelidoExibicao}`}
                                                    onClick={() => setMenuPerfilAberto(atual => atual === `mensagem-${m.id}` ? '' : `mensagem-${m.id}`)}
                                                >
                                                    @{apelidoExibicao}
                                                </button>
                                                {menuPerfilAberto === `mensagem-${m.id}` && (
                                                    <div className={`community-profile-menu ${deSessao ? 'is-right' : ''}`}>
                                                        <button
                                                            type="button"
                                                            className="community-profile-menu__action"
                                                            onClick={() => navigate(`/usuario/${mIdStr}`)}
                                                        >
                                                            <FiUser /> Visualizar perfil
                                                        </button>
                                                    </div>
                                                )}
                                                <span style={{ fontSize: '13px', wordBreak: 'break-word' }}>{m.conteudo}</span>
                                            </div>
                                        );
                                    })}
                                    {mensagens.length === 0 && (
                                        <div style={{ margin: 'auto', textAlign: 'center', color: 'var(--muted)', fontSize: '12px' }}>Nenhuma mensagem enviada. Comece o papo!</div>
                                    )}
                                </div>

                                <div style={{ overflowY: 'auto', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                                    <span className="page-eyebrow" style={{ fontSize: '10px' }}>Membros</span>
                                    {membros.map(m => {
                                        const idMembStr = extrairIdString(m.usuarioId);
                                        const deSessao = idMembStr === extrairIdString(sessao.id);
                                        const apelidoMemb = deSessao && meuPerfil?.apelido
                                            ? meuPerfil.apelido
                                            : usuariosMap[idMembStr] || m.usuarioId?.apelido || m.usuarioApelido || m.apelido || `User #${idMembStr}`;
                                        const souEu = idMembStr === extrairIdString(sessao.id);
                                        return (
                                            <div key={idMembStr} style={{ position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'space-between', background: 'rgba(255,255,255,0.02)', padding: '6px 8px', borderRadius: '8px' }}>
                                                <div style={{ display: 'grid', minWidth: 0 }}>
                                                    <button
                                                        type="button"
                                                        className="community-profile-link community-profile-link--member"
                                                        title={`Ver opções de @${apelidoMemb}`}
                                                        onClick={() => setMenuPerfilAberto(atual => atual === `membro-${idMembStr}` ? '' : `membro-${idMembStr}`)}
                                                    >
                                                        @{apelidoMemb}
                                                    </button>
                                                    {menuPerfilAberto === `membro-${idMembStr}` && (
                                                        <div className="community-profile-menu">
                                                            <button
                                                                type="button"
                                                                className="community-profile-menu__action"
                                                                onClick={() => navigate(`/usuario/${idMembStr}`)}
                                                            >
                                                                <FiUser /> Visualizar perfil
                                                            </button>
                                                        </div>
                                                    )}
                                                    <small style={{ fontSize: '9px', color: m.papel === 'CRIADOR' ? '#f6c969' : m.papel === 'MODERADOR' ? '#93c5fd' : 'var(--muted)' }}>{m.papel}</small>
                                                </div>
                                                <div style={{ display: 'flex', gap: '4px' }}>
                                                    {!souEu && (papelNaAtiva === 'CRIADOR' || papelNaAtiva === 'MODERADOR' || sessao.papel === 'ADMIN') && (
                                                        <>
                                                            {papelNaAtiva === 'CRIADOR' && m.papel === 'MEMBRO' && (
                                                                <button style={{ background: 'transparent', border: 0, color: '#65dc82', cursor: 'pointer' }} title="Promover" onClick={() => handleMudarPapel(m.usuarioId, 'promover')}><FiUserCheck /></button>
                                                            )}
                                                            {papelNaAtiva === 'CRIADOR' && m.papel === 'MODERADOR' && (
                                                                <button style={{ background: 'transparent', border: 0, color: '#f6c969', cursor: 'pointer' }} title="Rebaixar" onClick={() => handleMudarPapel(m.usuarioId, 'rebaixar')}><FiShield /></button>
                                                            )}
                                                            {(papelNaAtiva === 'CRIADOR' || m.papel === 'MEMBRO') && (
                                                                <button style={{ background: 'transparent', border: 0, color: 'var(--brand)', cursor: 'pointer' }} title="Expulsar" onClick={() => handleRemoverMembro(m.usuarioId)}><FiUserX /></button>
                                                            )}
                                                        </>
                                                    )}
                                                    {souEu && m.papel !== 'CRIADOR' && (
                                                        <button style={{ background: 'transparent', border: 0, color: 'var(--muted)', cursor: 'pointer' }} title="Sair da sala" onClick={() => handleRemoverMembro(m.usuarioId)}><FiLogOut /></button>
                                                    )}
                                                </div>
                                            </div>
                                        );
                                    })}
                                </div>
                            </div>

                            {papelNaAtiva ? (
                                <form onSubmit={handleEnviarMensagem} style={{ display: 'grid', gridTemplateColumns: '1fr auto', gap: '10px', alignItems: 'center', paddingTop: '10px', borderTop: '1px solid var(--line)' }}>
                                    <input className="recommendation-message" style={{ minHeight: '44px', height: '44px', padding: '0 14px', borderRadius: '10px' }} placeholder="Digite sua mensagem na sala..." value={novaMensagem} onChange={e => setNovaMensagem(e.target.value)} maxLength="255" />
                                    <button className="btn-primary" style={{ height: '44px', minHeight: '44px', width: '44px', padding: 0, display: 'grid', placeItems: 'center' }}><FiSend /></button>
                                </form>
                            ) : (
                                <div style={{ textAlign: 'center', padding: '12px', background: 'rgba(255,255,255,0.02)', borderRadius: '10px', fontSize: '12px', color: 'var(--muted)' }}>Você precisa entrar na comunidade para ler e enviar mensagens.</div>
                            )}
                        </div>
                    )}
                </div>
            </main>

            {modalCriarAberto && (
                <div className="analysis-modal-backdrop" onMouseDown={() => setModalCriarAberto(false)}>
                    <section className="analysis-modal" role="dialog" aria-modal="true" onMouseDown={e => e.stopPropagation()}>
                        <button className="analysis-modal__close" onClick={() => setModalCriarAberto(false)}><FiX /></button>
                        <div className="analysis-modal__header">
                            <span><FiUsers /></span>
                            <div><p className="page-eyebrow">Fundação</p><h2>Criar Comunidade</h2></div>
                        </div>
                        <form onSubmit={handleCriarComunidade} className="analysis-modal__form">
                            <label>
                                <span>Nome da Comunidade</span>
                                <input placeholder="Ex: Fãs de A24, Cinema de Pernambuco..." value={formCriar.nome} onChange={e => setFormCriar({ ...formCriar, nome: e.target.value })} required autoFocus />
                            </label>
                            <label>
                                <span>Descrição ou Regras da Sala</span>
                                <input placeholder="Fale sobre o propósito da comunidade..." value={formCriar.descricao} onChange={e => setFormCriar({ ...formCriar, descricao: e.target.value })} required />
                            </label>
                            <div className="analysis-modal__footer">
                                <button type="button" className="btn-secondary" onClick={() => setModalCriarAberto(false)}>Cancelar</button>
                                <button className="btn-primary" disabled={carregando}>{carregando ? 'Fundando...' : 'Fundar Sala'}</button>
                            </div>
                        </form>
                    </section>
                </div>
            )}
        </div>
    );
}
