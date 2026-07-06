package com.example.wallet_system.services;

import com.example.wallet_system.entities.Transaction;
import com.example.wallet_system.enums.TransactionStatus;
import com.example.wallet_system.enums.TransactionType;
import com.example.wallet_system.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;


    @Transactional
    public Transaction createTransaction(Long fromWalletId, Long toWalletId, BigDecimal amount, String desc) {
        log.info("Creating transaction from wallet {} to wallet {} with amount {}", fromWalletId, toWalletId, amount);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .description(desc)
                .fromWalletId(fromWalletId)
                .toWalletId(toWalletId)
                .status(TransactionStatus.PENDING)
                .type(TransactionType.TRANSFER)
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Transaction created: {}", transaction);
        return transaction;

    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public List<Transaction> getTransactionByWalletId(Long walletId) {
        return transactionRepository.findByWalletId(walletId);
    }

    public List<Transaction> getTransactionFromWalletId(Long walletId) {
        return transactionRepository.findByFromWalletId(walletId);
    }

    public List<Transaction> getTransactionToWalletId(Long walletId) {
        return transactionRepository.findByToWalletId(walletId);
    }

    public List<Transaction> getTransactionBySagaInstanceId(Long sagaInstanceId) {
        return transactionRepository.findBySagaInstanceId(sagaInstanceId);
    }

    public List<Transaction> getTransactionByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }

    public void updateTransactionWithSagaInstanceId(Long transactionId, Long sagaInstanceId) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.setSagaInstanceId(sagaInstanceId);
        transactionRepository.save(transaction);
        log.info("Transaction updated: {}", transaction);
    }

}
