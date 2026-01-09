-- Habilita a extensão para UUIDs se necessário
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Criação do Enum de Status do Pedido
CREATE TYPE order_status AS ENUM (
    'RECEIVED',
    'AWAITING_PAYMENT',
    'PAID',
    'IN_PREPARATION',
    'DONE',
    'DELIVERED'
);

-- 2. Tabela de Pedidos
CREATE TABLE orders (
    id              UUID PRIMARY KEY,
    customer_id     UUID           NOT NULL,
    status          order_status   NOT NULL,
    total_price     NUMERIC(18, 4) NOT NULL CHECK (total_price > 0),
    discount_amount NUMERIC(18, 4)          DEFAULT 0 CHECK (discount_amount >= 0),
    observation     VARCHAR(255),
    created_at      TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

-- 3. Tabela de Itens do Pedido
CREATE TABLE order_items (
    id               UUID PRIMARY KEY,
    order_id         UUID           NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id       UUID           NOT NULL,
    product_name     VARCHAR(255)   NOT NULL,
    product_quantity INTEGER        NOT NULL CHECK (product_quantity > 0),
    product_category VARCHAR(10)    NOT NULL,
    unit_price       NUMERIC(18, 4) NOT NULL CHECK (unit_price > 0),
    discount_amount  NUMERIC(18, 4)          DEFAULT 0 CHECK (discount_amount >= 0),
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ
);

-- 4. Índices de Performance
CREATE INDEX idx_orders_status ON orders (status);
CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);