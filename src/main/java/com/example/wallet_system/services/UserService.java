package com.example.wallet_system.services;

import com.example.wallet_system.entities.User;
import com.example.wallet_system.repositories.UserRepository;
import groovy.util.logging.Slf4j;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;


    // create User
    public User createUser(User user) {
        log.info("Creating user: {}", user);
        User savedUser = userRepository.save(user);
        log.info("User created with id: {} in shardwallet{}", savedUser.getId(), savedUser.getId() % 2 + 1);
        return savedUser;
    }


}
