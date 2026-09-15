# CampusGigs — Projeto Diamante

API REST de freelas entre alunos de uma universidade. Um aluno se cadastra, publica um serviço (freela); outro aluno, autenticado, contrata esse serviço.

**Stack:** Spring Boot 3.3.4, Java 17, PostgreSQL, JWT (jjwt 0.12.6), Flyway, Docker/docker-compose, Spring HttpExchange.

## Como executar

Pré-requisitos: Docker e Docker Compose.

```bash
docker compose up -d --build
```

Isso sobe:
- `campusgigs-db`: PostgreSQL 16 na porta `5432`
- `campusgigs-api`: a API na porta `8080` (o Flyway aplica as migrations automaticamente na subida)

Para derrubar o ambiente:

```bash
docker compose down
```

Para derrubar e apagar os dados (volume do banco):

```bash
docker compose down -v
```

## Exemplo de chamada autenticada

### 1. Cadastro (endpoint público)

```bash
curl -X POST http://localhost:8080/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ana Souza",
    "email": "ana@universidade.edu",
    "senha": "senha123",
    "cep": "01310-100"
  }'
```

Resposta (`201 Created`):

```json
{
  "id": 1,
  "nome": "Ana Souza",
  "email": "ana@universidade.edu",
  "cep": "01310-100",
  "cidade": null,
  "uf": null,
  "papel": "USER"
}
```

### 2. Login (endpoint público) — obtém o token JWT

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "ana@universidade.edu", "senha": "senha123"}'
```

Resposta (`200 OK`):

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9....",
  "tipo": "Bearer",
  "expiraEmMs": 3600000
}
```

### 3. Chamada autenticada (endpoint protegido) — usando o token

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...." http://localhost:8080/auth/me
```

Resposta (`200 OK`):

```json
{
  "id": 1,
  "nome": "Ana Souza",
  "email": "ana@universidade.edu",
  "cep": "01310-100",
  "cidade": null,
  "uf": null,
  "papel": "USER"
}
```

Sem token, com token inválido/expirado, ou com senha errada no login, a API responde `401 Unauthorized`.

### 4. Publicar e contratar um serviço

```bash
# Publicar (qualquer autenticado)
curl -X POST http://localhost:8080/servicos \
  -H "Content-Type: application/json" -H "Authorization: Bearer <token>" \
  -d '{"titulo": "Aula de Cálculo", "descricao": "Reforço para cálculo 1", "categoria": "Aulas", "preco": 50.00}'

# Listar (público, sem token)
curl http://localhost:8080/servicos

# Contratar (autenticado, exceto o próprio dono do serviço)
curl -X POST http://localhost:8080/servicos/1/contratar -H "Authorization: Bearer <token-de-outro-usuario>"

# Encerrar (só o dono, ou um ADMIN)
curl -X POST http://localhost:8080/servicos/1/encerrar -H "Authorization: Bearer <token>"
```

### 5. CEP automático (HttpExchange + ViaCEP)

No cadastro (`POST /auth/cadastro`), `cidade` e `uf` são preenchidos automaticamente a partir do `cep` informado,
consultando o [ViaCEP](https://viacep.com.br) por um cliente HTTP declarativo (Spring HttpExchange). Se o CEP não
existir, o cadastro inteiro é recusado — nunca fica um usuário salvo com endereço incompleto.

```bash
curl -X POST http://localhost:8080/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{"nome": "Ana Souza", "email": "ana@universidade.edu", "senha": "senha123", "cep": "01310-100"}'
# -> cidade: "São Paulo", uf: "SP"
```

Também dá pra atualizar o CEP de um usuário já cadastrado (autenticado, só o próprio):

```bash
curl -X PATCH http://localhost:8080/usuarios/me/cep \
  -H "Content-Type: application/json" -H "Authorization: Bearer <token>" \
  -d '{"cep": "20040-020"}'
