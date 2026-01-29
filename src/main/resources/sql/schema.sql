CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TABLE Dish (
    id integer PRIMARY KEY,
    name varchar(50) NOT NULL ,
    dish_type dish_type_enum NOT NULL
);

CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TYPE unit_type AS ENUM ('KG','L');
ALTER TYPE unit_type ADD VALUE 'PCS';
CREATE TABLE DishIngredient(
                               id_dishIngredient SERIAL primary key,
                               id_dish int,
                               id_ingredient int,
                               quantity_required NUMERIC,
                               Unit unit_type
);

INSERT INTO DishIngredient VALUES (1, 1, 1, 0.20,'KG'),(2,1,2,0.15,'KG'),(3,2,3,1.00,'KG'),
                                  (4,4,4,0.30,'KG'),(5,4,5,0.20,'KG');

ALTER TABLE dish RENAME COLUMN price TO selling_price;


ALTER TABLE DishIngredient drop COLUMN id_dish;

create type  mouvement_type as enum('IN','OUT');
CREATE TABLE stockmovement (
                               id_stock INT PRIMARY KEY, -- identifiant métier
                               id_ingredient INT REFERENCES ingredient(id),
                               quantity DOUBLE PRECISION NOT NULL,
                               type mouvement_type NOT NULL,
                               unit unit_type NOT NULL,
                               creation_datetime DATE NOT NULL
);


 CREATE TABLE stock_value (
                             id SERIAL PRIMARY KEY,
                             quantity DOUBLE PRECISION NOT NULL,
                             unit unit_type NOT NULL
);
select * from dish;
CREATE TABLE Orders(
    id serial primary key ,
    reference varchar(50),
    creaction_datetime timestamp
);
create Table DishOrder(
    id serial primary key ,
    id_order int references Orders(id),
    id_dish int references dish(id),
    quantity int
);
select * from ingredient;
 select  * from  stockmovement;
select ingredient.name, s.unit
from ingredient
         inner join public.stockmovement s
                    on ingredient.id = s.id_ingredient;
create  table restaurant_table(
    id serial primary key ,
    number int not null
);
alter table Orders
add column table_id int references restaurant_table(id) not null ,
add column arrriva_datetime timestamp not null ;

alter table Orders
add column departure_datetime timestamp not null ;



SELECT 1
FROM Orders
WHERE table_id = ?
  AND arrriva_datetime < ?
  AND departure_datetime > ?
LIMIT 1;
select* from Orders;

select  * from DishOrder;
select * from Dish;
select * from restaurant_table;
ALTER TABLE dishorder DROP COLUMN id;   656
ALTER TABLE dishorder ADD COLUMN id SERIAL PRIMARY KEY;

INSERT INTO orders (id, reference, creaction_datetime, table_id, arrriva_datetime, departure_datetime)
VALUES
    (1, 'CMD-001', NOW(), 1, NOW() + INTERVAL '10 minutes', NOW() + INTERVAL '1 hour'),

    (2, 'CMD-002', NOW(), 2, NOW() + INTERVAL '15 minutes', NOW() + INTERVAL '1 hour 30 minutes'),

    (3, 'CMD-003', NOW(), 3, NOW() + INTERVAL '20 minutes', NOW() + INTERVAL '2 hours');


INSERT INTO dishorder (id, id_order, id_dish, quantity)
VALUES
-- Commande 1
(1, 1, 1, 1), -- Salade fraîche
(2, 1, 2, 1), -- Poulet grillé
(3, 1, 4, 1), -- Gâteau au chocolat

-- Commande 2
(4, 2, 1, 2), -- 2 Salades fraîches
(5, 2, 3, 1), -- Riz aux légumes
(6, 2, 5, 1), -- Salade de fruits

-- Commande 3
(7, 3, 2, 2), -- 2 Poulets grillés
(8, 3, 4, 2); -- 2 Gâteaux au chocolat
