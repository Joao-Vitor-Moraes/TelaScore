import { useCallback, useEffect, useMemo, useState } from 'react';
import { FiCalendar, FiCheck, FiExternalLink, FiEye, FiFlag, FiSearch, FiTag, FiTarget, FiUser, FiX } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import NavbarAdmin from '../../components/NavbarAdmin';
import { useAuth } from '../../context/AuthContext';
import { denunciaService } from '../../services/api';
import '../admin/admin.css';

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
            <div className="cinema-page admin-page">
                <Navbar />
                <NavbarAdmin />
                <main className="cinema-container admin-content">
                    <div className="empty-state admin-error">Acesso restrito a administradores.</div>
                </main>
            </div>
        );
    }

    return (
        <div className="cinema-page admin-page">
            <Navbar />
            <NavbarAdmin />

            <main className="cinema-container admin-content">
                <div className="catalog-toolbar admin-toolbar">
                    <div>
                        <p className="page-eyebrow">Administração</p>
                        <h2 className="page-title">Denúncias</h2>
                        <p className="page-description">{listagem.length} de {denuncias.length} denúncias neste status</p>
                    </div>

                    <div className="catalog-toolbar__actions">
                        <label className="catalog-search">
                            <FiSearch />
                            <input
                                value={busca}
                                onChange={e => setBusca(e.target.value)}
                                placeholder="Buscar denúncia..."
                            />
                        </label>
                    </div>
                </div>

                <div className="admin-filterbar">
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

                {erroAcao && <div className="admin-alert">{erroAcao}</div>}
                {carregando && <div className="empty-state">Carregando...</div>}
                {erro && !carregando && <div className="empty-state admin-error">{erro}</div>}
                {!erro && !carregando && listagem.length === 0 && (
                    <div className="empty-state">
                        <FiFlag size={32} />
                        <p>Nenhuma denúncia encontrada.</p>
                    </div>
                )}

                {!carregando && !erro && listagem.length > 0 && (
                    <div className="admin-list">
                        {listagem.map(denuncia => (
                            <article key={denuncia.id} className="admin-card admin-report-card">
                                <div className="admin-card__main">
                                    <div className="admin-avatar admin-avatar--report">
                                        <FiUser size={24} />
                                    </div>

                                    <div className="admin-card__body">
                                        <div className="admin-card__title-row">
                                            <h3>Denúncia #{denuncia.id}</h3>
                                            <span className={`admin-status ${classeStatus(denuncia.status)}`}>
                                                <FiFlag />
                                                {rotuloStatus(denuncia.status)}
                                            </span>
                                        </div>

                                        <div className="admin-meta">
                                            <span><FiUser /> Denunciante #{denuncia.denuncianteId ?? '-'}</span>
                                            <span><FiTarget /> Alvo #{denuncia.alvoId ?? '-'}</span>
                                            <span><FiTag /> {rotuloMotivo(denuncia.motivo)}</span>
                                            <span><FiCalendar /> {formatarData(denuncia.dataCriacao)}</span>
                                        </div>

                                        {denuncia.descricao && (
                                            <p className="admin-description">{denuncia.descricao}</p>
                                        )}
                                    </div>
                                </div>

                                <div className="admin-card__footer">
                                    {denuncia.linkOcorrencia ? (
                                        <a className="btn-secondary" href={denuncia.linkOcorrencia} target="_blank" rel="noreferrer">
                                            <FiExternalLink />
                                            Abrir link
                                        </a>
                                    ) : (
                                        <span className="admin-muted">Sem link</span>
                                    )}

                                    <div className="admin-actions">
                                        {denuncia.status === 'PENDENTE' && (
                                            <button className="btn-secondary" onClick={() => avaliar(denuncia.id, 'ANALISAR')}>
                                                <FiEye />
                                                Analisar
                                            </button>
                                        )}

                                        {(denuncia.status === 'PENDENTE' || denuncia.status === 'EM_ANALISE') && (
                                            <>
                                                <button className="btn-primary" onClick={() => avaliar(denuncia.id, 'ACEITAR')}>
                                                    <FiCheck />
                                                    Aceitar
                                                </button>
                                                <button className="btn-danger" onClick={() => avaliar(denuncia.id, 'REJEITAR')}>
                                                    <FiX />
                                                    Rejeitar
                                                </button>
                                            </>
                                        )}
                                    </div>
                                </div>
                            </article>
                        ))}
                    </div>
                )}
            </main>
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

function classeStatus(status) {
    if (status === 'ACEITA') return 'is-accepted';
    if (status === 'REJEITADA') return 'is-rejected';
    if (status === 'EM_ANALISE') return 'is-reviewing';
    return 'is-pending';
}
