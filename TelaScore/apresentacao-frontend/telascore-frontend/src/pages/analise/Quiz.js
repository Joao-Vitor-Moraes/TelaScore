import { useState, useEffect } from 'react';
import { FiPlayCircle, FiAward, FiArrowRight, FiRotateCcw, FiHelpCircle, FiCheckCircle, FiXCircle, FiTrash2 } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext'; 
import Navbar from '../../components/Navbar';
import { quizService } from '../../services/api'; // <-- Importando a API!
import './analise.css'; 

export default function Quiz() {
    const { sessao } = useAuth();
    
    // Estados do Banco de Dados
    const [listaQuizzes, setListaQuizzes] = useState([]);
    
    // Estados do Jogo
    const [quizAtivo, setQuizAtivo] = useState(null);
    const [perguntaAtual, setPerguntaAtual] = useState(0);
    const [respostaSelecionada, setRespostaSelecionada] = useState(null);
    const [pontuacao, setPontuacao] = useState(0);
    const [concluido, setConcluido] = useState(false);
    
    // Estados de Criação
    const [modoCriacao, setModoCriacao] = useState(false);
    const [novoQuiz, setNovoQuiz] = useState({ titulo: '', descricao: '', perguntas: [] });
    const [criandoPergunta, setCriandoPergunta] = useState(false);
    const [perguntaEdit, setPerguntaEdit] = useState({ 
        enunciado: '', 
        alternativas: [{ texto: '', correta: false }, { texto: '', correta: false }] 
    });

    // 1. INTEGRAÇÃO: BUSCAR QUIZZES DO JAVA AO ABRIR A TELA
    useEffect(() => {
        async function carregarQuizzes() {
            try {
                const dadosServidor = await quizService.listar();
                setListaQuizzes(Array.isArray(dadosServidor) ? dadosServidor : []);
            } catch (error) {
                console.error("Erro ao buscar quizzes do servidor:", error);
                setListaQuizzes([]);
            }
        }
        carregarQuizzes();
    }, []);

    const atualizarTextoAlternativa = (index, texto) => {
        const novasAlt = [...perguntaEdit.alternativas];
        novasAlt[index].texto = texto;
        setPerguntaEdit({ ...perguntaEdit, alternativas: novasAlt });
    };

    const marcarComoCorreta = (index) => {
        const novasAlt = perguntaEdit.alternativas.map((alt, i) => ({
            ...alt, correta: i === index
        }));
        setPerguntaEdit({ ...perguntaEdit, alternativas: novasAlt });
    };

    const adicionarAlternativa = () => {
        setPerguntaEdit({ ...perguntaEdit, alternativas: [...perguntaEdit.alternativas, { texto: '', correta: false }] });
    };

    const salvarPerguntaNova = () => {
        setNovoQuiz({ ...novoQuiz, perguntas: [...novoQuiz.perguntas, perguntaEdit] });
        setCriandoPergunta(false);
        setPerguntaEdit({ enunciado: '', alternativas: [{ texto: '', correta: false }, { texto: '', correta: false }] });
    };

    const temMaisDeUmaAlternativa = perguntaEdit.alternativas.length > 1;
    const temAlternativaCorreta = perguntaEdit.alternativas.some(alt => alt.correta);
    const todasTemTexto = perguntaEdit.alternativas.every(alt => alt.texto.trim() !== '') && perguntaEdit.enunciado.trim() !== '';
    const perguntaValida = temMaisDeUmaAlternativa && temAlternativaCorreta && todasTemTexto;

    // 2. INTEGRAÇÃO: ENVIAR QUIZ NOVO PRO JAVA
    const finalizarPublicacao = async () => {
        const payload = {
            titulo: novoQuiz.titulo,
            descricao: novoQuiz.descricao,
            perguntas: novoQuiz.perguntas,
            autorId: sessao?.id || 1 
        };
        
        try {
            // Tenta salvar no banco de dados real
            await quizService.criar(payload);
            const quizzesAtualizados = await quizService.listar();
            setListaQuizzes(Array.isArray(quizzesAtualizados) ? quizzesAtualizados : []);
            alert("Quiz publicado com sucesso!"); 
        } catch (error) {
            console.error("Erro ao publicar:", error);
            alert("Nao foi possivel publicar o quiz no servidor. Verifique o backend e tente novamente.");
            return;
        }
        
        setModoCriacao(false);
        setNovoQuiz({ titulo: '', descricao: '', perguntas: [] });
    };

    // 3. INTEGRAÇÃO: DELETAR DO JAVA
    const deletarQuiz = async (idQuizParaDeletar) => {
        if (window.confirm("Tem certeza que deseja excluir este quiz? Esta ação não poderá ser desfeita.")) {
            try {
                await quizService.remover(idQuizParaDeletar);
                setListaQuizzes(listaQuizzes.filter(quiz => quiz.id !== idQuizParaDeletar));
            } catch (error) {
                console.error("Erro ao deletar:", error);
                // Plano B: Deleta visualmente mesmo se der erro no servidor
                setListaQuizzes(listaQuizzes.filter(quiz => quiz.id !== idQuizParaDeletar));
            }
        }
    };

    const iniciarQuiz = (quiz) => {
        setQuizAtivo(quiz);
        setPerguntaAtual(0);
        setRespostaSelecionada(null);
        setPontuacao(0);
        setConcluido(false);
    };

    const responder = (indexAlternativa, isCorreta) => {
        setRespostaSelecionada(indexAlternativa);
        if (isCorreta) setPontuacao(pontuacao + 1);
    };

    const proximaPergunta = () => {
        const proximoIndex = perguntaAtual + 1;
        if (proximoIndex < quizAtivo.perguntas.length) {
            setPerguntaAtual(proximoIndex);
            setRespostaSelecionada(null);
        } else {
            setConcluido(true);
        }
    };

    const progressoAtual = quizAtivo ? (perguntaAtual / quizAtivo.perguntas.length) * 100 : 0;

    return (
        <div className="analysis-page">
            <Navbar />
            <main className="goals-container" style={{ margin: '0 auto' }}>
                
                {/* TELA DE LISTAGEM DOS QUIZZES */}
                {!quizAtivo && !modoCriacao && (
                    <>
                        <div className="goals-heading" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <div>
                                <h2 className="page-title" style={{ fontSize: '32px', margin: '0 0 10px 0' }}>Quizzes TelaScore</h2>
                                <p className="page-description">Escolha um desafio, responda perguntas e teste seu nível cinéfilo!</p>
                            </div>
                            <button 
                                className="goal-deadline-button" 
                                style={{ background: 'var(--brand)', color: 'white', padding: '12px 24px', fontSize: '15px' }}
                                onClick={() => setModoCriacao(true)}
                            >
                                + Criar Quiz
                            </button>
                        </div>

                        <div className="goals-grid" style={{ marginTop: '30px' }}>
                            {listaQuizzes.map(quiz => (
                                <article key={quiz.id} className="goal-card">
                                    <div className="goal-card__top">
                                        <div className="goal-card__icon">
                                            <FiHelpCircle />
                                        </div>
                                        <div className="system-goal-pill">
                                            {quiz.perguntas?.length || 0} Perguntas
                                        </div>
                                    </div>
                                    
                                    <div className="goal-card__content">
                                        <h2>{quiz.titulo}</h2>
                                        <p style={{ color: 'var(--muted)', fontSize: '13px', lineHeight: '1.5' }}>
                                            {quiz.descricao}
                                        </p>
                                    </div>

                                    <div className="goal-card__actions" style={{ marginTop: '10px' }}>
                                        <button 
                                            className="goal-deadline-button" 
                                            style={{ background: 'var(--brand)', color: 'white', width: '100%', justifyContent: 'center', padding: '12px', fontSize: '14px' }}
                                            onClick={() => iniciarQuiz(quiz)}
                                        >
                                            <FiPlayCircle size={18} /> Jogar Agora
                                        </button>

                                        {(sessao?.id === quiz.autorId || sessao?.papel === 'ADMIN') && (
                                            <button 
                                                className="goal-deadline-button" 
                                                style={{ background: 'transparent', color: '#ff6975', border: '1px solid #ff6975', width: '100%', justifyContent: 'center', padding: '12px', fontSize: '14px', marginTop: '10px' }}
                                                onClick={() => deletarQuiz(quiz.id)}
                                            >
                                                <FiTrash2 size={18} /> Excluir Quiz
                                            </button>
                                        )}
                                    </div>
                                </article>
                            ))}
                        </div>
                    </>
                )}

                {/* TELA DE CRIAÇÃO DE NOVO QUIZ */}
                {modoCriacao && !criandoPergunta && (
                    <div className="goal-card" style={{ maxWidth: '650px', margin: '40px auto' }}>
                        <div className="goal-card__content">
                            <h2 style={{ fontSize: '1.5rem', marginBottom: '20px' }}>Novo Quiz</h2>
                            
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '8px', fontSize: '14px' }}>Título do Quiz</label>
                                    <input 
                                        type="text" 
                                        placeholder="Ex: Conhecimentos sobre Harry Potter"
                                        style={{ width: '100%', padding: '12px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(255,255,255,0.05)', color: 'white' }}
                                        value={novoQuiz.titulo}
                                        onChange={(e) => setNovoQuiz({...novoQuiz, titulo: e.target.value})}
                                    />
                                </div>

                                <div>
                                    <label style={{ display: 'block', marginBottom: '8px', fontSize: '14px' }}>Descrição (opcional)</label>
                                    <textarea 
                                        placeholder="Descreva seu quiz..."
                                        rows="3"
                                        style={{ width: '100%', padding: '12px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(255,255,255,0.05)', color: 'white', resize: 'vertical' }}
                                        value={novoQuiz.descricao}
                                        onChange={(e) => setNovoQuiz({...novoQuiz, descricao: e.target.value})}
                                    />
                                </div>
                                
                                <div style={{ border: '2px dashed rgba(255,255,255,0.2)', padding: '20px', borderRadius: '12px', marginTop: '10px' }}>
                                    {novoQuiz.perguntas.length === 0 ? (
                                        <div style={{ textAlign: 'center' }}>
                                            <p style={{ color: 'var(--muted)', marginBottom: '15px' }}>Nenhuma pergunta adicionada ainda.</p>
                                            <button 
                                                className="goal-deadline-button" 
                                                style={{ background: 'white', color: 'black', margin: '0 auto' }}
                                                onClick={() => setCriandoPergunta(true)}
                                            >
                                                + Adicionar Pergunta
                                            </button>
                                        </div>
                                    ) : (
                                        <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                            {novoQuiz.perguntas.map((p, index) => (
                                                <div key={index} style={{ background: 'rgba(255,255,255,0.05)', padding: '12px 15px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.1)' }}>
                                                    <strong style={{ color: 'var(--brand)' }}>Pergunta {index + 1}:</strong> {p.enunciado}
                                                </div>
                                            ))}
                                            <button 
                                                className="goal-deadline-button" 
                                                style={{ background: 'transparent', border: '1px dashed rgba(255,255,255,0.2)', color: 'white', width: '100%', justifyContent: 'center', marginTop: '10px' }}
                                                onClick={() => setCriandoPergunta(true)}
                                            >
                                                + Adicionar Outra Pergunta
                                            </button>
                                        </div>
                                    )}
                                </div>
                            </div>
                        </div>

                        <div className="goal-card__actions" style={{ display: 'flex', gap: '15px', marginTop: '30px' }}>
                            <button 
                                className="goal-deadline-button" 
                                style={{ flex: 1, padding: '12px', justifyContent: 'center' }}
                                onClick={() => setModoCriacao(false)}
                            >
                                Cancelar
                            </button>
                            <button 
                                className="goal-deadline-button" 
                                style={{ flex: 1, padding: '12px', justifyContent: 'center', background: 'var(--brand)', color: 'white', opacity: novoQuiz.perguntas.length === 0 ? 0.5 : 1 }}
                                disabled={novoQuiz.perguntas.length === 0}
                                onClick={finalizarPublicacao} 
                            >
                                Publicar Quiz
                            </button>
                        </div>
                        {novoQuiz.perguntas.length === 0 && (
                            <p style={{ textAlign: 'center', color: '#ff6975', fontSize: '12px', marginTop: '15px' }}>
                                Você precisa adicionar ao menos uma pergunta para publicar.
                            </p>
                        )}
                    </div>
                )}

                {/* TELA DE CRIAÇÃO DE NOVA PERGUNTA */}
                {modoCriacao && criandoPergunta && (
                    <div className="goal-card" style={{ maxWidth: '650px', margin: '40px auto' }}>
                        <div className="goal-card__content">
                            <button onClick={() => setCriandoPergunta(false)} style={{ background: 'none', border: 'none', color: 'var(--muted)', marginBottom: '20px', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '5px' }}>
                                ← Voltar
                            </button>
                            <h2 style={{ fontSize: '1.5rem', marginBottom: '20px' }}>Nova Pergunta</h2>
                            
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '8px', fontSize: '14px' }}>Texto da Pergunta</label>
                                    <input 
                                        type="text" 
                                        placeholder="Digite sua pergunta aqui..."
                                        style={{ width: '100%', padding: '12px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.1)', background: 'rgba(255,255,255,0.05)', color: 'white' }}
                                        value={perguntaEdit.enunciado}
                                        onChange={(e) => setPerguntaEdit({...perguntaEdit, enunciado: e.target.value})}
                                    />
                                </div>

                                <div>
                                    <label style={{ display: 'block', marginBottom: '8px', fontSize: '14px' }}>Alternativas</label>
                                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                        {perguntaEdit.alternativas.map((alt, index) => (
                                            <div key={index} style={{ display: 'flex', alignItems: 'center', gap: '10px', background: 'rgba(255,255,255,0.02)', padding: '10px', borderRadius: '8px', border: '1px solid rgba(255,255,255,0.05)' }}>
                                                <input 
                                                    type="radio" 
                                                    name="altCorreta" 
                                                    checked={alt.correta}
                                                    onChange={() => marcarComoCorreta(index)}
                                                    style={{ cursor: 'pointer', width: '18px', height: '18px', accentColor: 'var(--brand)' }}
                                                />
                                                <input 
                                                    type="text" 
                                                    placeholder={`Alternativa ${index + 1}`}
                                                    style={{ flex: 1, background: 'transparent', border: 'none', color: 'white', outline: 'none' }}
                                                    value={alt.texto}
                                                    onChange={(e) => atualizarTextoAlternativa(index, e.target.value)}
                                                />
                                            </div>
                                        ))}
                                    </div>
                                    <button 
                                        onClick={adicionarAlternativa}
                                        style={{ background: 'transparent', border: '1px dashed rgba(255,255,255,0.2)', color: 'white', padding: '10px', borderRadius: '8px', width: '100%', marginTop: '10px', cursor: 'pointer' }}
                                    >
                                        + Adicionar Alternativa
                                    </button>
                                </div>

                                <div style={{ background: 'rgba(255,255,255,0.03)', padding: '15px', borderRadius: '8px', marginTop: '10px' }}>
                                    <p style={{ fontSize: '13px', fontWeight: 'bold', marginBottom: '8px' }}>Regras para a pergunta:</p>
                                    <ul style={{ fontSize: '12px', display: 'flex', flexDirection: 'column', gap: '5px', paddingLeft: '20px' }}>
                                        <li style={{ color: temMaisDeUmaAlternativa ? '#65dc82' : '#ff6975' }}>Deve ter mais de 1 alternativa</li>
                                        <li style={{ color: temAlternativaCorreta ? '#65dc82' : '#ff6975' }}>Deve ter 1 (apenas uma) alternativa correta</li>
                                    </ul>
                                </div>
                            </div>
                        </div>

                        <div className="goal-card__actions" style={{ marginTop: '20px' }}>
                            <button 
                                className="goal-deadline-button" 
                                style={{ width: '100%', padding: '14px', justifyContent: 'center', background: 'var(--brand)', color: 'white', opacity: perguntaValida ? 1 : 0.5 }}
                                disabled={!perguntaValida}
                                onClick={salvarPerguntaNova}
                            >
                                Salvar Pergunta
                            </button>
                        </div>
                    </div>
                )}

                {/* JOGO EM ANDAMENTO */}
                {quizAtivo && !concluido && (
                    <div className="goal-card" style={{ maxWidth: '650px', margin: '40px auto' }}>
                        
                        <div className="goal-card__top">
                            <div className="goal-card__icon">
                                <FiHelpCircle />
                            </div>
                            <div className="system-goal-pill">
                                PERGUNTA {perguntaAtual + 1} DE {quizAtivo.perguntas.length}
                            </div>
                        </div>

                        <div className="goal-card__content">
                            <div className="goal-card__progress-label">
                                <span>Progresso do Quiz</span>
                                <strong>{Math.round(progressoAtual)}%</strong>
                            </div>
                            <div className="goal-card__track" style={{ marginBottom: '30px' }}>
                                <div style={{ width: `${progressoAtual}%` }}></div>
                            </div>

                            <h2 style={{ fontSize: '1.4rem', marginBottom: '30px' }}>
                                {quizAtivo.perguntas[perguntaAtual].enunciado}
                            </h2>

                            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                                {quizAtivo.perguntas[perguntaAtual].alternativas.map((alt, index) => {
                                    
                                    let backgroundBotao = 'rgba(255, 255, 255, .045)';
                                    let corBorda = 'rgba(255, 255, 255, .13)';
                                    let icone = null;

                                    if (respostaSelecionada !== null) {
                                        if (alt.correta) {
                                            backgroundBotao = 'rgba(70, 211, 105, .09)';
                                            corBorda = 'rgba(70, 211, 105, .45)';
                                            icone = <FiCheckCircle color="#65dc82" />;
                                        } else if (respostaSelecionada === index) {
                                            backgroundBotao = 'rgba(229, 9, 20, .09)';
                                            corBorda = 'rgba(229, 9, 20, .45)';
                                            icone = <FiXCircle color="#ff6975" />;
                                        }
                                    }

                                    return (
                                        <button 
                                            key={index} 
                                            disabled={respostaSelecionada !== null}
                                            onClick={() => responder(index, alt.correta)}
                                            style={{ 
                                                display: 'flex', 
                                                alignItems: 'center', 
                                                justifyContent: 'space-between',
                                                padding: '16px 20px', 
                                                background: backgroundBotao,
                                                border: `1px solid ${corBorda}`,
                                                borderRadius: '12px',
                                                color: 'white',
                                                cursor: respostaSelecionada === null ? 'pointer' : 'default',
                                                textAlign: 'left',
                                                fontSize: '15px',
                                                transition: 'all 0.2s ease'
                                            }}
                                            onMouseOver={(e) => { if(respostaSelecionada === null) e.target.style.background = 'rgba(255, 255, 255, .08)' }}
                                            onMouseOut={(e) => { if(respostaSelecionada === null) e.target.style.background = backgroundBotao }}
                                        >
                                            <span>{alt.texto}</span>
                                            {icone}
                                        </button>
                                    );
                                })}
                            </div>
                        </div>

                        {respostaSelecionada !== null && (
                            <div className="goal-card__actions" style={{ marginTop: '30px' }}>
                                <button 
                                    className="goal-deadline-button" 
                                    style={{ background: 'var(--brand)', color: 'white', width: '100%', justifyContent: 'center', padding: '14px', fontSize: '15px' }} 
                                    onClick={proximaPergunta}
                                >
                                    {perguntaAtual + 1 === quizAtivo.perguntas.length ? 'VER RESULTADO' : 'PRÓXIMA PERGUNTA'} <FiArrowRight />
                                </button>
                            </div>
                        )}
                    </div>
                )}

                {/* RESULTADO FINAL */}
                {concluido && (
                    <div className="goal-card" style={{ maxWidth: '500px', margin: '40px auto', alignItems: 'center', textAlign: 'center' }}>
                        
                        <div className="goal-card__icon" style={{ width: '80px', height: '80px', fontSize: '40px', marginBottom: '20px', color: '#f6c969', background: 'rgba(245, 180, 60, .09)' }}>
                            <FiAward />
                        </div>
                        
                        <div className="goal-card__content">
                            <h2>Quiz Concluído!</h2>
                            <p style={{ color: 'var(--muted)', fontSize: '15px', marginTop: '10px' }}>
                                Você acertou <strong style={{ color: 'white' }}>{pontuacao}</strong> de <strong style={{ color: 'white' }}>{quizAtivo.perguntas.length}</strong> perguntas.
                            </p>
                        </div>
                        
                        <div className="goal-card__actions" style={{ justifyContent: 'center', gap: '15px', width: '100%', marginTop: '30px' }}>
                            <button 
                                className="goal-deadline-button" 
                                style={{ flex: 1, justifyContent: 'center', padding: '12px' }} 
                                onClick={() => { setQuizAtivo(null); setConcluido(false); setPerguntaAtual(0); }}
                            >
                                Voltar à lista
                            </button>
                            <button 
                                className="goal-deadline-button" 
                                style={{ flex: 1, justifyContent: 'center', padding: '12px', background: 'white', color: 'black' }} 
                                onClick={() => iniciarQuiz(quizAtivo)}
                            >
                                <FiRotateCcw /> Jogar de Novo
                            </button>
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
}