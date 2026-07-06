package com.example.wallet_system.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    ApplicationRunner migrate() {
        return args -> {

            Flyway.configure()
                    .dataSource(
                            "jdbc:mysql://localhost:3306/shardwallet1",
                            "root",
                            "")
                    .load()
                    .migrate();

            Flyway.configure()
                    .dataSource(
                            "jdbc:mysql://localhost:3306/shardwallet2",
                            "root",
                            "")
                    .load()
                    .migrate();
        };
    }
}