const BASE_URL = process.env.REACT_APP_API_URL !== undefined
    ? process.env.REACT_APP_API_URL
    : 'http://localhost:8080';

function getToken() {
  try {
    const raw = localStorage.getItem('telascore_sessao');
    return raw ? JSON.parse(raw).token : null;
  } catch {
    return null;
  }
}

async function request(method, path, body, extraHeaders = {}) {
  const token = getToken();
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
  const token = getToken();
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
  editar: (id, dados) => request('PUT', `/api/identidade/usuario/${id}`, dados),
  remover: (id) => request('DELETE', `/api/identidade/usuario/${id}`),
  buscarPorApelido: (apelido) =>
      request('GET', `/api/identidade/usuario/buscar?apelido=${encodeURIComponent(apelido)}`),
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
  criar: (dados) => request('POST', '/api/metas', dados),
  editar: (id, dados) => request('PUT', `/api/metas/${id}`, dados),
  remover: (id) => request('DELETE', `/api/metas/${id}`),
  adicionarProgresso: (id, quantity) => request('PUT', `/api/metas/${id}/progresso?quantidade=${quantity}`),
  removerProgresso: (id, quantity) => request('PUT', `/api/metas/${id}/progresso/remover?quantidade=${quantity}`),
  estenderPrazo: (metaId, novoPrazo) => request('PUT', '/api/metas/prazo', { metaId, novoPrazo }),
  pontuacao: () => request('GET', '/api/metas/pontuacao'),
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
