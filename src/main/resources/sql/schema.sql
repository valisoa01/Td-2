CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TABLE Dish (
    id integer PRIMARY KEY,
    name varchar(50),
    dish_type dish_type_enum
);

CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
create table Ingredient(
    id integer primary key ,
    name varchar(50),
    price  numeric,
    category category_enum,
    id_dish integer references Dish(id)
);