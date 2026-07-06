CREATE TABLE IF NOT EXISTS `transaction`
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    from_wallet_id BIGINT NOT NULL,
    to_wallet_id BIGINT NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    type VARCHAR(255) NOT NULL DEFAULT 'TRANSFER',
    description VARCHAR(255),
    saga_instance_id BIGINT,

    PRIMARY KEY (id)
    );



CREATE TABLE IF NOT EXISTS wallet
(
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    balance DECIMAL(19,4) NOT NULL DEFAULT 0.0000,

    PRIMARY KEY (id)
    );
