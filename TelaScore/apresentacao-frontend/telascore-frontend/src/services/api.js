const BASE_URL = 'http://localhost:8080';

function getToken() {
  try {
    const raw = localStorage.getItem('telascore_sessao');
    return raw ? JSON.parse(raw).token : null;
  } catch {
    return null;
  }
}

async function request(method, path, body) {
  const token = getToken();
  const options = {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  };
  if (body) options.body = JSON.stringify(body);
  const res = await fetch(`${BASE_URL}${path}`, options);
  if (!res.ok) throw new Error(`Erro ${res.status}: ${res.statusText}`);
  if (res.status === 204 || res.status === 201 && res.headers.get('content-length') === '0') return null;
  const text = await res.text();
  return text ? JSON.parse(text) : null;
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

// Filmes — substitua o filmeService existente por este:
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
  registrar: (nome, email, senha) => request('POST', '/api/identidade/usuario/registrar', { nome, email, senha }),
};

// Avaliações — adicione depois do filmeService:
export const avaliacaoService = {
  listarPorFilme: (filmeId, usuarioId) => {
    const query = usuarioId ? `?usuarioId=${usuarioId}` : '';
    return request('GET', `/avaliacoes/filme/${filmeId}${query}`);
  },
  avaliar: (dados) => request('POST', '/avaliacoes', dados),
  atualizar: (id, dados) => request('PUT', `/avaliacoes/${id}`, dados),
  remover: (id) => request('DELETE', `/avaliacoes/${id}`),
};


