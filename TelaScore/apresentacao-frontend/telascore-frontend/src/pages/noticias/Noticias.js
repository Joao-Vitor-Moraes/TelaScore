import { useCallback, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import {
    FiFileText, FiPlus, FiSearch, FiX, FiClock, FiTag, FiTrash2, FiUser, FiFilm
} from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { filmeService, noticiaService, usuarioService } from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export default function Noticias() {
    const { sessao } = useAuth();
    const location = useLocation();
    const [noticias, setNoticias] = useState([]);
    const [aba, setAba] = useState('TODAS');
    const [busca, setBusca] = useState('');

    const [noticiaAtiva, setNoticiaAtiva] = useState(null);
    const [modalCriarAberto, setModalCriarAberto] = useState(false);
    const [formCriar, setFormCriar] = useState({ titulo: '', conteudo: '', categoria: 'LANCAMENTO', filmeId: '' });
    const [filmes, setFilmes] = useState([]);
    const [buscaFilme, setBuscaFilme] = useState('');
    const [seletorFilmeAberto, setSeletorFilmeAberto] = useState(false);
    const [noticiaParaExcluir, setNoticiaParaExcluir] = useState(null);
    const [filmeEmPainel, setFilmeEmPainel] = useState(null);
    const [carregandoFilmePainel, setCarregandoFilmePainel] = useState(false);

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

    useEffect(() => {
        filmeService.listar()
            .then(lista => setFilmes(Array.isArray(lista) ? lista : []))
            .catch(() => setFilmes([]));
    }, []);

    const carregarNoticias = useCallback(async () => {
        try {
            setErro('');
            const categoriaFiltro = aba === 'TODAS' ? '' : aba;
            const data = await noticiaService.pesquisar(busca, categoriaFiltro);
            const recebidas = Array.isArray(data) ? data : [];
            setNoticias(recebidas);
            const recomendada = recebidas.find(item => Number(item.id) === Number(location.state?.conteudoRecomendadoId));
            if (recomendada) setNoticiaAtiva(recomendada);
        } catch (e) {
            setNoticias([]);
            setErro('Não foi possível carregar o mural de notícias.');
        }
    }, [aba, busca, location.state?.conteudoRecomendadoId]);

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
                categoria: formCriar.categoria,
                filmeId: formCriar.filmeId || null
            });
            setFormCriar({ titulo: '', conteudo: '', categoria: 'LANCAMENTO', filmeId: '' });
            setBuscaFilme('');
            setSeletorFilmeAberto(false);
            setModalCriarAberto(false);
            setFeedback('Notícia publicada com sucesso no portal!');
            await carregarNoticias();
        } catch (e) {
            setErro(e.message || 'Erro ao publicar notícia.');
        } finally {
            setCarregando(false);
        }
    }

    async function confirmarExcluirNoticia() {
        if (!noticiaParaExcluir) return;
        try {
            await noticiaService.remover(noticiaParaExcluir.id);
            setFeedback('Publicação removida.');
            if (noticiaAtiva?.id === noticiaParaExcluir.id) setNoticiaAtiva(null);
            setNoticiaParaExcluir(null);
            await carregarNoticias();
        } catch (e) {
            setErro('Falha ao excluir a noticia.');
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
    const termoFilme = buscaFilme.trim().toLowerCase();
    const filmesFiltrados = filmes
        .filter(f => {
            return !termoFilme || f.titulo?.toLowerCase().includes(termoFilme) || String(f.anoLancamento || '').includes(termoFilme);
        })
        .sort((a, b) => (a.titulo || '').localeCompare(b.titulo || '', 'pt-BR'));
    const filmeSelecionado = filmes.find(f => String(f.id) === String(formCriar.filmeId));

    function selecionarFilme(filme) {
        setFormCriar({ ...formCriar, filmeId: String(filme.id) });
        setBuscaFilme(`${filme.titulo}${filme.anoLancamento ? ` (${filme.anoLancamento})` : ''}`);
        setSeletorFilmeAberto(false);
    }

    function limparFilmeSelecionado() {
        setFormCriar({ ...formCriar, filmeId: '' });
        setBuscaFilme('');
        setSeletorFilmeAberto(true);
    }

    async function abrirPainelFilme(noticia) {
        if (!noticia?.filmeId) return;
        setCarregandoFilmePainel(true);
        setFilmeEmPainel({
            id: noticia.filmeId,
            titulo: noticia.filmeTitulo,
            imagemUrl: noticia.filmeImagemUrl
        });
        try {
            const detalhes = await filmeService.obter(noticia.filmeId);
            setFilmeEmPainel(detalhes || {
                id: noticia.filmeId,
                titulo: noticia.filmeTitulo,
                imagemUrl: noticia.filmeImagemUrl
            });
        } catch (e) {
            setFilmeEmPainel({
                id: noticia.filmeId,
                titulo: noticia.filmeTitulo,
                imagemUrl: noticia.filmeImagemUrl
            });
        } finally {
            setCarregandoFilmePainel(false);
        }
    }

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
                                            {n.filmeTitulo && (
                                                <button type="button" className="news-movie-pill" onClick={(e) => { e.stopPropagation(); abrirPainelFilme(n); }}>
                                                    <FiFilm size={12} /> {n.filmeTitulo}
                                                </button>
                                            )}
                                        </div>
                                        {(sessao.id === n.autorId || papelAtual === 'ADMIN') && (
                                            <div className="admin-actions">
                                                <button className="recommend-reject" style={{ padding: '8px' }} onClick={() => setNoticiaParaExcluir(n)}>
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
                            {noticiaAtiva.filmeTitulo && (
                                <button type="button" className="news-movie-preview" onClick={() => abrirPainelFilme(noticiaAtiva)}>
                                    {noticiaAtiva.filmeImagemUrl && (
                                        <img src={noticiaAtiva.filmeImagemUrl} alt="" />
                                    )}
                                    <div>
                                        <span className="page-eyebrow" style={{ fontSize: '10px' }}>Filme mencionado</span>
                                        <h3 style={{ margin: '4px 0 0', fontSize: '15px' }}>{noticiaAtiva.filmeTitulo}</h3>
                                        <small>Ver detalhes do filme</small>
                                    </div>
                                </button>
                            )}
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
                            <label className="movie-combobox-label">
                                <span>Filme mencionado</span>
                                <div className="movie-combobox">
                                    <FiSearch className="movie-combobox__icon" />
                                    <input
                                        placeholder="Digite para procurar ou escolha um filme..."
                                        value={buscaFilme}
                                        onFocus={() => setSeletorFilmeAberto(true)}
                                        onBlur={() => setTimeout(() => setSeletorFilmeAberto(false), 120)}
                                        onChange={e => {
                                            setBuscaFilme(e.target.value);
                                            setFormCriar({ ...formCriar, filmeId: '' });
                                            setSeletorFilmeAberto(true);
                                        }}
                                    />
                                    {(buscaFilme || formCriar.filmeId) && (
                                        <button type="button" className="movie-combobox__clear" onMouseDown={e => e.preventDefault()} onClick={limparFilmeSelecionado}>
                                            <FiX />
                                        </button>
                                    )}
                                    {seletorFilmeAberto && (
                                        <div className="movie-combobox__menu">
                                            <button type="button" className={!formCriar.filmeId ? 'is-selected' : ''} onMouseDown={e => e.preventDefault()} onClick={() => {
                                                setFormCriar({ ...formCriar, filmeId: '' });
                                                setBuscaFilme('');
                                                setSeletorFilmeAberto(false);
                                            }}>
                                                <span>Nenhum filme mencionado</span>
                                            </button>
                                            {filmesFiltrados.map(filme => (
                                                <button
                                                    type="button"
                                                    key={filme.id}
                                                    className={String(formCriar.filmeId) === String(filme.id) ? 'is-selected' : ''}
                                                    onMouseDown={e => e.preventDefault()}
                                                    onClick={() => selecionarFilme(filme)}
                                                >
                                                    <span>{filme.titulo}</span>
                                                    {filme.anoLancamento && <small>{filme.anoLancamento}</small>}
                                                </button>
                                            ))}
                                            {filmesFiltrados.length === 0 && (
                                                <p>Nenhum filme encontrado.</p>
                                            )}
                                        </div>
                                    )}
                                </div>
                                <small className="movie-combobox__hint">
                                    {filmeSelecionado ? `Selecionado: ${filmeSelecionado.titulo}` : `${filmes.length} filmes disponiveis para mencionar`}
                                </small>
                            </label>
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

            {noticiaParaExcluir && (
                <div className="analysis-modal-backdrop" onMouseDown={() => setNoticiaParaExcluir(null)}>
                    <section className="analysis-modal news-confirm-modal" role="dialog" aria-modal="true" onMouseDown={e => e.stopPropagation()}>
                        <button className="analysis-modal__close" onClick={() => setNoticiaParaExcluir(null)}><FiX /></button>
                        <div className="analysis-modal__header">
                            <span><FiTrash2 /></span>
                            <div>
                                <p className="page-eyebrow">Exclusao</p>
                                <h2>Remover noticia?</h2>
                            </div>
                        </div>
                        <div className="news-confirm-modal__body">
                            <p>Essa publicacao sera apagada permanentemente do mural.</p>
                            <strong>{noticiaParaExcluir.titulo}</strong>
                        </div>
                        <div className="analysis-modal__footer">
                            <button type="button" className="btn-secondary" onClick={() => setNoticiaParaExcluir(null)}>Cancelar</button>
                            <button type="button" className="btn-primary" onClick={confirmarExcluirNoticia}>Apagar noticia</button>
                        </div>
                    </section>
                </div>
            )}

            {filmeEmPainel && (
                <div className="analysis-modal-backdrop" onMouseDown={() => setFilmeEmPainel(null)}>
                    <section className="analysis-modal news-film-panel" role="dialog" aria-modal="true" onMouseDown={e => e.stopPropagation()}>
                        <button className="analysis-modal__close" onClick={() => setFilmeEmPainel(null)}><FiX /></button>
                        <div className="news-film-panel__poster">
                            {filmeEmPainel.imagemUrl ? (
                                <img src={filmeEmPainel.imagemUrl} alt="" />
                            ) : (
                                <div><FiFilm /></div>
                            )}
                        </div>
                        <div className="news-film-panel__content">
                            <p className="page-eyebrow">Filme mencionado</p>
                            <h2>{filmeEmPainel.titulo}</h2>
                            <div className="news-film-panel__meta">
                                {filmeEmPainel.anoLancamento && <span>{filmeEmPainel.anoLancamento}</span>}
                                {filmeEmPainel.nomeDiretor && <span>{filmeEmPainel.nomeDiretor}</span>}
                                {Number(filmeEmPainel.mediaNotas) > 0 && <span>Nota {Number(filmeEmPainel.mediaNotas).toFixed(1)}</span>}
                            </div>
                            {Array.isArray(filmeEmPainel.generos) && filmeEmPainel.generos.length > 0 && (
                                <div className="news-film-panel__genres">
                                    {filmeEmPainel.generos.map(genero => <span key={genero}>{genero}</span>)}
                                </div>
                            )}
                            {carregandoFilmePainel ? (
                                <p className="news-film-panel__loading">Carregando detalhes do filme...</p>
                            ) : (
                                <p className="news-film-panel__synopsis">
                                    {filmeEmPainel.sinopse || 'Sem sinopse cadastrada para este filme.'}
                                </p>
                            )}
                        </div>
                    </section>
                </div>
            )}
        </div>
    );
}
