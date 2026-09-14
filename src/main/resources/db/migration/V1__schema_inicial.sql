-- V1: schema inicial do CampusGigs
-- Usuario, Servico e Contratacao com seus relacionamentos e enums em VARCHAR

CREATE TABLE usuarios (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(255)        NOT NULL,
    email       VARCHAR(255)        NOT NULL UNIQUE,
    senha       VARCHAR(255)        NOT NULL,
    cep         VARCHAR(9)          NOT NULL,
    cidade      VARCHAR(100),
    uf          VARCHAR(2),
    papel       VARCHAR(20)         NOT NULL DEFAULT 'USER'
                CHECK (papel IN ('ADMIN', 'USER'))
);

CREATE TABLE servicos (
    id              BIGSERIAL PRIMARY KEY,
    prestador_id    BIGINT          NOT NULL REFERENCES usuarios(id),
    titulo          VARCHAR(255)    NOT NULL,
    descricao       VARCHAR(1000)   NOT NULL,
    categoria       VARCHAR(100)    NOT NULL,
    preco           NUMERIC(10, 2)  NOT NULL CHECK (preco >= 0),
    situacao        VARCHAR(20)     NOT NULL DEFAULT 'ATIVO'
                    CHECK (situacao IN ('ATIVO', 'PAUSADO', 'ENCERRADO'))
);

CREATE TABLE contratacoes (
    id                  BIGSERIAL PRIMARY KEY,
    servico_id          BIGINT          NOT NULL REFERENCES servicos(id),
    contratante_id      BIGINT          NOT NULL REFERENCES usuarios(id),
    situacao            VARCHAR(20)     NOT NULL DEFAULT 'SOLICITADA'
                        CHECK (situacao IN ('SOLICITADA', 'ACEITA', 'CONCLUIDA', 'CANCELADA')),
    data_contratacao    TIMESTAMP       NOT NULL DEFAULT now()
);

-- Índices para as buscas mais comuns do fluxo (listagem de serviços ativos,
-- histórico de contratações por usuário)
CREATE INDEX idx_servicos_situacao ON servicos(situacao);
CREATE INDEX idx_servicos_prestador ON servicos(prestador_id);
CREATE INDEX idx_contratacoes_servico ON contratacoes(servico_id);
CREATE INDEX idx_contratacoes_contratante ON contratacoes(contratante_id);