```

Um CEP com formato válido mas inexistente responde `422 Unprocessable Entity`. Se o serviço externo estiver fora do
ar ou muito lento, a API responde `503 Service Unavailable` em vez de travar ou salvar dado incompleto — a base URL
e o timeout do ViaCEP são configuráveis por variável de ambiente (`CEP_BASE_URL`, `CEP_TIMEOUT_MS`), o que permite
simular essa falha sem depender da internet cair de verdade:

```bash
# Sobe a API apontando pro CEP_BASE_URL inválido, pra simular o serviço externo fora do ar
CEP_BASE_URL=http://host-que-nao-existe.invalid:9999 CEP_TIMEOUT_MS=2000 docker compose up -d --build
```

## Evidências de teste manual

Ver histórico de commits para a justificativa de cada checkpoint. Todos os testes abaixo foram rodados manualmente
via `curl`, contra a aplicação de pé em Docker (`docker compose up -d --build`), com os prints em [`Evidencias/`](Evidencias).

### CP2 — cadastro e autenticação

| # | Teste | Esperado | Print |
|---|-------|----------|-------|
| 1 | Cadastro (`POST /auth/cadastro`) | 201 Created | [cp2-01-cadastro-sucesso.png](Evidencias/cp2-01-cadastro-sucesso.png) |
| 2 | Cadastro com email já usado | 409 Conflict | [cp2-02-cadastro-email-duplicado.png](Evidencias/cp2-02-cadastro-email-duplicado.png) |
| 3 | Cadastro com dados inválidos (nome/email/senha/cep) | 400 Bad Request | [cp2-03-cadastro-dados-invalidos.png](Evidencias/cp2-03-cadastro-dados-invalidos.png) |
| 4 | Senha conferida direto no banco (`SELECT ... FROM usuarios`) | hash BCrypt (`$2a$10$...`), nunca texto puro | [cp2-04-senha-hash-bcrypt.png](Evidencias/cp2-04-senha-hash-bcrypt.png) |

### CP3 — emissão e validação de JWT

| # | Teste | Esperado | Print |
|---|-------|----------|-------|
| 1 | `POST /auth/login` com credenciais corretas | 200 OK + token JWT | [cp3-01-login-sucesso.png](Evidencias/cp3-01-login-sucesso.png) |
| 2 | `POST /auth/login` com senha errada | 401 Unauthorized | [cp3-02-login-senha-errada.png](Evidencias/cp3-02-login-senha-errada.png) |
| 3 | `GET /auth/me` sem token | 401 Unauthorized (acesso negado) | [cp3-03-me-sem-token.png](Evidencias/cp3-03-me-sem-token.png) |
| 4 | `GET /auth/me` com token válido | 200 OK | [cp3-04-me-com-token.png](Evidencias/cp3-04-me-com-token.png) |

### CP4 — regras de autorização por papel

Cenário: **Ana** publica um serviço; **Bruno** é um USER comum sem relação com ele; **Carla** é promovida a ADMIN
direto no banco (não existe endpoint público para virar ADMIN, de propósito — promoção de papel é uma operação
administrativa fora da API pública).

| # | Teste | Esperado | Print |
|---|-------|----------|-------|
| 1 | Cadastro do Bruno | 201 Created | [cp4-01-cadastro-bruno.png](Evidencias/cp4-01-cadastro-bruno.png) |
| 2 | Ana publica um serviço | 201 Created | [cp4-02-publicar-servico.png](Evidencias/cp4-02-publicar-servico.png) |
| 3 | **Bruno (não-dono) tenta EDITAR o serviço da Ana** | **403 Forbidden — acesso negado por papel** | [cp4-03-editar-negado.png](Evidencias/cp4-03-editar-negado.png) |
| 4 | **Bruno (não-dono) tenta ENCERRAR o serviço da Ana** | **403 Forbidden — acesso negado por papel** | [cp4-04-encerrar-negado.png](Evidencias/cp4-04-encerrar-negado.png) |
| 5 | Bruno contrata o serviço (ativo) da Ana | 201 Created | [cp4-05-contratar-sucesso.png](Evidencias/cp4-05-contratar-sucesso.png) |
| 6 | Ana tenta contratar o PRÓPRIO serviço | 409 Conflict | [cp4-06-auto-contratacao-negada.png](Evidencias/cp4-06-auto-contratacao-negada.png) |
| 7 | Cadastro da Carla | 201 Created | [cp4-07-cadastro-carla.png](Evidencias/cp4-07-cadastro-carla.png) |
| 8 | Carla promovida a ADMIN direto no banco | papel = ADMIN confirmado | [cp4-08-promover-admin.png](Evidencias/cp4-08-promover-admin.png) |
| 9 | Carla (ADMIN, não-dona) ENCERRA o serviço da Ana | 200 OK — exceção do ADMIN | [cp4-09-admin-encerra-sucesso.png](Evidencias/cp4-09-admin-encerra-sucesso.png) |
| 10 | Carla (ADMIN, não-dona) tenta EDITAR o serviço da Ana | **403 Forbidden — ADMIN não tem exceção pra editar** | [cp4-10-admin-edita-negado.png](Evidencias/cp4-10-admin-edita-negado.png) |

Os testes 3, 4 e 10 são os casos de **acesso negado por papel** exigidos na entrega: um USER sem relação com o
serviço não pode alterá-lo de forma alguma, e mesmo um ADMIN — que tem uma exceção explícita para encerrar qualquer
serviço (teste 9) — não tem essa mesma exceção para editar (teste 10).

### CP5 — integração HttpExchange com o serviço externo de CEP

| # | Teste | Esperado | Print |
|---|-------|----------|-------|
| 1 | Cadastro com CEP válido | 201 Created — `cidade`/`uf` preenchidos automaticamente | [cp5-01-cadastro-cep-valido.png](Evidencias/cp5-01-cadastro-cep-valido.png) |
| 2 | Cadastro com CEP de formato válido, mas inexistente | 422 Unprocessable Entity — cadastro recusado | [cp5-02-cadastro-cep-inexistente.png](Evidencias/cp5-02-cadastro-cep-inexistente.png) |
| 3 | `PATCH /usuarios/me/cep` (autenticado) | 200 OK — `cidade`/`uf` atualizados | [cp5-03-atualizar-cep-sucesso.png](Evidencias/cp5-03-atualizar-cep-sucesso.png) |
| 4 | `PATCH /usuarios/me/cep` sem token | 401 Unauthorized | [cp5-04-atualizar-cep-sem-token.png](Evidencias/cp5-04-atualizar-cep-sem-token.png) |
| 5 | **Serviço externo de CEP indisponível** (`CEP_BASE_URL` apontando pra um host inexistente) | **503 Service Unavailable** — nada fica salvo pela metade | [cp5-05-cep-servico-indisponivel.png](Evidencias/cp5-05-cep-servico-indisponivel.png) |

O teste 5 é o caso que o enunciado pede pra entender explicitamente: quando o serviço externo falha ou demora, a API
não trava nem deixa o cadastro incompleto — recusa a operação inteira com um erro claro (`503`), e nenhum usuário é
persistido nesse cenário (conferido direto no banco durante o desenvolvimento).
