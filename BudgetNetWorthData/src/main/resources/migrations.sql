
--changeset jquass:create_accounts_table
CREATE TABLE accounts(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_name VARCHAR(255) NOT NULL,
    email VARCHAR(320) NOT NULL
);


