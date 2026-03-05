INSERT INTO categorias (nome, descricao) VALUES
('Eletrônicos', 'Produtos eletrônicos em geral'),
('Informática', 'Produtos de informática e computadores'),
('Periféricos', 'Periféricos de computador'),
('Gamer', 'Produtos para gamers'),
('Acessórios', 'Acessórios diversos');

INSERT INTO produtos (nome, descricao, preco, quantidade_estoque, categoria_id, status) VALUES
('Mouse Gamer RGB', 'Mouse gamer com iluminação RGB', 149.90, 25, 4, 'ATIVO'),
('Teclado Mecânico', 'Teclado mecânico com switches blue', 299.90, 15, 4, 'ATIVO'),
('Monitor 27"', 'Monitor LED 27 polegadas Full HD', 899.90, 10, 1, 'ATIVO'),
('Notebook Dell', 'Notebook Dell Intel Core i5', 3499.90, 8, 2, 'ATIVO'),
('Webcam HD', 'Webcam Full HD 1080p', 199.90, 30, 3, 'ATIVO'),
('Headset Gamer', 'Headset gamer com microfone', 249.90, 20, 4, 'ATIVO'),
('Mouse Pad XL', 'Mouse pad extra grande', 79.90, 50, 5, 'ATIVO'),
('Suporte para Notebook', 'Suporte ajustável para notebook', 129.90, 35, 5, 'ATIVO');
