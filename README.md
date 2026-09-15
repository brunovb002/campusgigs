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

## Evidências de teste manual

Ver histórico de commits para a justificativa de cada checkpoint.

### CP2 — cadastro e autenticação

```
1) Cadastro (POST /auth/cadastro)                         -> 201 Created
2) GET /auth/me sem credenciais                            -> 401 Unauthorized  (acesso negado)
3) GET /auth/me com email+senha corretos (HTTP Basic)       -> 200 OK
4) GET /auth/me com senha errada                            -> 401 Unauthorized  (acesso negado)
5) POST /auth/cadastro com email já usado                   -> 409 Conflict
6) POST /auth/cadastro com dados inválidos (nome/email/senha/cep) -> 400 Bad Request
```

Senha conferida diretamente no banco: armazenada como hash BCrypt (`$2a$10$...`), nunca em texto puro.

### CP3 — emissão e validação de JWT

```
1) Cadastro (POST /auth/cadastro)                     -> 201 Created
2) POST /auth/login com credenciais corretas           -> 200 OK + token JWT
3) POST /auth/login com senha errada                   -> 401 Unauthorized
4) POST /auth/login com email inexistente              -> 401 Unauthorized (mesma mensagem genérica)
5) GET /auth/me com token válido                       -> 200 OK
6) GET /auth/me sem token                              -> 401 Unauthorized (acesso negado)
7) GET /auth/me com token adulterado                   -> 401 Unauthorized (acesso negado)
8) GET /auth/me com header sem o prefixo "Bearer "     -> 401 Unauthorized (acesso negado)
```

### CP4 — regras de autorização por papel

Cenário: Ana publica um serviço; Bruno é um USER comum sem relação com ele; Carla é ADMIN (promovida direto no
banco — não existe endpoint público para virar ADMIN, de propósito).

```
1) Ana publica um serviço (autenticado pode publicar)               -> 201 Created
2) Publicar SEM token                                                -> 401 Unauthorized
3) Listar serviços SEM token (leitura é pública)                    -> 200 OK
4) Bruno (não-dono) tenta EDITAR o serviço da Ana                    -> 403 Forbidden  [acesso negado por papel]
5) Bruno (não-dono) tenta ENCERRAR o serviço da Ana                  -> 403 Forbidden  [acesso negado por papel]
6) Ana (dona) edita o próprio serviço                                -> 200 OK
7) Carla (ADMIN, não-dona) ENCERRA o serviço da Ana                  -> 200 OK   (exceção do ADMIN)
8) Carla (ADMIN, não-dona) tenta EDITAR o serviço da Ana             -> 403 Forbidden  [ADMIN não tem exceção pra editar]
9) Bruno tenta contratar o serviço da Ana já ENCERRADO               -> 409 Conflict
10) Ana tenta contratar o PRÓPRIO serviço                            -> 409 Conflict
11) Bruno contrata um serviço ATIVO da Ana                           -> 201 Created
```

O caso 4 (e o 5, e o 8) são os casos de **acesso negado por papel** exigidos na entrega: um USER sem relação com o
serviço não pode alterá-lo, e mesmo um ADMIN — que tem uma exceção explícita para encerrar qualquer serviço — não
tem essa mesma exceção para editar.
