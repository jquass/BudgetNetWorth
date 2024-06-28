
CREATE TABLE `accounts` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `account_name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `uploads` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `filename` VARCHAR(255) DEFAULT NULL,
    `status` VARCHAR(255) DEFAULT NULL,
    `account_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `upload_headers` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `upload_id` BIGINT NOT NULL,
    `value` VARCHAR(255) NOT NULL,
    `type` VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `upload_rows` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `upload_id` BIGINT NOT NULL,
    `full_row` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `date` VARCHAR(255) NOT NULL,
    `memo` VARCHAR(255) NOT NULL,
    `amount` FLOAT NOT NULL,
    `account_id` BIGINT NOT NULL,
    `upload_id` BIGINT DEFAULT NULL,
    `upload_row_id` BIGINT DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_date_memo_amount` (`date`,`memo`,`amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
