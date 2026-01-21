CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TABLE Dish (
    id integer PRIMARY KEY,
    name varchar(50) NOT NULL ,
    dish_type dish_type_enum NOT NULL
);

CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
-- CREATE TABLE Dish_Ingredient (
--     id_dish INTEGER REFERENCES Dish(id),
--     id_ingredient INTEGER REFERENCES Ingredient(id),
--     required_quantity NUMERIC,
--
--     PRIMARY KEY (id_dish, id_ingredient)
-- );
CREATE TYPE unit_type AS ENUM ('KG','L');
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


UPDATE Dish SET selling_price = 3500.00 WHERE id_dish = 1;
UPDATE Dish SET selling_price = 12000.00 WHERE id_dish = 2;
UPDATE Dish SET selling_price = NULL WHERE id_dish = 3;
UPDATE Dish SET selling_price = 8000.00 WHERE id_dish = 4;
UPDATE Dish SET selling_price = NULL WHERE id_dish = 5;

ALTER TABLE DishIngredient drop COLUMN id_dish;