-- Banco de dados do sistema Mais Doces
-- Projeto Integrador - Daniel Teixeira Silva
-- Pode ser executado no MySQL Workbench (Ctrl+Shift+Enter).
-- O proprio sistema tambem executa este script se o banco ainda nao existir.

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS mais_doces;
CREATE DATABASE mais_doces DEFAULT CHARACTER SET utf8mb4;
USE mais_doces;

CREATE TABLE funcionario (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  cargo VARCHAR(20) NOT NULL,
  login VARCHAR(30) NOT NULL UNIQUE,
  senha VARCHAR(30) NOT NULL,
  ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cliente (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  telefone VARCHAR(20),
  email VARCHAR(100),
  endereco VARCHAR(150),
  observacao VARCHAR(200)
);

CREATE TABLE fornecedor (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  telefone VARCHAR(20),
  cidade VARCHAR(60),
  categoria VARCHAR(50)
);

CREATE TABLE produto (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  categoria VARCHAR(50),
  preco DECIMAL(10,2) NOT NULL,
  quantidade INT NOT NULL DEFAULT 0,
  validade DATE,
  fornecedor_id INT,
  FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id)
);

CREATE TABLE venda (
  id INT AUTO_INCREMENT PRIMARY KEY,
  cliente_id INT,
  funcionario_id INT NOT NULL,
  data DATE NOT NULL,
  data_entrega DATE,
  total DECIMAL(10,2) NOT NULL,
  forma_pagamento VARCHAR(30),
  status VARCHAR(20) NOT NULL,
  ocasiao VARCHAR(30),
  FOREIGN KEY (cliente_id) REFERENCES cliente(id),
  FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
);

CREATE TABLE item_venda (
  id INT AUTO_INCREMENT PRIMARY KEY,
  venda_id INT NOT NULL,
  produto_id INT NOT NULL,
  quantidade INT NOT NULL,
  preco DECIMAL(10,2) NOT NULL,
  subtotal DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (venda_id) REFERENCES venda(id),
  FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE encomenda (
  id INT AUTO_INCREMENT PRIMARY KEY,
  item_venda_id INT NOT NULL,
  data_entrega DATE NOT NULL,
  custo DECIMAL(10,2) NOT NULL DEFAULT 0,
  status VARCHAR(20) NOT NULL DEFAULT 'Em andamento',
  FOREIGN KEY (item_venda_id) REFERENCES item_venda(id)
);

CREATE TABLE caixa (
  id INT AUTO_INCREMENT PRIMARY KEY,
  tipo VARCHAR(10) NOT NULL,
  valor DECIMAL(10,2) NOT NULL,
  data DATE NOT NULL,
  descricao VARCHAR(150),
  forma_pagamento VARCHAR(30),
  venda_id INT,
  FOREIGN KEY (venda_id) REFERENCES venda(id)
);

-- ===================== DADOS PARA TESTE =====================
-- Usuarios do sistema (todos com senha 123)

INSERT INTO funcionario (nome, cargo, login, senha) VALUES
('Daniel Teixeira Silva', 'Gerente', 'admin', '123'),
('Bruna Costa', 'Atendente', 'atendente', '123'),
('Lucia Ramos', 'Confeiteiro', 'confeiteiro', '123'),
('Rafael Nunes', 'Financeiro', 'financeiro', '123');

INSERT INTO fornecedor (nome, telefone, cidade, categoria) VALUES
('Cacau Nobre Distribuidora', '(19) 3333-4444', 'Campinas', 'Chocolates'),
('Embala Fácil', '(19) 3444-5555', 'Limeira', 'Embalagens');

INSERT INTO produto (nome, categoria, preco, quantidade, validade, fornecedor_id) VALUES
('Brigadeiro gourmet', 'Docinhos', 3.50, 80, CURDATE() + INTERVAL 5 DAY, 1),
('Beijinho gourmet', 'Docinhos', 3.50, 64, CURDATE() + INTERVAL 5 DAY, NULL),
('Brownie de chocolate', 'Brownies', 12.00, 13, CURDATE() + INTERVAL 3 DAY, 1),
('Bolo red velvet 1 kg', 'Bolos', 95.00, 2, CURDATE() + INTERVAL 3 DAY, NULL),
('Bolo de pote ninho', 'Bolos', 16.00, 18, CURDATE() + INTERVAL 4 DAY, NULL),
('Torta de limão (fatia)', 'Tortas', 14.00, 6, CURDATE() - INTERVAL 1 DAY, NULL),
('Cookie recheado', 'Cookies', 9.00, 22, CURDATE() + INTERVAL 12 DAY, NULL),
('Pão de mel', 'Docinhos', 7.50, 30, CURDATE() + INTERVAL 20 DAY, NULL);

INSERT INTO cliente (nome, telefone, email, endereco, observacao) VALUES
('Mariana Oliveira', '(19) 98888-7777', 'mariana@email.com', 'Rua das Flores, 120', 'Prefere WhatsApp'),
('Carla Mendes', '(19) 97777-1111', 'carla@email.com', 'Av. Brasil, 55', NULL),
('Escritório Alfa', '(19) 3555-2020', 'contato@alfa.com', 'Rua XV, 300', 'Pedidos para eventos');

-- vendas 1 e 2 sao de balcao, vendas 3, 4 e 5 sao encomendas
INSERT INTO venda (cliente_id, funcionario_id, data, data_entrega, total, forma_pagamento, status, ocasiao) VALUES
(NULL, 2, CURDATE(), NULL, 59.00, 'PIX', 'Pago', 'Nenhuma'),
(NULL, 2, CURDATE(), NULL, 105.00, NULL, 'Pendente', 'Nenhuma'),
(2, 2, CURDATE(), CURDATE() + INTERVAL 1 DAY, 95.00, 'Cartão de crédito', 'Pago', 'Aniversário'),
(3, 2, CURDATE(), CURDATE() + INTERVAL 3 DAY, 420.00, NULL, 'Pendente', 'Nenhuma'),
(1, 2, CURDATE(), CURDATE() + INTERVAL 10 DAY, 985.00, 'PIX', 'Pago', 'Casamento');

INSERT INTO item_venda (venda_id, produto_id, quantidade, preco, subtotal) VALUES
(1, 1, 10, 3.50, 35.00),
(1, 3, 2, 12.00, 24.00),
(2, 1, 30, 3.50, 105.00),
(3, 4, 1, 95.00, 95.00),
(4, 1, 120, 3.50, 420.00),
(5, 4, 3, 95.00, 285.00),
(5, 1, 200, 3.50, 700.00);

INSERT INTO encomenda (item_venda_id, data_entrega, custo, status) VALUES
(4, CURDATE() + INTERVAL 1 DAY, 85.00, 'Finalizado'),
(5, CURDATE() + INTERVAL 3 DAY, 0, 'Em andamento'),
(6, CURDATE() + INTERVAL 10 DAY, 0, 'Em andamento'),
(7, CURDATE() + INTERVAL 10 DAY, 0, 'Em andamento');

INSERT INTO caixa (tipo, valor, data, descricao, forma_pagamento, venda_id) VALUES
('Entrada', 59.00, CURDATE(), 'Venda 1 - Balcão', 'PIX', 1),
('Entrada', 95.00, CURDATE(), 'Venda 3 - Carla Mendes', 'Cartão de crédito', 3),
('Entrada', 985.00, CURDATE(), 'Venda 5 - Mariana Oliveira', 'PIX', 5),
('Saída', 85.00, CURDATE(), 'Compra de embalagens', 'Dinheiro', NULL);
