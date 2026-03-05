INSERT INTO categorias (nome, descricao) VALUES 
('Eletrônicos', 'Produtos eletrônicos em geral'), 
('Informática', 'Produtos de informática e computadores'), 
('Periféricos', 'Periféricos de computador'), 
('Gamer', 'Produtos para gamers'), 
('Acessórios', 'Acessórios diversos');

INSERT INTO produtos (nome, descricao, preco, quantidade_estoque, categoria_id, status) VALUES 
('Notebook Dell Inspiron', 'Notebook Dell com 16GB RAM e 512GB SSD', 4500.00, 10, 1, 'ATIVO'),
('Mouse Logitech', 'Mouse sem fio Logitech', 150.00, 50, 3, 'ATIVO'),
('Teclado Mecânico', 'Teclado mecânico RGB', 300.00, 25, 3, 'ATIVO'),
('Monitor Gamer 27"', 'Monitor LED 27" 144Hz', 1200.00, 15, 4, 'ATIVO'),
('Webcam HD', 'Webcam 1080p com microfone', 200.00, 30, 5, 'EM_FALTA');
