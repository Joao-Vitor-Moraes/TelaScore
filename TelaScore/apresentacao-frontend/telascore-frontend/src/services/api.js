import { obterTokenSessao } from './sessaoStorage';

const BASE_URL = process.env.REACT_APP_API_URL !== undefined
    ? process.env.REACT_APP_API_URL
    : 'http://localhost:8080';

async function request(method, path, body, extraHeaders = {}) {
  const token = obterTokenSessao();
  const options = {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...extraHeaders,
    },
  };
  if (body) options.body = JSON.stringify(body);
  const res = await fetch(`${BASE_URL}${path}`, options);
  if (!res.ok) {
    const text = await res.text();
    let msg = `Erro ${res.status}`;
    try { const json = JSON.parse(text); if (json.mensagem) msg = json.mensagem; }
    catch { if (text) msg = text; }
    throw new Error(msg);
  }
  if (res.status === 204 || (res.status === 201 && res.headers.get('content-length') === '0')) return null;
  const text = await res.text();
  if (!text) return null;
  const contentType = res.headers.get('content-type') || '';
  return contentType.includes('application/json') ? JSON.parse(text) : text;
}

async function upload(path, file) {
  const token = obterTokenSessao();
  const formData = new FormData();
  formData.append('arquivo', file);
  const res = await fetch(`${BASE_URL}${path}`, {
    method: 'POST',
    headers: token ? { Authorization: `Bearer ${token}` } : {},
    body: formData,
  });
  if (!res.ok) {
    const text = await res.text();
    let msg = `Erro ${res.status}`;
    try { const json = JSON.parse(text); if (json.mensagem) msg = json.mensagem; }
    catch { if (text) msg = text; }
    throw new Error(msg);
  }
  return res.json();
}

// Listas
export const listaService = {
  listarPublicas: () => request('GET', '/api/listas/publicas'),
  listarPorUsuario: (usuarioId, quemPedeId) => request('GET', `/api/listas?usuarioId=${usuarioId}${quemPedeId != null ? `&quemPedeId=${quemPedeId}` : ''}`),
  obter: (listaId, quemPedeId) => request('GET', `/api/listas/${listaId}${quemPedeId != null ? `?quemPedeId=${quemPedeId}` : ''}`),
  criar: (comando) => request('POST', '/api/listas', comando),
  editar: (listaId, body) => request('PUT', `/api/listas/${listaId}`, body),
  remover: (listaId, usuarioId) => request('DELETE', `/api/listas/${listaId}?usuarioId=${usuarioId}`),
  consultarItens: (listaId) => request('GET', `/api/listas/${listaId}/itens`),
  adicionarFilme: (listaId, body) => request('POST', `/api/listas/${listaId}/filmes`, body),
  removerFilme: (listaId, filmeId, usuarioId) => request('DELETE', `/api/listas/${listaId}/filmes/${filmeId}?usuarioId=${usuarioId}`),
  reordenarFilme: (listaId, filmeId, body) => request('PATCH', `/api/listas/${listaId}/filmes/${filmeId}/posicao`, body),
  tornarColaborativa: (listaId, usuarioId) => request('PATCH', `/api/listas/${listaId}/colaborativa`, { usuarioId }),
  adicionarColaborador: (listaId, body) => request('POST', `/api/listas/${listaId}/colaboradores`, body),
  removerColaborador: (listaId, colaboradorId, donoId) => request('DELETE', `/api/listas/${listaId}/colaboradores/${colaboradorId}?donoId=${donoId}`),
  registrarAssistido: (listaId, filmeId, usuarioId) => request('PATCH', `/api/listas/${listaId}/filmes/${filmeId}/assistido`, { usuarioId }),
};

