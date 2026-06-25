export const SESSAO_STORAGE_KEY = 'telascore_sessao';

function decodificarBase64Url(valor) {
  const base64 = valor.replace(/-/g, '+').replace(/_/g, '/');
  const padding = '='.repeat((4 - (base64.length % 4)) % 4);
  return atob(base64 + padding);
}

function lerPayloadToken(token) {
  if (!token || typeof token !== 'string') return null;
  const partes = token.split('.');
  if (partes.length !== 3) return null;

  try {
    return JSON.parse(decodificarBase64Url(partes[1]));
  } catch {
    return null;
  }
}

function tokenExpirado(payload) {
  return payload?.exp && Date.now() >= payload.exp * 1000;
}

export function normalizarSessao(dados) {
  if (!dados || typeof dados !== 'object') return null;

  const token = dados.token || dados.accessToken || dados.jwt || null;
  const payload = lerPayloadToken(token);
  if (token && tokenExpirado(payload)) return null;

  const usuario = dados.usuario || dados.user || {};
  const id = Number(dados.id ?? dados.usuarioId ?? usuario.id ?? payload?.sub);
  if (!Number.isFinite(id) || id <= 0) return null;

  const papel = dados.papel || dados.role || usuario.papel || usuario.role || payload?.papel || 'CINEFILO';

  return {
    ...usuario,
    ...dados,
    id,
    papel,
    token,
    tipoToken: dados.tipoToken || dados.tokenType || (token ? 'Bearer' : null),
    expiraEmSegundos: dados.expiraEmSegundos ?? dados.expiresIn ?? null,
  };
}

export function carregarSessaoSalva() {
  try {
    const raw = localStorage.getItem(SESSAO_STORAGE_KEY);
    const sessao = raw ? normalizarSessao(JSON.parse(raw)) : null;
    if (!sessao && raw) localStorage.removeItem(SESSAO_STORAGE_KEY);
    return sessao;
  } catch {
    localStorage.removeItem(SESSAO_STORAGE_KEY);
    return null;
  }
}

export function salvarSessao(dados) {
  const sessao = normalizarSessao(dados);
  if (!sessao) {
    localStorage.removeItem(SESSAO_STORAGE_KEY);
    return null;
  }

  localStorage.setItem(SESSAO_STORAGE_KEY, JSON.stringify(sessao));
  return sessao;
}

export function limparSessao() {
  localStorage.removeItem(SESSAO_STORAGE_KEY);
}

export function obterTokenSessao() {
  return carregarSessaoSalva()?.token || null;
}
