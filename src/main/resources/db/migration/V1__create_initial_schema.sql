-- =============================================
-- V1__create_initial_schema.sql
-- Schema inicial do Sistema de Assistência Técnica
-- =============================================

-- USUARIOS
CREATE TABLE usuarios (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(100)        NOT NULL,
    email       VARCHAR(150)        NOT NULL UNIQUE,
    senha_hash  VARCHAR(255)        NOT NULL,
    role        VARCHAR(20)         NOT NULL DEFAULT 'TECNICO',
    ativo       BOOLEAN             NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMP           NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP         NOT NULL DEFAULT NOW()
);

-- CLIENTES
CREATE TABLE clientes (
    id            BIGSERIAL PRIMARY KEY,
    nome          VARCHAR(100)      NOT NULL,
    cpf           VARCHAR(14)       UNIQUE,
    telefone      VARCHAR(20)       NOT NULL,
    email         VARCHAR(150),
    logradouro    VARCHAR(200),
    numero        VARCHAR(10),
    complemento   VARCHAR(100),
    bairro        VARCHAR(100),
    cidade        VARCHAR(100),
    estado        VARCHAR(2),
    cep           VARCHAR(9),
    observacoes   TEXT,
    ativo         BOOLEAN           NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP         NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP         NOT NULL DEFAULT NOW()
);

-- APARELHOS
CREATE TABLE aparelhos (
    id              BIGSERIAL PRIMARY KEY,
    cliente_id      BIGINT            NOT NULL REFERENCES clientes(id),
    tipo            VARCHAR(20)       NOT NULL, -- CELULAR, NOTEBOOK, DESKTOP, TABLET, OUTRO
    marca           VARCHAR(50)       NOT NULL,
    modelo          VARCHAR(100)      NOT NULL,
    numero_serie    VARCHAR(100),
    imei            VARCHAR(20),
    cor             VARCHAR(30),
    senha           VARCHAR(50),
    observacoes     TEXT,
    criado_em       TIMESTAMP         NOT NULL DEFAULT NOW(),
    atualizado_em   TIMESTAMP         NOT NULL DEFAULT NOW()
);