// Solicitações
export const solicitacaoService = {
  criar: (comando) => request('POST', '/api/solicitacoes', comando),
  obter: (id) => request('GET', `/api/solicitacoes/${id}`),
  cancelar: (id, usuarioId) => request('DELETE', `/api/solicitacoes/${id}?usuarioId=${usuarioId}`),
  avaliar: (id, avaliadorId, aprovar) => request('PATCH', `/api/solicitacoes/${id}/avaliar`, { solicitacaoId: id, avaliadorId, aprovar }),
  solicitarAjustes: (id, avaliadorId, feedback) => request('PATCH', `/api/solicitacoes/${id}/ajustes`, { solicitacaoId: id, avaliadorId, feedback }),
  editar: (id, body) => request('PUT', `/api/solicitacoes/${id}`, body),
  listarPorSolicitante: (solicitanteId) => request('GET', `/api/solicitacoes?solicitanteId=${solicitanteId}`),
  listarPorStatus: (status) => request('GET', `/api/solicitacoes?status=${status}`),
};

// Filmes
export const filmeService = {
  listar: () => request('GET', '/filmes'),
  listarGeneros: () => request('GET', '/filmes/generos'),
  obter: (id) => request('GET', `/filmes/${id}`),
  cadastrar: (dados) => request('POST', '/filmes', dados),
  atualizar: (id, dados) => request('PUT', `/filmes/${id}`, dados),
  remover: (id) => request('DELETE', `/filmes/${id}`),
};

// Auth
export const authService = {
  login: (email, senha) => request('POST', '/api/identidade/usuario/login', { email, senha }),
  registrar: (dados) => request('POST', '/api/identidade/usuario/registrar', dados),
};

// Avaliações
export const avaliacaoService = {
  listarPorFilme: (filmeId, usuarioId) => {
    const query = usuarioId ? `?usuarioId=${usuarioId}` : '';
    return request('GET', `/avaliacoes/filme/${filmeId}${query}`);
  },
  avaliar: (dados) => request('POST', '/avaliacoes', dados),
  atualizar: (id, dados) => request('PUT', `/avaliacoes/${id}`, dados),
  remover: (id) => request('DELETE', `/avaliacoes/${id}`),
};

// Usuários
export const usuarioService = {
  meuUsuario: () => request('GET', '/api/identidade/usuario/meu-usuario'),
  editarMeuUsuario: (dados) => request('PUT', '/api/identidade/usuario/meu-usuario', dados),
  enviarAvatar: (arquivo) => upload('/api/identidade/usuario/meu-usuario/avatar', arquivo),
  listar: () => request('GET', '/api/identidade/usuario'),
  obterPorId: (id) => request('GET', `/api/identidade/usuario/${id}`),
  obter: (id) => request('GET', `/api/identidade/usuario/${id}`),
  editar: (id, dados) => request('PUT', `/api/identidade/usuario/${id}`, dados),
  remover: (id) => request('DELETE', `/api/identidade/usuario/${id}`),
  buscarPorApelido: (apelido) =>
      request('GET', `/api/identidade/usuario/buscar?apelido=${encodeURIComponent(apelido)}`),
};

// Eventos
export const eventoService = {
  listar: (usuarioId) => request('GET', `/api/eventos?usuarioId=${usuarioId}`),
  obter: (id, usuarioId) => request('GET', `/api/eventos/${id}${usuarioId != null ? `?usuarioId=${usuarioId}` : ''}`),
  agendar: (comando) => request('POST', '/api/eventos', comando),
  responder: (id, usuarioId, resposta) => request('POST', `/api/eventos/${id}/resposta`, { usuarioId, resposta }),
  cancelar: (id) => request('DELETE', `/api/eventos/${id}`),
  amigos: (usuarioId) => request('GET', `/api/eventos/amigos?usuarioId=${usuarioId}`),
};

// Calendário de estreias
export const calendarioService = {
  criar: (usuarioId) => request('POST', '/api/calendarios', { usuarioId }),
  obter: (usuarioId) => request('GET', `/api/calendarios/${usuarioId}`),
  registrarFilme: (usuarioId, body) => request('POST', `/api/calendarios/${usuarioId}/filmes`, body),
  removerFilme: (usuarioId, filmeId) => request('DELETE', `/api/calendarios/${usuarioId}/filmes/${filmeId}`),
  alternarLembrete: (usuarioId, filmeId) => request('PATCH', `/api/calendarios/${usuarioId}/filmes/${filmeId}/lembrete`),
  dispararLembretes: (usuarioId, data) => request('POST', `/api/calendarios/${usuarioId}/lembretes?data=${data}`),
};

