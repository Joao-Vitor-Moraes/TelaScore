import { useState } from 'react';
import { FiPlayCircle, FiAward, FiArrowRight, FiRotateCcw, FiHelpCircle, FiCheckCircle, FiXCircle } from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import './analise.css'; // <-- Puxando o CSS de Análise!

// DADOS FALSOS (MOCK) DO QUIZ E PERGUNTAS
const QUIZZES_MOCK = [
    {
        id: 1,
        titulo: "Mestre do Cinema: Christopher Nolan",
        descricao: "Você realmente entendeu Interestelar e A Origem? Teste seus neurônios!",
        perguntas: [
            {
                enunciado: "Em 'Interestelar', qual é o nome do robô que acompanha Cooper?",
                alternativas: [
                    { texto: "JARVIS", correta: false },
                    { texto: "TARS", correta: true },
                    { texto: "HAL 9000", correta: false },
                    { texto: "R2-D2", correta: false }
                ]
            },
            {
                enunciado: "Qual objeto Cobb usa como totem em 'A Origem'?",
                alternativas: [
                    { texto: "Um pião (totem giratório)", correta: true },
                    { texto: "Um dado viciado", correta: false },
                    { texto: "Uma moeda de xadrez", correta: false },
                    { texto: "Um relógio de bolso", correta: false }
                ]
            }
        ]
    },
    {
        id: 2,
        titulo: "Universo Clássico: O Poderoso Chefão",
        descricao: "Prove que você conhece a família Corleone e faça uma oferta irrecusável.",
        perguntas: [
            {
                enunciado: "Quem assume o comando da família após a morte de Vito Corleone?",
                alternativas: [
                    { texto: "Sonny Corleone", correta: false },
                    { texto: "Fredo Corleone", correta: false },
                    { texto: "Michael Corleone", correta: true },
                    { texto: "Tom Hagen", correta: false }
                ]
            }
        ]
    }
];

export default function Quiz() {
    const [quizAtivo, setQuizAtivo] = useState(null);
    const [perguntaAtual, setPerguntaAtual] = useState(0);
    const [respostaSelecionada, setRespostaSelecionada] = useState(null);
    const [pontuacao, setPontuacao] = useState(0);
    const [concluido, setConcluido] = useState(false);

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

    // Calcula a porcentagem para a barra de progresso do seu CSS
    const progressoAtual = quizAtivo ? (perguntaAtual / quizAtivo.perguntas.length) * 100 : 0;

    return (
        <div className="analysis-page">
            <Navbar />
            <main className="goals-container" style={{ margin: '0 auto' }}>
                
                {/* TELA DE LISTAGEM DOS QUIZZES */}
                {!quizAtivo && (
                    <>
                        <div className="goals-heading">
                            <div>
                                <h2 className="page-title" style={{ fontSize: '32px', margin: '0 0 10px 0' }}>Quizzes TelaScore</h2>
                                <p className="page-description">Escolha um desafio, responda perguntas e teste seu nível cinéfilo!</p>
                            </div>
                        </div>

                        {/* Usando o grid definido no seu CSS */}
                        <div className="goals-grid" style={{ marginTop: '30px' }}>
                            {QUIZZES_MOCK.map(quiz => (
                                <article key={quiz.id} className="goal-card">
                                    <div className="goal-card__top">
                                        <div className="goal-card__icon">
                                            <FiHelpCircle />
                                        </div>
                                        <div className="system-goal-pill">
                                            {quiz.perguntas.length} Perguntas
                                        </div>
                                    </div>
                                    
                                    <div className="goal-card__content">
                                        <h2>{quiz.titulo}</h2>
                                        <p style={{ color: 'var(--muted)', fontSize: '13px', lineHeight: '1.5' }}>
                                            {quiz.descricao}
                                        </p>
                                    </div>

                                    <div className="goal-card__actions">
                                        <button 
                                            className="goal-deadline-button" 
                                            style={{ background: 'var(--brand)', color: 'white', width: '100%', justifyContent: 'center', padding: '12px', fontSize: '14px' }}
                                            onClick={() => iniciarQuiz(quiz)}
                                        >
                                            <FiPlayCircle size={18} /> Jogar Agora
                                        </button>
                                    </div>
                                </article>
                            ))}
                        </div>
                    </>
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
                            {/* Barra de Progresso do seu CSS */}
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

                            {/* Alternativas */}
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                                {quizAtivo.perguntas[perguntaAtual].alternativas.map((alt, index) => {
                                    
                                    // Lógica de cores baseada no CSS do grupo
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

                        {/* Botão Próxima */}
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
                                onClick={() => setQuizAtivo(null)}
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