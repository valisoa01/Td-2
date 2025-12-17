create database mini_dish_db;
create user mini_dish_db_manager with password '123456';
grant  connect on database mini_dish_db to mini_dish_db_manager;

\c mini_dish_db

GRANT usage on SCHEMA PUBLIC to mini_dish_db_manager;
GRANT CREATE ON SCHEMA PUBLIC TO mini_dish_db_manager;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO mini_dish_db_manager;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO mini_dish_db_manager;

Alter DEFAULT privileges IN SCHEMA PUBLIC
GRANT ALL ON TABLES TO mini_dish_db_manager;

ALTER DEFAULT privileges IN SCHEMA PUBLIC
GRANT ALL ON SEQUENCES TO mini_dish_db_manager;