// Denúncias
export const denunciaService = {
  registrar: (dados) => request('POST', '/api/denuncias', dados),
  listarMinhas: () => request('GET', '/api/denuncias/minhas'),
  listarTodas: () => request('GET', '/api/denuncias'),
  listarPorStatus: (status) => request('GET', `/api/denuncias?status=${encodeURIComponent(status)}`),
  listarPendentes: () => request('GET', '/api/denuncias/pendentes'),
  avaliar: (id, decisao) => request('PATCH', `/api/denuncias/${id}/avaliar`, { decisao }),
};

// Metas
export const metaService = {
  listar: () => request('GET', '/api/metas'),
  listarPorUsuario: (usuarioId) => request('GET', `/api/metas/usuario/${usuarioId}`),
  criar: (dados) => request('POST', '/api/metas', dados),
  editar: (id, dados) => request('PUT', `/api/metas/${id}`, dados),
  alternarNotificacao: (id, ativa) => request('PATCH', `/api/metas/${id}/notificacao`, { ativa }),
  remover: (id) => request('DELETE', `/api/metas/${id}`),
  adicionarProgresso: (id, quantity, modo = 'FEEDBACK') =>
      request('PUT', `/api/metas/${id}/progresso?quantidade=${quantity}&modo=${modo}`),
  removerProgresso: (id, quantity) => request('PUT', `/api/metas/${id}/progresso/remover?quantidade=${quantity}`),
  estenderPrazo: (metaId, novoPrazo) => request('PUT', '/api/metas/prazo', { metaId, novoPrazo }),
  pontuacao: () => request('GET', '/api/metas/pontuacao'),
  listarNotificacoes: () => request('GET', '/api/metas/notificacoes'),
  visualizarNotificacao: (id) => request('PUT', `/api/metas/notificacoes/${id}/visualizar`),
  listarSistema: () => request('GET', '/api/metas/sistema'),
  criarSistema: (dados) => request('POST', '/api/metas/sistema', dados),
};

// Recomendações
export const recomendacaoService = {
  listar: () => request('GET', '/api/recomendacoes'),
  listarEnviadas: () => request('GET', '/api/recomendacoes/enviadas'),
  enviar: (dados) => request('POST', '/api/recomendacoes', dados),
  visualizar: (recomendacaoId) => request('PUT', `/api/recomendacoes/${recomendacaoId}/visualizar`),
  avaliarPosteriormente: (recomendacaoId, dados) =>
      request('POST', `/api/recomendacoes/${recomendacaoId}/avaliacao`, dados),
  responder: (recomendacaoId, resposta, comentario) =>
      request('PUT', '/api/recomendacoes/reagir', { recomendacaoId, resposta, comentario: comentario || null }),
};

// Comunidades
export const comunidadeService = {
  listarTodas: () => request('GET', '/api/comunidades'),
  buscarComunidadesDoUsuario: (usuarioId) => request('GET', `/api/comunidades?usuarioId=${usuarioId}`),
  listarMensagens: (comunidadeId) => request('GET', `/api/comunidades/${comunidadeId}/mensagens`),
  listarMembros: (comunidadeId) => request('GET', `/api/comunidades/${comunidadeId}/membros`),
  criar: (dados) => request('POST', '/api/comunidades', dados),
  entrar: (comunidadeId, dados) => request('POST', `/api/comunidades/${comunidadeId}/membros`, dados),
  enviarMensagem: (dados) => request('POST', `/api/comunidades/${dados.comunidadeId}/mensagens`, dados),
  removerMembro: (comunidadeId, usuarioId, operadorId) => request('DELETE', `/api/comunidades/${comunidadeId}/membros/${usuarioId}`, null, { 'X-Usuario-Id': operadorId }),
  promoverMembro: (comunidadeId, usuarioId, operadorId) => request('PUT', `/api/comunidades/${comunidadeId}/membros/${usuarioId}/promover`, null, { 'X-Usuario-Id': operadorId }),
  rebaixarMembro: (comunidadeId, usuarioId, operadorId) => request('PUT', `/api/comunidades/${comunidadeId}/membros/${usuarioId}/rebaixar`, null, { 'X-Usuario-Id': operadorId }),
  excluirComunidade: (comunidadeId, usuarioId) => request('DELETE', `/api/comunidades/${comunidadeId}`, null, { 'X-Usuario-Id': usuarioId }),
};

