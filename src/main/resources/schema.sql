DROP TABLE IF EXISTS vouchers;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    role VARCHAR(50) NOT NULL
);

CREATE TABLE vouchers (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    tour_type VARCHAR(100),
    transfer_type VARCHAR(100),
    hotel_type VARCHAR(100),
    status VARCHAR(50),
    arrival_date DATE,
    eviction_date DATE,
    user_id BIGINT,
    is_hot BOOLEAN,
    CONSTRAINT fk_user FOREIGN KEY (user_id)
        REFERENCES users(id)
);