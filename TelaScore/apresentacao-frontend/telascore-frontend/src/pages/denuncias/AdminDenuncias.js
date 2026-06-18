import { useCallback, useEffect, useMemo, useState } from 'react';
import { FiCalendar, FiCheck, FiExternalLink, FiEye, FiFlag, FiSearch, FiTag, FiTarget, FiUser, FiX } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import NavbarAdmin from '../../components/NavbarAdmin';
import { useAuth } from '../../context/AuthContext';
import { denunciaService } from '../../services/api';

const STATUS = [
    { valor: 'PENDENTE', rotulo: 'Pendentes' },
    { valor: 'EM_ANALISE', rotulo: 'Em análise' },
    { valor: 'ACEITA', rotulo: 'Aceitas' },
    { valor: 'REJEITADA', rotulo: 'Rejeitadas' },
];

export default function AdminDenuncias() {
    const { sessao } = useAuth();
    const [status, setStatus] = useState('PENDENTE');
    const [denuncias, setDenuncias] = useState([]);
    const [carregando, setCarregando] = useState(true);
    const [erro, setErro] = useState(null);
    const [erroAcao, setErroAcao] = useState(null);
    const [busca, setBusca] = useState('');

    const carregar = useCallback(async (statusSelecionado = status) => {
        setCarregando(true);
        setErro(null);
        try {
            const dados = await denunciaService.listarPorStatus(statusSelecionado);
            setDenuncias(Array.isArray(dados) ? dados : []);
        } catch {
            setDenuncias([]);
            setErro('Erro ao carregar denúncias.');
        } finally {
            setCarregando(false);
        }
    }, [status]);

    useEffect(() => {
        if (sessao?.papel === 'ADMIN') {
            carregar();
        }
    }, [carregar, sessao?.papel]);

    const listagem = useMemo(() => {
        const termo = busca.trim().toLowerCase();
        if (!termo) return denuncias;

        return denuncias.filter(denuncia => [
            denuncia.id,
            denuncia.denuncianteId,
            denuncia.alvoId,
            denuncia.status,
            denuncia.tipoAlvo,
            denuncia.motivo,
            denuncia.descricao,
            denuncia.linkOcorrencia,
            formatarData(denuncia.dataCriacao),
        ].some(valor => String(valor ?? '').toLowerCase().includes(termo)));
    }, [busca, denuncias]);

    async function avaliar(id, decisao) {
        setErroAcao(null);
        try {
            await denunciaService.avaliar(id, decisao);
            await carregar();
        } catch {
            setErroAcao('Erro ao avaliar denúncia.');
        }
    }

    if (sessao?.papel !== 'ADMIN') {
        return (
            <div style={styles.pagina}>
                <Navbar />
                <NavbarAdmin />
                <p style={styles.msgErro}>Acesso restrito a administradores.</p>
            </div>
        );
    }

    return (
        <div style={styles.pagina}>
            <Navbar />
            <NavbarAdmin />
            <div style={styles.conteudo}>
                <div style={styles.cabecalho}>
                    <div>
                        <h2 style={styles.titulo}>ADM. DENÚNCIAS</h2>
                        <span style={styles.total}>{listagem.length} de {denuncias.length} denúncias</span>
                    </div>

                    <label className="template-search">
                        <FiSearch size={16} />
                        <input
                            value={busca}
                            onChange={e => setBusca(e.target.value)}
                            placeholder="Buscar denúncia..."
                        />
                    </label>
                </div>

                <div style={styles.abas}>
                    {STATUS.map(item => (
                        <button
                            key={item.valor}
                            className={item.valor === status ? 'btn-primary' : 'btn-secondary'}
                            onClick={() => setStatus(item.valor)}
                        >
                            {item.rotulo}
                        </button>
                    ))}
                </div>

                {erroAcao && <p style={styles.msgErroAcao}>{erroAcao}</p>}
                {carregando && <p style={styles.msg}>Carregando...</p>}
                {erro && !carregando && <p style={styles.msgErro}>{erro}</p>}
                {!erro && !carregando && listagem.length === 0 && (
                    <p style={styles.msg}>Nenhuma denúncia encontrada.</p>
                )}

                <div style={styles.lista}>
                    {listagem.map(denuncia => (
                        <article key={denuncia.id} style={styles.card}>
                            <div style={styles.cardTopo}>
                                <div style={styles.avatar}>
                                    <FiUser size={22} />
                                </div>

                                <div style={styles.cardInfo}>
                                    <div style={styles.linhaPrincipal}>
                                        <span style={styles.cardTitulo}>Denúncia #{denuncia.id}</span>
                                        <span style={{ ...styles.badge, ...estiloStatus(denuncia.status) }}>
                                            <FiFlag size={12} />
                                            {rotuloStatus(denuncia.status)}
                                        </span>
                                    </div>

                                    <div style={styles.detalhes}>
                                        <span style={styles.detalhe}><FiUser size={13} /> Denunciante #{denuncia.denuncianteId ?? '-'}</span>
                                        <span style={styles.detalhe}><FiTarget size={13} /> Alvo #{denuncia.alvoId ?? '-'}</span>
                                        <span style={styles.detalhe}><FiTag size={13} /> {rotuloMotivo(denuncia.motivo)}</span>
                                        <span style={styles.detalhe}><FiCalendar size={13} /> {formatarData(denuncia.dataCriacao)}</span>
                                    </div>
                                </div>
                            </div>

                            {denuncia.descricao && (
                                <p style={styles.descricao}>{denuncia.descricao}</p>
                            )}

                            <div style={styles.rodapeCard}>
                                {denuncia.linkOcorrencia ? (
                                    <a className="btn-secondary" href={denuncia.linkOcorrencia} target="_blank" rel="noreferrer">
                                        <FiExternalLink size={14} />
                                        Abrir link
                                    </a>
                                ) : (
                                    <span style={styles.semLink}>Sem link</span>
                                )}

                                <div style={styles.acoes}>
                                    {denuncia.status === 'PENDENTE' && (
                                        <button className="btn-secondary" onClick={() => avaliar(denuncia.id, 'ANALISAR')}>
                                            <FiEye size={14} />
                                            Analisar
                                        </button>
                                    )}

                                    {(denuncia.status === 'PENDENTE' || denuncia.status === 'EM_ANALISE') && (
                                        <>
                                            <button className="btn-primary" onClick={() => avaliar(denuncia.id, 'ACEITAR')}>
                                                <FiCheck size={14} />
                                                Aceitar
                                            </button>
                                            <button className="btn-danger" onClick={() => avaliar(denuncia.id, 'REJEITAR')}>
                                                <FiX size={14} />
                                                Rejeitar
                                            </button>
                                        </>
                                    )}
                                </div>
                            </div>
                        </article>
                    ))}
                </div>
            </div>
        </div>
    );
}

