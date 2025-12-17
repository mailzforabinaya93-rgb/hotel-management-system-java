CREATE DATABASE IF NOT EXISTS hotel_db;
USE hotel_db;

-- USERS TABLE
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

-- MENU TABLE
CREATE TABLE menu (
    itemId INT AUTO_INCREMENT PRIMARY KEY,
    itemName VARCHAR(100) NOT NULL,
    price DOUBLE NOT NULL
);

-- ORDERS TABLE
CREATE TABLE orders (
    orderId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    itemName VARCHAR(100),
    quantity INT,
    total DOUBLE,
    orderTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