// Notícias
export const noticiaService = {
  pesquisar: (termo, categoria) => {
    const query = [];
    if (termo) query.push(`termo=${encodeURIComponent(termo)}`);
    if (categoria) query.push(`categoria=${encodeURIComponent(categoria)}`);
    const queryString = query.length > 0 ? `?${query.join('&')}` : '';
    return request('GET', `/noticias${queryString}`);
  },
  criar: (dados) => request('POST', '/noticias', dados),
  remover: (id) => request('DELETE', `/noticias/${id}`),
};

// Quizzes
export const quizService = {
  listar: () => request('GET', '/api/quizzes'),
  obter: (id) => request('GET', `/api/quizzes/${id}`),
  criar: (dados) => request('POST', '/api/quizzes', dados),
  responder: (id, dados) => request('POST', `/api/quizzes/${id}/tentativas`, dados),
  remover: (id) => request('DELETE', `/api/quizzes/${id}`)
};

// Mensagens Privadas
export const mensagemPrivadaService = {
  // Busca o histórico de conversa com um usuário específico
  listarConversa: (usuarioId, amigoId) => request('GET', `/api/mensagens/privadas/${amigoId}?usuarioId=${usuarioId}`),
  listarPorDestinatario: (destinatarioId, usuarioId) => request('GET', `/api/mensagens/privadas/${destinatarioId}?usuarioId=${usuarioId}`),
  enviar: (dados) => request('POST', '/api/mensagens/privadas', dados),
  editar: (id, dados) => request('PUT', `/api/mensagens/privadas/${id}`, dados),
  remover: (id, usuarioId) => request('DELETE', `/api/mensagens/privadas/${id}?usuarioId=${usuarioId}`)
};

// Figurinhas
export const figurinhaService = {
  listar: () => request('GET', '/api/figurinhas'),
  criar: (dados) => request('POST', '/api/figurinhas', dados),
  remover: (id) => request('DELETE', `/api/figurinhas/${id}`)
};

// Amigos
export const amigoService = {
  seguir: (seguidorId, seguidoId) => request('POST', '/api/conexoes', { seguidorId, seguidoId }),
  deixarDeSeguir: (seguidoId, seguidorId) => request('DELETE', `/api/conexoes/${seguidoId}`, null, { 'X-Usuario-Id': seguidorId }),
  listarSeguindo: (usuarioId) => request('GET', `/api/conexoes/${usuarioId}/seguindo`),
  listarSeguidores: (usuarioId) => request('GET', `/api/conexoes/${usuarioId}/seguidores`),
  listarAmigos: (usuarioId) => request('GET', `/api/conexoes/${usuarioId}/amigos`),
  status: (seguidorId, seguidoId) => request('GET', `/api/conexoes/status?seguidorId=${seguidorId}&seguidoId=${seguidoId}`),
  buscarPorApelido: (apelido) => request('GET', `/api/identidade/usuario/buscar?apelido=${encodeURIComponent(apelido)}`),
};

// Nivel
export const recompensaService = {
  consultarTotal: (usuarioId) => request('GET', `/api/recompensas/total?usuarioId=${usuarioId}`),
  listarHistorico: (usuarioId) => request('GET', `/api/recompensas/historico?usuarioId=${usuarioId}`),
  conceder: (dados) => request('POST', '/api/recompensas/conceder', dados),
};
