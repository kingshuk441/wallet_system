package com.example.wallet_system.saga.steps;

import com.example.wallet_system.entities.Transaction;
import com.example.wallet_system.enums.SagaStepType;
import com.example.wallet_system.enums.TransactionStatus;
import com.example.wallet_system.repositories.TransactionRepository;
import com.example.wallet_system.saga.ISagaStep;
import com.example.wallet_system.saga.SagaContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UpdateTransactionStatus implements ISagaStep {


    private static final Logger log = LogManager.getLogger(UpdateTransactionStatus.class);

    private final TransactionRepository transactionRepository;


    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        Long transactionId = context.getLong("transactionId");
        log.info("Update transaction status: {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        context.put("originalTransactionStatus", transaction.getStatus());

        transaction.setStatus(TransactionStatus.SUCCESS);// can be fetched from context as well
        transactionRepository.save(transaction);

        context.put("transactionStatusAfterUpdate", transaction.getStatus());
        log.info("Update transaction status step executed successfully");
        return true;

    }

    @Override
    public boolean compensate(SagaContext context) {
        Long transactionId = context.getLong("transactionId");
        log.info("Compensate Update transaction status: {}", transactionId);


        TransactionStatus originateTransactionStatus = TransactionStatus.valueOf(context.getString("originalTransactionStatus"));

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setStatus(originateTransactionStatus);
        transactionRepository.save(transaction);

        log.info("Compensate Update transaction status step executed successfully");


        return true;
    }

    @Override
    public String getStepName() {
        return SagaStepType.UPDATE_TRANSACTION_STATUS_STEP.toString();
    }
}
