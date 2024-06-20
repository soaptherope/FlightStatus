--liquibase formatted sql

--changeset alishersharipov:create "roles" table

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    code VARCHAR(256) UNIQUE
);

--changeset alishersharipov:create "users" table

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(256) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL,
    role_id INT,
    CONSTRAINT fk_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
);

--changeset alishersharipov:create "flights" table

CREATE TABLE flights (
    id SERIAL PRIMARY KEY,
    origin VARCHAR(256) NOT NULL,
    destination VARCHAR(256) NOT NULL,
    departure TIMESTAMP WITH TIME ZONE NOT NULL,
    arrival TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT status_check CHECK (status IN ('InTime', 'Delayed', 'Cancelled'))
);