-- ORDENS DE SERVICO
CREATE TABLE ordens_servico (
    id                  BIGSERIAL PRIMARY KEY,
    numero              VARCHAR(20)     NOT NULL UNIQUE,
    aparelho_id         BIGINT          NOT NULL REFERENCES aparelhos(id),
    cliente_id          BIGINT          NOT NULL REFERENCES clientes(id),
    tecnico_id          BIGINT          REFERENCES usuarios(id),
    status              VARCHAR(30)     NOT NULL DEFAULT 'RECEBIDO',
    defeito_relatado    TEXT            NOT NULL,
    laudo_tecnico       TEXT,
    observacoes_internas TEXT,
    valor_mao_obra      NUMERIC(10,2)   NOT NULL DEFAULT 0,
    valor_pecas         NUMERIC(10,2)   NOT NULL DEFAULT 0,
    desconto            NUMERIC(10,2)   NOT NULL DEFAULT 0,
    valor_total         NUMERIC(10,2)   NOT NULL DEFAULT 0,
    forma_pagamento     VARCHAR(30),    -- preenchido na conclusão
    data_previsao       DATE,
    data_conclusao      TIMESTAMP,
    data_entrega        TIMESTAMP,
    criado_em           TIMESTAMP       NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- HISTORICO DE STATUS DA OS
CREATE TABLE historico_os (
    id              BIGSERIAL PRIMARY KEY,
    ordem_id        BIGINT          NOT NULL REFERENCES ordens_servico(id),
    status_anterior VARCHAR(30),
    status_novo     VARCHAR(30)     NOT NULL,
    observacao      TEXT,
    usuario_id      BIGINT          REFERENCES usuarios(id),
    criado_em       TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- CATEGORIAS DE PECAS
CREATE TABLE categorias_peca (
    id      BIGSERIAL PRIMARY KEY,
    nome    VARCHAR(100) NOT NULL UNIQUE,
    ativo   BOOLEAN      NOT NULL DEFAULT TRUE
);

-- PECAS / ESTOQUE
CREATE TABLE pecas (
    id                  BIGSERIAL PRIMARY KEY,
    categoria_id        BIGINT          REFERENCES categorias_peca(id),
    nome                VARCHAR(150)    NOT NULL,
    descricao           TEXT,
    codigo_interno      VARCHAR(50)     UNIQUE,
    quantidade          INTEGER         NOT NULL DEFAULT 0,
    estoque_minimo      INTEGER         NOT NULL DEFAULT 1,
    preco_custo         NUMERIC(10,2)   NOT NULL DEFAULT 0,
    preco_venda         NUMERIC(10,2)   NOT NULL DEFAULT 0,
    ativo               BOOLEAN         NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMP       NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ITENS DE ORDEM DE SERVICO (pecas ou servicos usados)
CREATE TABLE itens_os (
    id              BIGSERIAL PRIMARY KEY,
    ordem_id        BIGINT          NOT NULL REFERENCES ordens_servico(id),
    peca_id         BIGINT          REFERENCES pecas(id), -- NULL se for serviço avulso
    descricao       VARCHAR(200)    NOT NULL,
    quantidade      INTEGER         NOT NULL DEFAULT 1,
    preco_unitario  NUMERIC(10,2)   NOT NULL,
    subtotal        NUMERIC(10,2)   NOT NULL,
    tipo            VARCHAR(20)     NOT NULL DEFAULT 'PECA', -- PECA ou SERVICO
    criado_em       TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- MOVIMENTACOES DE ESTOQUE
CREATE TABLE movimentacoes_estoque (
    id              BIGSERIAL PRIMARY KEY,
    peca_id         BIGINT          NOT NULL REFERENCES pecas(id),
    ordem_id        BIGINT          REFERENCES ordens_servico(id), -- pode ser entrada manual
    tipo            VARCHAR(10)     NOT NULL, -- ENTRADA, SAIDA
    quantidade      INTEGER         NOT NULL,
    motivo          VARCHAR(100)    NOT NULL, -- USO_OS, COMPRA, AJUSTE, PERDA
    observacao      TEXT,
    usuario_id      BIGINT          REFERENCES usuarios(id),
    criado_em       TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- LANCAMENTOS FINANCEIROS
CREATE TABLE lancamentos (
    id              BIGSERIAL PRIMARY KEY,
    ordem_id        BIGINT          REFERENCES ordens_servico(id), -- pode ser despesa avulsa
    tipo            VARCHAR(10)     NOT NULL, -- RECEITA, DESPESA
    descricao       VARCHAR(200)    NOT NULL,
    valor           NUMERIC(10,2)   NOT NULL,
    forma_pagamento VARCHAR(30),    -- DINHEIRO, PIX, CARTAO_CREDITO, CARTAO_DEBITO, BOLETO
    categoria       VARCHAR(50),    -- OS, FORNECEDOR, ALUGUEL, FERRAMENTA, OUTROS
    data_lancamento DATE            NOT NULL DEFAULT CURRENT_DATE,
    observacao      TEXT,
    usuario_id      BIGINT          REFERENCES usuarios(id),
    criado_em       TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- =============================================
-- INDEXES para performance
-- =============================================
CREATE INDEX idx_clientes_nome ON clientes(nome);
CREATE INDEX idx_clientes_cpf ON clientes(cpf);
CREATE INDEX idx_clientes_telefone ON clientes(telefone);
CREATE INDEX idx_aparelhos_cliente ON aparelhos(cliente_id);
CREATE INDEX idx_os_status ON ordens_servico(status);
CREATE INDEX idx_os_cliente ON ordens_servico(cliente_id);
CREATE INDEX idx_os_numero ON ordens_servico(numero);
CREATE INDEX idx_os_criado_em ON ordens_servico(criado_em);
CREATE INDEX idx_historico_os_ordem ON historico_os(ordem_id);
CREATE INDEX idx_itens_os_ordem ON itens_os(ordem_id);
CREATE INDEX idx_movimentacoes_peca ON movimentacoes_estoque(peca_id);
CREATE INDEX idx_lancamentos_data ON lancamentos(data_lancamento);
CREATE INDEX idx_lancamentos_tipo ON lancamentos(tipo);

-- =============================================
-- DADOS INICIAIS
-- =============================================

-- Categorias de peças padrão
INSERT INTO categorias_peca (nome) VALUES
    ('Telas e Displays'),
    ('Baterias'),
    ('Conectores e Cabos'),
    ('Chips e Componentes'),
    ('Carcaças e Tampos'),
    ('Câmeras'),
    ('Botões e Flex'),
    ('Coolers e Pastas Térmicas'),
    ('Memória e Armazenamento'),
    ('Outros');

-- Usuário admin padrão (senha: admin123 - BCrypt)
INSERT INTO usuarios (nome, email, senha_hash, role) VALUES
    ('Administrador', 'admin@assistencia.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN');
