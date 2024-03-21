# DELETE
# FROM friendships;
# DELETE
# FROM transactions;
# DELETE
# FROM bank_accounts;
# DELETE
# FROM users;

DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS bank_accounts;
DROP TABLE IF EXISTS users;


CREATE TABLE users
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(250) NOT NULL,
    last_name  VARCHAR(250) NOT NULL,
    email      VARCHAR(250) NOT NULL,
    password   VARCHAR(250) NOT NULL
);


INSERT INTO users (first_name, last_name, email, password)
VALUES ('John', 'Dupont', '1@gmail.com', '$2a$10$rgVyJt18/S/QFuDoL1oqoOOYPjLvyN2eLJDurWgOzeD/khrqLvjAK'),
       ('Henry', 'Charles', '2@gmail.com', '$2a$10$rgVyJt18/S/QFuDoL1oqoOOYPjLvyN2eLJDurWgOzeD/khrqLvjAK'),
       ('Amelie', 'Boudea', '3@gmail.com', '$2a$10$rgVyJt18/S/QFuDoL1oqoOOYPjLvyN2eLJDurWgOzeD/khrqLvjAK'),
       ('Loïc', 'Dubrulle', 'loutix@hotmail.com', '$2a$10$rgVyJt18/S/QFuDoL1oqoOOYPjLvyN2eLJDurWgOzeD/khrqLvjAK');

CREATE TABLE bank_accounts
(
    id      INT AUTO_INCREMENT PRIMARY KEY,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    user_id INT,
    iban    VARCHAR(50),
    CONSTRAINT fk_user_bank_account FOREIGN KEY (user_id) REFERENCES users (id)
);


INSERT INTO bank_accounts (balance, user_id, iban)
VALUES (100, 1, "FR12345678901234567890123456"),
       (200, 2, "FRABCDEF1234567890123456789"),
       (300, 3, "FRABCDEF1234567890123456789"),
       (400, 4, "FR12345678901234567890123456");


CREATE TABLE friendships
(
    id        INT AUTO_INCREMENT PRIMARY KEY,
    user_id   INT,
    friend_id INT,
    CONSTRAINT fk_user_friendship FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_friend_friendship FOREIGN KEY (friend_id) REFERENCES users (id)
);


INSERT INTO friendships (user_id, friend_id)
VALUES (1, 2),
       (4, 1),
       (4, 3);


CREATE TABLE transactions
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    amount           DECIMAL(10, 2) DEFAULT 0.00,
    description      VARCHAR(50),
    transaction_type VARCHAR(25),
    date             TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    bank_account_id  INT,
    friend_bank_id   INT,
    CONSTRAINT fk_bank_account_transaction FOREIGN KEY (bank_account_id) REFERENCES bank_accounts (id)
);


INSERT INTO transactions (amount, description, transaction_type, bank_account_id, friend_bank_id, date)
VALUES (100, "cadeau de noël", "DEBIT", 1, 4, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 124 MINUTE)),
       (100, "cadeau de noël", "CREDIT", 4, 1, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 124 MINUTE)),
       (200, "repas", "DEBIT", 4, 2, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3625 MINUTE)),
       (200, "repas", "CREDIT", 2, 4, DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 3625 MINUTE));