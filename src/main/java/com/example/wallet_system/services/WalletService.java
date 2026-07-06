package com.example.wallet_system.services;

import com.example.wallet_system.entities.Wallet;
import com.example.wallet_system.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;


    public Wallet createWallet(Long id) {
        log.info("Creating wallet for user : {}", id);

        Wallet wallet = Wallet.builder().id(id)
                .balance(BigDecimal.ZERO)
                .isActive(true)
                .build();

        wallet = walletRepository.save(wallet);
        log.info("Wallet created: {}", wallet);
        return wallet;
    }


    public Wallet getWallet(Long id) {
        log.info("Getting wallet for user : {}", id);
        return walletRepository.findById(id).orElseThrow(() -> new RuntimeException("Wallet not found for user: " + id));
    }


    public List<Wallet> getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId);
    }

    @Transactional
    public void debit(Long walletId, BigDecimal amount) {
        Wallet wallet = getWallet(walletId);
        log.info("Debitting wallet for user : {}", wallet);
        try {
            wallet.deductBalance(amount);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        walletRepository.save(wallet);
        log.info("Wallet debited: {}", wallet);
    }

    @Transactional
    public void credit(Long walletId, BigDecimal amount) {
        Wallet wallet = getWallet(walletId);
        log.info("Credit wallet for user : {}", wallet);
        try {
            wallet.creditBalance(amount);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        walletRepository.save(wallet);
        log.info("Wallet credited: {}", wallet);
    }

    public BigDecimal getBalance(Long walletId) {
        Wallet wallet = getWallet(walletId);
        log.info("Getting balance for wallet : {}", wallet);
        return wallet.getBalance();
    }
}

