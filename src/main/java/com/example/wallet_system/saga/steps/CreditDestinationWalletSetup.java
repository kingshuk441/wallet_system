package com.example.wallet_system.saga.steps;

import com.example.wallet_system.entities.Wallet;
import com.example.wallet_system.enums.SagaStepType;
import com.example.wallet_system.repositories.WalletRepository;
import com.example.wallet_system.saga.ISagaStep;
import com.example.wallet_system.saga.SagaContext;
import groovy.util.logging.Slf4j;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;


@AllArgsConstructor
@Service
public class CreditDestinationWalletSetup implements ISagaStep {

    private static final Logger log = LoggerFactory.getLogger(CreditDestinationWalletSetup.class);
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        // Step 1 : get the destination wallet id from context
        Long toWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");
        log.info("Crediting destination wallet with id: {} with amount: {}", toWalletId, amount);

        // Step 2: fetch the destination wallet from the database with a lock
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Step 3: Credit the destination wallet
        // Step 4: Update the context with the changes
        log.info("Wallet fetched with balance: {}", wallet.getBalance());
        context.put("originalToWalletBalance", wallet.getBalance());
        try {
            wallet.creditBalance(amount);
            walletRepository.save(wallet);
            log.info("Wallet saved with balance: {}", wallet.getBalance());
            context.put("toWalletBalanceAfterCredit", wallet.getBalance());
            log.info("Credit destination Wallet Step executed Successfully");
            return true;
        } catch (Exception e) {
            log.info("Error while crediting destination wallet: {}", e.getMessage());
        }
        return false;
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        // Step 1 : get the destination wallet id from context
        Long toWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");
        log.info("Compensating credit of destination wallet with id: {} with amount: {}", toWalletId, amount);

        // Step 2: fetch the destination wallet from the database with a lock
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        // Step 3: Credit the destination wallet
        // Step 4: Update the context with the changes
        log.info("Wallet fetched with balance: {}", wallet.getBalance());
        context.put("toWalletBalanceBeforeCreditCompensation", wallet.getBalance());
        try {
            wallet.deductBalance(amount);
            walletRepository.save(wallet);
            log.info("Wallet saved with balance: {}", wallet.getBalance());
            context.put("toWalletBalanceAfterCreditCompensation", wallet.getBalance());
            log.info("Credit Compensation of destination Wallet Step executed Successfully");
            return true;
        } catch (Exception e) {
            log.info("Error while Compensating of crediting destination wallet: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public String getStepName() {
        return SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString();
    }
}
