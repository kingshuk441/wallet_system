package com.example.wallet_system.saga.steps;

import com.example.wallet_system.entities.Wallet;
import com.example.wallet_system.repositories.WalletRepository;
import com.example.wallet_system.saga.ISagaStep;
import com.example.wallet_system.saga.SagaContext;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class DebitSourceWalletStep implements ISagaStep {

    private static final Logger log = LoggerFactory.getLogger(DebitSourceWalletStep.class);
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        // Step 1 : get the source wallet id from context
        Long toWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");
        log.info("Debiting source wallet with id: {} with amount: {}", toWalletId, amount);

        // Step 2: fetch the source wallet from the database with a lock
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Step 3: Debit the source wallet
        // Step 4: Update the context with the changes
        log.info("Wallet fetched with balance: {}", wallet.getBalance());
        context.put("originalSourceWalletBalance", wallet.getBalance());
        try {
            wallet.deductBalance(amount);
            walletRepository.save(wallet);
            log.info("Wallet saved with balance: {}", wallet.getBalance());
            context.put("sourceWalletBalanceAfterDebit", wallet.getBalance());
            log.info("Debit source Wallet Step executed Successfully");
            return true;
        } catch (Exception e) {
            log.info("Error while debiting source wallet: {}", e.getMessage());
        }
        return false;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        // Step 1 : get the source wallet id from context
        Long toWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");
        log.info("Compensating source wallet with id: {} with amount: {}", toWalletId, amount);

        // Step 2: fetch the destination wallet from the database with a lock
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Step 3: Debit the destination wallet
        // Step 4: Update the context with the changes
        context.put("sourceWalletBalanceBeforeDebitCompensation", wallet.getBalance());
        log.info("Wallet fetched with balance: {}", wallet.getBalance());
        try {
            wallet.creditBalance(amount);
            walletRepository.save(wallet);
            log.info("Wallet saved with balance: {}", wallet.getBalance());
            context.put("sourceWalletBalanceAfterDebitCompensation", wallet.getBalance());
            log.info("Debit source Wallet Compensation Step executed Successfully");
            return true;
        } catch (Exception e) {
            log.info("Error while debiting Compensation source wallet: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public String getStepName() {
        return "DebitSourceWalletStep";
    }
}
