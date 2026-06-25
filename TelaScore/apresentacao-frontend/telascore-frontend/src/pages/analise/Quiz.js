import { useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  FiAlertCircle, FiArrowLeft, FiArrowRight, FiAward, FiCheckCircle, FiEdit3,
  FiHelpCircle, FiPlayCircle, FiPlus, FiRefreshCw, FiSearch, FiTrash2, FiX, FiXCircle
} from 'react-icons/fi';
import Navbar from '../../components/Navbar';
import { useAuth } from '../../context/AuthContext';
import { quizService } from '../../services/api';
import './analise.css';

const quizInicial = { titulo: '', descricao: '', perguntas: [] };
const perguntaInicial = {
  enunciado: '',
  alternativas: [
    { texto: '', correta: true },
    { texto: '', correta: false }
  ]
};

export default function Quiz() {
  const { sessao } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const [quizzes, setQuizzes] = useState([]);
  const [busca, setBusca] = useState('');
  const [erro, setErro] = useState('');
  const [feedback, setFeedback] = useState('');
  const [carregando, setCarregando] = useState(true);

  const [modoCriacao, setModoCriacao] = useState(false);
  const [novoQuiz, setNovoQuiz] = useState(quizInicial);
  const [perguntaEdit, setPerguntaEdit] = useState(perguntaInicial);
  const [salvando, setSalvando] = useState(false);

  const [quizAtivo, setQuizAtivo] = useState(null);
  const [perguntaAtual, setPerguntaAtual] = useState(0);
  const [respostas, setRespostas] = useState({});
  const [respostaSelecionada, setRespostaSelecionada] = useState('');
  const [resultado, setResultado] = useState(null);
  const [registrandoResultado, setRegistrandoResultado] = useState(false);
  const [quizParaExcluir, setQuizParaExcluir] = useState(null);

  useEffect(() => {
    carregarQuizzes();
  }, []);

  useEffect(() => {
    const idRecomendado = Number(location.state?.conteudoRecomendadoId);
    if (!idRecomendado || carregando || quizAtivo) return;

    const quizRecomendado = quizzes.find(quiz => Number(quiz.id) === idRecomendado);
    if (quizRecomendado) {
      iniciarQuiz(quizRecomendado);
      setFeedback(`Quiz recomendado: ${quizRecomendado.titulo}`);
      navigate(location.pathname, { replace: true, state: {} });
    }
  }, [carregando, location.pathname, location.state?.conteudoRecomendadoId, navigate, quizAtivo, quizzes]);

  async function carregarQuizzes() {
    setCarregando(true);
    try {
      setErro('');
      const dados = await quizService.listar();
      setQuizzes(Array.isArray(dados) ? dados : []);
    } catch (e) {
      setErro('Não foi possível carregar os quizzes agora.');
      setQuizzes([]);
    } finally {
      setCarregando(false);
    }
  }

  const quizzesFiltrados = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    return quizzes.filter(quiz => {
      if (!termo) return true;
      return quiz.titulo?.toLowerCase().includes(termo) || quiz.descricao?.toLowerCase().includes(termo);
    });
  }, [busca, quizzes]);

  const perguntaValida = perguntaEdit.enunciado.trim()
    && perguntaEdit.alternativas.length >= 2
    && perguntaEdit.alternativas.every(alt => alt.texto.trim())
    && perguntaEdit.alternativas.filter(alt => alt.correta).length === 1;

  const quizValido = novoQuiz.titulo.trim() && novoQuiz.perguntas.length > 0;

  function alterarAlternativa(index, texto) {
    setPerguntaEdit({
      ...perguntaEdit,
      alternativas: perguntaEdit.alternativas.map((alt, i) => i === index ? { ...alt, texto } : alt)
    });
  }

  function marcarCorreta(index) {
    setPerguntaEdit({
      ...perguntaEdit,
      alternativas: perguntaEdit.alternativas.map((alt, i) => ({ ...alt, correta: i === index }))
    });
  }

  function adicionarAlternativa() {
    setPerguntaEdit({
      ...perguntaEdit,
      alternativas: [...perguntaEdit.alternativas, { texto: '', correta: false }]
    });
  }

  function removerAlternativa(index) {
    const alternativas = perguntaEdit.alternativas.filter((_, i) => i !== index);
    const temCorreta = alternativas.some(alt => alt.correta);
    setPerguntaEdit({
      ...perguntaEdit,
      alternativas: alternativas.map((alt, i) => ({ ...alt, correta: temCorreta ? alt.correta : i === 0 }))
    });
  }

  function adicionarPergunta() {
    if (!perguntaValida) return;
    setNovoQuiz({ ...novoQuiz, perguntas: [...novoQuiz.perguntas, perguntaEdit] });
    setPerguntaEdit(perguntaInicial);
  }

  function removerPergunta(index) {
    setNovoQuiz({ ...novoQuiz, perguntas: novoQuiz.perguntas.filter((_, i) => i !== index) });
  }

  async function publicarQuiz() {
    if (!quizValido) return;
    setSalvando(true);
    try {
      const criado = await quizService.criar({
        id: 0,
        titulo: novoQuiz.titulo.trim(),
        descricao: novoQuiz.descricao.trim(),
        perguntas: novoQuiz.perguntas
      });
      setQuizzes([criado, ...quizzes]);
      setNovoQuiz(quizInicial);
      setPerguntaEdit(perguntaInicial);
      setModoCriacao(false);
      setFeedback('Quiz publicado com sucesso.');
    } catch (e) {
      setErro(e.message || 'Não foi possível publicar o quiz.');
    } finally {
      setSalvando(false);
    }
  }

  async function confirmarExclusao() {
    if (!quizParaExcluir) return;
    try {
      await quizService.remover(quizParaExcluir.id);
      setQuizzes(quizzes.filter(quiz => quiz.id !== quizParaExcluir.id));
      setQuizParaExcluir(null);
      setFeedback('Quiz removido.');
    } catch (e) {
      setErro(e.message || 'Não foi possível remover o quiz.');
    }
  }

  function iniciarQuiz(quiz) {
    setQuizAtivo(quiz);
    setPerguntaAtual(0);
    setRespostaSelecionada('');
    setRespostas({});
    setResultado(null);
    setErro('');
    setFeedback('');
  }

  function escolherResposta(texto) {
    const pergunta = quizAtivo.perguntas[perguntaAtual];
    setRespostaSelecionada(texto);
    setRespostas({ ...respostas, [pergunta.enunciado]: texto });
  }

  async function avancar() {
    if (!respostaSelecionada) return;
    const proxima = perguntaAtual + 1;
    if (proxima < quizAtivo.perguntas.length) {
      setPerguntaAtual(proxima);
      setRespostaSelecionada(respostas[quizAtivo.perguntas[proxima].enunciado] || '');
      return;
    }

    setRegistrandoResultado(true);
    try {
      const respostaBackend = await quizService.responder(quizAtivo.id, {
        usuarioId: sessao?.id,
        respostas
      });
      setResultado(respostaBackend);
    } catch (e) {
      setErro(e.message || 'Não foi possível finalizar sua tentativa agora.');
    } finally {
      setRegistrandoResultado(false);
    }
  }

  function voltarLista() {
    setQuizAtivo(null);
    setResultado(null);
    setPerguntaAtual(0);
    setRespostaSelecionada('');
    setRespostas({});
  }

  const pergunta = quizAtivo?.perguntas?.[perguntaAtual];
  const progresso = quizAtivo ? ((perguntaAtual + (respostaSelecionada ? 1 : 0)) / quizAtivo.perguntas.length) * 100 : 0;
  const alternativaCorreta = pergunta?.alternativas?.find(alt => alt.correta)?.texto;
  const podeGerenciar = sessao?.papel === 'ADMIN' || sessao?.papel === 'AUTOR';

  return (
    <div className="analysis-page quiz-page">
      <Navbar />
      <main className="goals-container quiz-shell">
        {!quizAtivo && !modoCriacao && (
          <>
            <div className="goals-heading quiz-heading">
              <div>
                <p className="page-eyebrow">Desafios</p>
                <h1 className="page-title">Quizzes TelaScore</h1>
                <p className="page-description">Crie, responda e acompanhe desafios de cinema.</p>
              </div>
              {podeGerenciar && (
                <button className="btn-primary" onClick={() => setModoCriacao(true)}>
                  <FiPlus /> Criar quiz
                </button>
              )}
            </div>

            <div className="quiz-toolbar">
              <label>
                <FiSearch />
                <input value={busca} onChange={e => setBusca(e.target.value)} placeholder="Buscar por título ou descrição..." />
                {busca && <button onClick={() => setBusca('')}><FiX /></button>}
              </label>
              <button className="goal-deadline-button" onClick={carregarQuizzes}><FiRefreshCw /> Atualizar</button>
            </div>

            {erro && <div className="analysis-error">{erro}</div>}
            {feedback && <div className="analysis-feedback">{feedback}</div>}

            {carregando ? (
              <div className="recommendations-empty"><FiHelpCircle /><p>Carregando quizzes...</p></div>
            ) : quizzesFiltrados.length === 0 ? (
              <div className="quiz-empty">
                <FiHelpCircle />
                <h2>Nenhum quiz encontrado</h2>
                <p>Crie o primeiro desafio para ele aparecer aqui.</p>
              </div>
            ) : (
              <div className="quiz-grid">
                {quizzesFiltrados.map(quiz => (
                  <article key={quiz.id} className="quiz-card">
                    <div className="quiz-card__top">
                      <span><FiHelpCircle /></span>
                      <small>{quiz.perguntas?.length || 0} perguntas</small>
                    </div>
                    <h2>{quiz.titulo}</h2>
                    <p>{quiz.descricao || 'Sem descrição.'}</p>
                    <div className="quiz-card__actions">
                      <button className="btn-primary" onClick={() => iniciarQuiz(quiz)}>
                        <FiPlayCircle /> Jogar
                      </button>
                      {podeGerenciar && (
                        <button className="quiz-danger-button" onClick={() => setQuizParaExcluir(quiz)}>
                          <FiTrash2 /> Excluir
                        </button>
                      )}
                    </div>
                  </article>
                ))}
              </div>
            )}
          </>
        )}

        {modoCriacao && (
          <section className="quiz-builder">
            <div className="quiz-builder__header">
              <button className="goal-deadline-button" onClick={() => setModoCriacao(false)}><FiArrowLeft /> Voltar</button>
              <div>
                <p className="page-eyebrow">Editor</p>
                <h1 className="page-title">Criar quiz</h1>
              </div>
            </div>

            {erro && <div className="analysis-error">{erro}</div>}

            <div className="quiz-builder__grid">
              <div className="quiz-builder__panel">
                <label>
                  <span>Título</span>
                  <input value={novoQuiz.titulo} onChange={e => setNovoQuiz({ ...novoQuiz, titulo: e.target.value })} placeholder="Ex: Cinema dos anos 2000" />
                </label>
                <label>
                  <span>Descrição</span>
                  <textarea value={novoQuiz.descricao} onChange={e => setNovoQuiz({ ...novoQuiz, descricao: e.target.value })} placeholder="Conte o tema do desafio..." />
                </label>
              </div>

              <div className="quiz-builder__panel">
                <h2><FiEdit3 /> Nova pergunta</h2>
                <label>
                  <span>Enunciado</span>
                  <input value={perguntaEdit.enunciado} onChange={e => setPerguntaEdit({ ...perguntaEdit, enunciado: e.target.value })} placeholder="Digite a pergunta..." />
                </label>

                <div className="quiz-options-editor">
                  {perguntaEdit.alternativas.map((alt, index) => (
                    <div key={index} className="quiz-option-edit">
                      <input type="radio" checked={alt.correta} onChange={() => marcarCorreta(index)} />
                      <input value={alt.texto} onChange={e => alterarAlternativa(index, e.target.value)} placeholder={`Alternativa ${index + 1}`} />
                      {perguntaEdit.alternativas.length > 2 && (
                        <button onClick={() => removerAlternativa(index)}><FiX /></button>
                      )}
                    </div>
                  ))}
                </div>

                <div className="quiz-builder__actions">
                  <button className="goal-deadline-button" onClick={adicionarAlternativa}><FiPlus /> Alternativa</button>
                  <button className="btn-primary" disabled={!perguntaValida} onClick={adicionarPergunta}><FiPlus /> Adicionar pergunta</button>
                </div>
              </div>
            </div>

            <section className="quiz-question-list">
              <div>
                <h2>Perguntas adicionadas</h2>
                <small>{novoQuiz.perguntas.length} pergunta(s)</small>
              </div>
              {novoQuiz.perguntas.length === 0 ? (
                <p>Nenhuma pergunta adicionada ainda.</p>
              ) : novoQuiz.perguntas.map((p, index) => (
                <article key={`${p.enunciado}-${index}`}>
                  <strong>{index + 1}. {p.enunciado}</strong>
                  <button onClick={() => removerPergunta(index)}><FiTrash2 /></button>
                </article>
              ))}
            </section>

            <div className="quiz-publish-bar">
              <button className="goal-deadline-button" onClick={() => setModoCriacao(false)}>Cancelar</button>
              <button className="btn-primary" disabled={!quizValido || salvando} onClick={publicarQuiz}>
                {salvando ? 'Publicando...' : 'Publicar quiz'}
              </button>
            </div>
          </section>
        )}

        {quizAtivo && !resultado && pergunta && (
          <section className="quiz-player">
            <button className="goal-deadline-button" onClick={voltarLista}><FiArrowLeft /> Sair</button>
            <div className="quiz-player__card">
              <div className="quiz-player__header">
                <div>
                  <p className="page-eyebrow">Pergunta {perguntaAtual + 1} de {quizAtivo.perguntas.length}</p>
                  <h1>{quizAtivo.titulo}</h1>
                </div>
                <span>{Math.round(progresso)}%</span>
              </div>
              <div className="goal-card__track"><div style={{ width: `${progresso}%` }} /></div>
              <h2>{pergunta.enunciado}</h2>

              <div className="quiz-answer-grid">
                {pergunta.alternativas.map(alt => {
                  const selecionada = respostaSelecionada === alt.texto;
                  const revelada = Boolean(respostaSelecionada);
                  const classe = revelada && alt.correta ? 'is-correct' : revelada && selecionada ? 'is-wrong' : '';
                  return (
                    <button key={alt.texto} className={`${selecionada ? 'is-selected' : ''} ${classe}`} disabled={revelada} onClick={() => escolherResposta(alt.texto)}>
                      <span>{alt.texto}</span>
                      {revelada && alt.correta && <FiCheckCircle />}
                      {revelada && selecionada && !alt.correta && <FiXCircle />}
                    </button>
                  );
                })}
              </div>

              {respostaSelecionada && (
                <div className="quiz-answer-feedback">
                  {respostaSelecionada === alternativaCorreta ? <FiCheckCircle /> : <FiAlertCircle />}
                  <span>{respostaSelecionada === alternativaCorreta ? 'Resposta correta.' : `Resposta correta: ${alternativaCorreta}`}</span>
                </div>
              )}

              <button className="btn-primary quiz-next-button" disabled={!respostaSelecionada || registrandoResultado} onClick={avancar}>
                {registrandoResultado ? 'Registrando...' : perguntaAtual + 1 === quizAtivo.perguntas.length ? 'Ver resultado' : 'Próxima pergunta'} <FiArrowRight />
              </button>
            </div>
          </section>
        )}

        {quizAtivo && resultado && (
          <section className="quiz-result">
            <div className="quiz-result__icon"><FiAward /></div>
            <p className="page-eyebrow">Resultado registrado</p>
            <h1>{resultado.aprovado ? 'Mandou bem!' : 'Quase lá'}</h1>
            <p>Você acertou <strong>{resultado.acertos}</strong> de <strong>{resultado.totalPerguntas}</strong> perguntas.</p>
            <div className="quiz-result__score">
              <span>{Math.round(resultado.percentual)}%</span>
              <small>{resultado.pontuacao} pontos</small>
            </div>
            <div className="quiz-result__actions">
              <button className="goal-deadline-button" onClick={voltarLista}>Voltar para lista</button>
              <button className="btn-primary" onClick={() => iniciarQuiz(quizAtivo)}><FiRefreshCw /> Jogar de novo</button>
            </div>
          </section>
        )}
      </main>

      {quizParaExcluir && (
        <div className="analysis-modal-backdrop" onMouseDown={() => setQuizParaExcluir(null)}>
          <section className="analysis-modal news-confirm-modal" role="dialog" aria-modal="true" onMouseDown={e => e.stopPropagation()}>
            <button className="analysis-modal__close" onClick={() => setQuizParaExcluir(null)}><FiX /></button>
            <div className="analysis-modal__header">
              <span><FiTrash2 /></span>
              <div><p className="page-eyebrow">Exclusao</p><h2>Remover quiz?</h2></div>
            </div>
            <div className="news-confirm-modal__body">
              <p>Esse quiz sera apagado permanentemente.</p>
              <strong>{quizParaExcluir.titulo}</strong>
            </div>
            <div className="analysis-modal__footer">
              <button className="btn-secondary" onClick={() => setQuizParaExcluir(null)}>Cancelar</button>
              <button className="btn-primary" onClick={confirmarExclusao}>Apagar quiz</button>
            </div>
          </section>
        </div>
      )}
    </div>
  );
}
