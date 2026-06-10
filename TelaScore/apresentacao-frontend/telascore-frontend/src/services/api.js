const BASE_URL = 'http://localhost:8080';

async function request(method, path, body) {
  const options = {
    method,
    headers: { 'Content-Type': 'application/json' },
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
  listarPorUsuario: (usuarioId) => request('GET', `/api/listas?usuarioId=${usuarioId}`),
  obter: (listaId) => request('GET', `/api/listas/${listaId}`),
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

// Filmes
export const filmeService = {
  listar: () => request('GET', '/filmes'),
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
