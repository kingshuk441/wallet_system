package com.example.wallet_system.services;

import com.example.wallet_system.entities.Wallet;
import com.example.wallet_system.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;


    public Wallet createWallet(Long id) {

        log.info("Wallet creating with id: {} in shardwallet{}", id, id % 2 + 1);

        Wallet wallet = Wallet.builder()
                .balance(BigDecimal.ZERO)
                .userId(id)
                .isActive(true)
                .build();

        wallet = walletRepository.save(wallet);
        log.info("Wallet created: {}", wallet);
        return wallet;
    }


    public Wallet getWallet(Long userId) {
        log.info("Getting wallet for user : {}", userId);
        return walletRepository.findByUserId(userId).getFirst();
    }


    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId).getFirst();
    }

    @Transactional
    public void debit(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        log.info("Debitting wallet for user : {}", wallet);
        walletRepository.updateBalanceByUserId(userId, wallet.getBalance().subtract(amount));
        log.info("Wallet debited: {}", wallet);
    }

    @Transactional
    public void credit(Long userId, BigDecimal amount) {
        Wallet wallet = getWalletByUserId(userId);
        log.info("Credit wallet for user : {}", wallet);
        walletRepository.updateBalanceByUserId(userId, wallet.getBalance().add(amount));
        log.info("Wallet credited: {}", wallet);
    }

    public BigDecimal getBalance(Long userId) {
        Wallet wallet = getWalletByUserId(userId);
        log.info("Getting balance for wallet : {}", wallet);
        return wallet.getBalance();
    }
}

