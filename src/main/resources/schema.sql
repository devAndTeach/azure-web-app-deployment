-- Schema definition for the widgets and gadgets

-- Drop the tables if they exist
drop table widgets if exists;
drop table gadgets if exists;

-- Widgets
create table widgets (id integer, description varchar(45), price numeric(6,2), gears integer, sprockets integer);

-- Gadgets
create table gadgets (id integer, description varchar(45), price numeric(6,2), cylinders integer);

