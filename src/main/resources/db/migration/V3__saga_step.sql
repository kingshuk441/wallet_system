CREATE TABLE IF NOT EXISTS saga_step (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         saga_instance_id BIGINT,
                                         step_name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    error_message VARCHAR(255),
    step_data JSON
    );

