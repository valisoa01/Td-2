INSERT INTO Dish (id, name, dish_type) VALUES
(1, 'Salade fraîche', 'START'),
(2, 'Poulet grillé', 'MAIN'),
(3, 'Riz aux légumes', 'MAIN'),
(4, 'Gâteau au chocolat', 'DESSERT'),
(5, 'Salade de fruits', 'DESSERT');
INSERT INTO Ingredient (id, name, price, category) VALUES
(1, 'Laitue', 800.00, 'VEGETABLE'),
(2, 'Tomate', 600.00, 'VEGETABLE'),
(3, 'Poulet', 4500.00, 'ANIMAL'),
(4, 'Chocolat', 3000.00, 'OTHER'),
(5, 'Beurre', 2500.00, 'DAIRY');


INSERT INTO stockmovement VALUES
                              (1, 1, 10, 'IN',  'KG', '2024-01-01'),
                              (2, 1,  2, 'OUT', 'KG', '2024-01-03'),
                              (3, 1,  5, 'IN',  'KG', '2024-01-05'),

                              (4, 2, 20, 'IN',  'KG', '2024-01-01'),
                              (5, 2,  3, 'OUT', 'KG', '2024-01-04'),

                              (6, 3, 15, 'IN',  'KG', '2024-01-02');
