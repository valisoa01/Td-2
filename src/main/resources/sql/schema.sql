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


ALTER TABLE DishIngredient drop COLUMN id_dish;

create type  mouvement_type as enum('IN','OUT');
CREATE TABLE stock_movement (
                                id INT PRIMARY KEY, -- identifiant métier
                                ingredient_id INT NOT NULL REFERENCES ingredient(id),
                                stock_value_id INT NOT NULL REFERENCES stock_value(id),
                                type mouvement_type NOT NULL,
                                creation_datetime TIMESTAMP NOT NULL
);

 CREATE TABLE stock_value (
                             id SERIAL PRIMARY KEY,
                             quantity DOUBLE PRECISION NOT NULL,
                             unit unit_type NOT NULL
);


