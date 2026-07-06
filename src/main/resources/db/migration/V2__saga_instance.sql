
CREATE TABLE IF NOT EXISTS `saga_instance` (
                                               id BIGINT
                                               AUTO_INCREMENT PRIMARY KEY,
                                               context JSON,
                                               status VARCHAR(255) NOT NULL DEFAULT 'STARTED',
    current_step VARCHAR(255)
    );
