create database weather_database;
use weather_database;
create table weather_history(
	weather_date date primary key not null,
    temperature varchar(4) not null
)