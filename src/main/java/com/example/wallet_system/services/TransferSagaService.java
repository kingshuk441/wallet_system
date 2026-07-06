package com.example.wallet_system.services;

import com.example.wallet_system.entities.Transaction;
import com.example.wallet_system.enums.SagaStepType;
import com.example.wallet_system.saga.ISagaOrchestrator;
import com.example.wallet_system.saga.SagaContext;
import com.example.wallet_system.saga.SagaStepFactory;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TransferSagaService {
    private final TransactionService transactionService;
    private final ISagaOrchestrator sagaOrchestrator;


    @Transactional
    public Long initiateTransfer(Long fromWalletId, Long toWalletId, BigDecimal amount, String desc) {
        log.info("Initiating transfer from wallet : {} to wallet : {} and amount : {} and desc: {} ", fromWalletId, toWalletId, amount, desc);

        Transaction transaction = transactionService.createTransaction(fromWalletId, toWalletId, amount, desc);

        SagaContext sagaContext = SagaContext
                .builder()
                .data(Map.ofEntries(
                        Map.entry("transactionId", transaction.getId())
                        , Map.entry("amount", amount),
                        Map.entry("desc", desc)
                        , Map.entry("fromWalletId", fromWalletId)
                        , Map.entry("toWalletId", toWalletId)
                ))
                .build();

        Long sagaInstanceId = sagaOrchestrator.startSaga(sagaContext);
        log.info("Saga instance id created: {}", sagaInstanceId);

        transactionService.updateTransactionWithSagaInstanceId(transaction.getId(), sagaInstanceId);

        executeTransferSaga(sagaInstanceId);

        return sagaInstanceId;

    }


    public void executeTransferSaga(Long sagaInstanceId) {
        log.info("Execute transfer saga instance id : {}", sagaInstanceId);


        try {
            for (SagaStepType step : SagaStepFactory.transferMoneySagaSteps) {
                boolean success = sagaOrchestrator.executeStep(sagaInstanceId, step.toString());
                if (!success) {
                    log.error("Saga instance id {} execute failed", sagaInstanceId);
                    sagaOrchestrator.failSaga(sagaInstanceId);
                    return;
                }
            }
            sagaOrchestrator.completeSaga(sagaInstanceId);

        } catch (Exception e) {
            log.error("Saga instance id {} execute failed", sagaInstanceId, e);
            sagaOrchestrator.failSaga(sagaInstanceId);

        }
    }
}
