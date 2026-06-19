import { useCallback, useEffect, useState } from 'react';
import {
    FiFileText, FiPlus, FiSearch, FiX, FiClock, FiTag, FiTrash2, FiUser
} from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { noticiaService, usuarioService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function Noticias() {
    const { sessao } = useAuth();
    const [noticias, setNoticias] = useState([]);
    const [aba, setAba] = useState('TODAS');
    const [busca, setBusca] = useState('');

    const [noticiaAtiva, setNoticiaAtiva] = useState(null);
    const [modalCriarAberto, setModalCriarAberto] = useState(false);
    const [formCriar, setFormCriar] = useState({ titulo: '', conteudo: '', categoria: 'LANCAMENTO' });

    const [erro, setErro] = useState('');
    const [feedback, setFeedback] = useState('');
    const [carregando, setCarregando] = useState(false);
    const [papelAtual, setPapelAtual] = useState(sessao?.papel || 'CINEFILO');

    useEffect(() => {
        async function verificarPapelNoCarregamento() {
            if (!sessao?.id) return;
            try {
                const dadosUsuario = await usuarioService.obter(sessao.id);
                if (dadosUsuario && dadosUsuario.papel) {
                    setPapelAtual(dadosUsuario.papel);
                }
            } catch (e) {
                setPapelAtual(sessao.papel || 'CINEFILO');
            }
        }
        verificarPapelNoCarregamento();
    }, [sessao]);

    const carregarNoticias = useCallback(async () => {
        try {
            setErro('');
            const categoriaFiltro = aba === 'TODAS' ? '' : aba;
            const data = await noticiaService.pesquisar(busca, categoriaFiltro);
            setNoticias(Array.isArray(data) ? data : []);
        } catch (e) {
            setNoticias([]);
            setErro('Não foi possível carregar o mural de notícias.');
        }
    }, [aba, busca]);

    useEffect(() => {
        carregarNoticias();
    }, [carregarNoticias]);

    useEffect(() => {
        const intervalo = setInterval(carregarNoticias, 5000);
        return () => clearInterval(intervalo);
    }, [carregarNoticias]);

    async function handleCriarNoticia(e) {
        e.preventDefault();
        setCarregando(true);
        try {
            await noticiaService.criar({
                autorId: sessao.id,
                titulo: formCriar.titulo,
                conteudo: formCriar.conteudo,
                categoria: formCriar.categoria
            });
            setFormCriar({ titulo: '', conteudo: '', categoria: 'LANCAMENTO' });
            setModalCriarAberto(false);
            setFeedback('Notícia publicada com sucesso no portal!');
            await carregarNoticias();
        } catch (e) {
            setErro(e.message || 'Erro ao publicar notícia.');
        } finally {
            setCarregando(false);
        }
    }

    async function handleExcluirNoticia(id) {
        if (!window.confirm('Deseja apagar esta publicação permanentemente do sistema?')) return;
        try {
            await noticiaService.remover(id);
            setFeedback('Publicação removida.');
            if (noticiaAtiva?.id === id) setNoticiaAtiva(null);
            await carregarNoticias();
        } catch (e) {
            setErro('Falha ao excluir a notícia.');
        }
    }

    const formatarData = (dataString) => {
        if (!dataString) return '';
        const data = new Date(dataString);
        return data.toLocaleDateString('pt-BR', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
    };

    if (!sessao) {
        return (
            <div className="cinema-page" style={{ display: 'grid', placeItems: 'center', minHeight: '100vh' }}>
                <div className="recommendations-empty"><p>Autenticando sessão de leitor...</p></div>
            </div>
        );
    }

    const podeRedigir = papelAtual === 'ADMIN' || papelAtual === 'AUTOR';

    return (
        <div className="cinema-page">
            <Navbar />
            <main className="cinema-container goals-container">
                <div className="goals-heading">
                    <div>
                        <p className="page-eyebrow">Informação</p>
                        <h1 className="page-title">Mural de Notícias</h1>
                        <p className="page-description">Fique por dentro de lançamentos, críticas da comunidade, curiosidades e festivais de cinema.</p>
                    </div>
                    {podeRedigir && (
                        <button className="btn-primary goals-new-button" onClick={() => setModalCriarAberto(true)}>
                            <FiPlus /> Redigir Notícia
                        </button>
                    )}
                </div>

                <section className="recommendations-tabs" role="tablist">
                    <button className={aba === 'TODAS' ? 'is-active' : ''} onClick={() => setAba('TODAS')}><FiFileText /> Todas</button>
                    <button className={aba === 'LANCAMENTO' ? 'is-active' : ''} onClick={() => setAba('LANCAMENTO')}><FiClock /> Lançamentos</button>
                    <button className={aba === 'CRITICA' ? 'is-active' : ''} onClick={() => setAba('CRITICA')}><FiFileText /> Críticas</button>
                    <button className={aba === 'EVENTO' ? 'is-active' : ''} onClick={() => setAba('EVENTO')}><FiTag /> Eventos</button>
                    <button className={aba === 'CURIOSIDADE' ? 'is-active' : ''} onClick={() => setAba('CURIOSIDADE')}><FiSearch /> Curiosidades</button>
                </section>

                <div className="recommendations-filters">
                    <label>
                        <FiSearch />
                        <input value={busca} onChange={e => setBusca(e.target.value)} placeholder="Pesquisar notícias por palavras-chave..." />
                        {busca && <button onClick={() => setBusca('')}><FiX /></button>}
                    </label>
                </div>

                {erro && <div className="analysis-error">{erro}</div>}
                {feedback && <div className="analysis-feedback">{feedback}</div>}

                <div className="goals-grid" style={{ gridTemplateColumns: noticiaAtiva ? '1fr 1.2fr' : 'repeat(auto-fit, minmax(320px, 1fr))' }}>

                    <div className="admin-list" style={{ display: 'grid', gap: '14px', alignContent: 'start' }}>
                        {noticias.map(n => {
                            const ativa = noticiaAtiva?.id === n.id;
                            return (
                                <article key={n.id} className="admin-card" style={{ borderColor: ativa ? 'var(--brand)' : '' }}>
                                    <div className="admin-card__main" style={{ gridTemplateColumns: '1fr auto' }}>
                                        <div className="admin-card__body" style={{ cursor: 'pointer' }} onClick={() => setNoticiaAtiva(n)}>
                                            <span className="page-eyebrow" style={{ fontSize: '10px' }}>{n.categoria}</span>
                                            <h3 style={{ marginTop: '4px', marginBottom: '0px' }}>{n.titulo}</h3>
                                            <small className="admin-muted" style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                                                <span><FiUser /> @{n.autorApelido || `Autor #${n.autorId}`}</span>
                                                <span><FiClock /> {formatarData(n.dataPublicacao)}</span>
                                            </small>
                                        </div>
                                        {(sessao.id === n.autorId || papelAtual === 'ADMIN') && (
                                            <div className="admin-actions">
                                                <button className="recommend-reject" style={{ padding: '8px' }} onClick={() => handleExcluirNoticia(n.id)}>
                                                    <FiTrash2 />
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                </article>
                            );
                        })}
                        {noticias.length === 0 && (
                            <div className="recommendations-empty"><FiFileText /><p>Nenhuma notícia encontrada com estes filtros.</p></div>
                        )}
                    </div>

                    {noticiaAtiva && (
                        <div className="recommendation-compose" style={{ margin: 0, padding: '24px', display: 'flex', flexDirection: 'column', gap: '14px', height: '100%', overflowY: 'auto' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', borderBottom: '1px solid var(--line)', paddingBottom: '14px' }}>
                                <div>
                                    <span className="page-eyebrow">{noticiaAtiva.categoria}</span>
                                    <h2 style={{ fontSize: '22px', marginTop: '6px' }}>{noticiaAtiva.titulo}</h2>
                                    <div style={{ display: 'flex', gap: '14px', marginTop: '8px' }} className="admin-muted">
                                        <small style={{ fontWeight: 'bold', color: 'var(--brand-light)' }}>@{noticiaAtiva.autorApelido || `Autor #${noticiaAtiva.autorId}`}</small>
                                        <small>{formatarData(noticiaAtiva.dataPublicacao)}</small>
                                    </div>
                                </div>
                                <button className="goal-deadline-button" style={{ padding: '6px' }} onClick={() => setNoticiaAtiva(null)}><FiX /></button>
                            </div>
                            <p style={{ fontSize: '15px', lineHeight: '1.6', color: 'rgba(255,255,255,0.85)', whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
                                {noticiaAtiva.conteudo}
                            </p>
                        </div>
                    )}
                </div>
            </main>

            {modalCriarAberto && (
                <div className="analysis-modal-backdrop" onMouseDown={() => setModalCriarAberto(false)}>
                    <section className="analysis-modal" style={{ maxWidth: '650px' }} role="dialog" aria-modal="true" onMouseDown={e => e.stopPropagation()}>
                        <button className="analysis-modal__close" onClick={() => setModalCriarAberto(false)}><FiX /></button>
                        <div className="analysis-modal__header">
                            <span><FiFileText /></span>
                            <div><p className="page-eyebrow">Redação</p><h2>Publicar Nova Notícia</h2></div>
                        </div>
                        <form onSubmit={handleCriarNoticia} className="analysis-modal__form">
                            <label>
                                <span>Título Principal</span>
                                <input placeholder="Digite um título impactante para a notícia..." value={formCriar.titulo} onChange={e => setFormCriar({ ...formCriar, titulo: e.target.value })} required autoFocus />
                            </label>
                            <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '14px' }}>
                                <label>
                                    <span>Categoria</span>
                                    <select style={{ background: '#151518', color: '#fff', border: '1px solid var(--line)', height: '44px', borderRadius: '8px', padding: '0 10px' }} value={formCriar.categoria} onChange={e => setFormCriar({ ...formCriar, categoria: e.target.value })}>
                                        <option value="LANCAMENTO">Lançamento</option>
                                        <option value="CRITICA">Crítica</option>
                                        <option value="EVENTO">Evento</option>
                                        <option value="CURIOSIDADE">Curiosidade</option>
                                    </select>
                                </label>
                            </div>
                            <label>
                                <span>Corpo da Notícia</span>
                                <textarea style={{ background: '#151518', color: '#fff', border: '1px solid var(--line)', borderRadius: '8px', padding: '12px', minHeight: '180px', fontFamily: 'inherit', resize: 'vertical' }} placeholder="Escreva o conteúdo integral da matéria aqui..." value={formCriar.conteudo} onChange={e => setFormCriar({ ...formCriar, conteudo: e.target.value })} required />
                            </label>
                            <div className="analysis-modal__footer">
                                <button type="button" className="btn-secondary" onClick={() => setModalCriarAberto(false)}>Cancelar</button>
                                <button className="btn-primary" disabled={carregando}>{carregando ? 'Publicando...' : 'Publicar Matéria'}</button>
                            </div>
                        </form>
                    </section>
                </div>
            )}
        </div>
    );
}