function formatarData(valor) {
    if (!valor) return '-';
    return new Date(valor).toLocaleString('pt-BR');
}

function rotuloStatus(status) {
    const rotulos = {
        PENDENTE: 'Pendente',
        EM_ANALISE: 'Em análise',
        ACEITA: 'Aceita',
        REJEITADA: 'Rejeitada',
    };
    return rotulos[status] || status;
}

function rotuloMotivo(motivo) {
    const rotulos = {
        OFENSIVO: 'Ofensivo',
        SPAM: 'Spam',
        ASSEDIO: 'Assédio',
        CONTEUDO_INADEQUADO: 'Conteúdo inadequado',
        OUTRO: 'Outro',
    };
    return rotulos[motivo] || motivo || '-';
}

function estiloStatus(status) {
    const estilos = {
        PENDENTE: styles.badgePendente,
        EM_ANALISE: styles.badgeAnalise,
        ACEITA: styles.badgeAceita,
        REJEITADA: styles.badgeRejeitada,
    };
    return estilos[status] || styles.badgeNeutro;
}

const styles = {
    pagina: {
        minHeight: '100vh',
        backgroundColor: '#0f3460',
        color: 'white',
    },
    conteudo: {
        maxWidth: '920px',
        margin: '0 auto',
        padding: '24px 32px',
    },
    cabecalho: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '16px',
        marginBottom: '16px',
        flexWrap: 'wrap',
    },
    titulo: {
        fontSize: '16px',
        letterSpacing: '1px',
        margin: 0,
    },
    total: {
        display: 'block',
        color: '#aaa',
        fontSize: '12px',
        marginTop: '6px',
    },
    buscaBox: {
        display: 'flex',
        alignItems: 'center',
        gap: '8px',
        backgroundColor: '#16213e',
        border: '1px solid #2a2a4a',
        borderRadius: '8px',
        padding: '8px 12px',
        minWidth: '240px',
    },
    inputBusca: {
        border: 'none',
        backgroundColor: 'transparent',
        color: 'white',
        outline: 'none',
        fontSize: '13px',
        width: '100%',
    },
    abas: {
        display: 'flex',
        gap: '8px',
        flexWrap: 'wrap',
        marginBottom: '16px',
    },
    aba: {
        padding: '8px 12px',
        borderRadius: '6px',
        border: '1px solid #2a2a4a',
        backgroundColor: '#16213e',
        color: '#aaa',
        cursor: 'pointer',
        fontSize: '12px',
        fontWeight: 'bold',
    },
    abaAtiva: {
        color: 'white',
        borderColor: '#e94560',
        backgroundColor: 'rgba(233, 69, 96, 0.15)',
    },
    lista: {
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
    },
    card: {
        backgroundColor: '#16213e',
        borderRadius: '10px',
        padding: '16px',
        border: '1px solid #22304f',
        display: 'flex',
        flexDirection: 'column',
        gap: '12px',
    },
    cardTopo: {
        display: 'flex',
        alignItems: 'center',
        gap: '16px',
    },
    avatar: {
        width: '56px',
        height: '56px',
        borderRadius: '8px',
        backgroundColor: '#0f3460',
        border: '1px solid #2a2a4a',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: '#e94560',
        flexShrink: 0,
    },
    cardInfo: {
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        gap: '8px',
        minWidth: 0,
    },
    linhaPrincipal: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'flex-start',
        gap: '12px',
        flexWrap: 'wrap',
    },
    cardTitulo: {
        fontSize: '15px',
        fontWeight: 'bold',
    },
    badge: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        padding: '4px 8px',
        borderRadius: '6px',
        fontSize: '11px',
        fontWeight: 'bold',
        letterSpacing: '0.5px',
    },
    badgePendente: {
        color: '#f97316',
        border: '1px solid #f97316',
    },
    badgeAnalise: {
        color: '#93c5fd',
        border: '1px solid #93c5fd',
    },
    badgeAceita: {
        color: '#10b981',
        border: '1px solid #10b981',
    },
    badgeRejeitada: {
        color: '#e94560',
        border: '1px solid #e94560',
    },
    badgeNeutro: {
        color: '#aaa',
        border: '1px solid #aaa',
    },
    detalhes: {
        display: 'flex',
        flexWrap: 'wrap',
        gap: '8px 14px',
    },
    detalhe: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        color: '#aaa',
        fontSize: '12px',
        minWidth: 0,
    },
    descricao: {
        color: '#ddd',
        fontSize: '13px',
        margin: 0,
        lineHeight: 1.45,
    },
    rodapeCard: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: '12px',
        flexWrap: 'wrap',
    },
    link: {
        display: 'inline-flex',
        alignItems: 'center',
        gap: '6px',
        color: '#93c5fd',
        fontSize: '13px',
        textDecoration: 'none',
    },
    semLink: {
        color: '#777',
        fontSize: '13px',
    },
    acoes: {
        display: 'flex',
        gap: '8px',
        flexWrap: 'wrap',
    },
    btnAnalise: {
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '6px',
        padding: '7px 12px',
        borderRadius: '6px',
        border: '1px solid #f97316',
        backgroundColor: 'transparent',
        color: '#f97316',
        cursor: 'pointer',
        fontSize: '12px',
        fontWeight: 'bold',
    },
    btnAceitar: {
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '6px',
        padding: '7px 12px',
        borderRadius: '6px',
        border: 'none',
        backgroundColor: '#10b981',
        color: 'white',
        cursor: 'pointer',
        fontSize: '12px',
        fontWeight: 'bold',
    },
    btnRejeitar: {
        display: 'inline-flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '6px',
        padding: '7px 12px',
        borderRadius: '6px',
        border: '1px solid #e94560',
        backgroundColor: 'transparent',
        color: '#e94560',
        cursor: 'pointer',
        fontSize: '12px',
        fontWeight: 'bold',
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
    msgErroAcao: {
        color: '#e94560',
        backgroundColor: 'rgba(233, 69, 96, 0.1)',
        border: '1px solid rgba(233, 69, 96, 0.35)',
        borderRadius: '8px',
        padding: '10px 12px',
        fontSize: '13px',
        marginBottom: '12px',
    },
};
