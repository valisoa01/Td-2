CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TABLE Dish (
    id integer PRIMARY KEY,
    name varchar(50) NOT NULL ,
    dish_type dish_type_enum NOT NULL
);

CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TABLE Dish_Ingredient (
    id_dish INTEGER REFERENCES Dish(id),
    id_ingredient INTEGER REFERENCES Ingredient(id),
    required_quantity NUMERIC,

    PRIMARY KEY (id_dish, id_ingredient)
);
