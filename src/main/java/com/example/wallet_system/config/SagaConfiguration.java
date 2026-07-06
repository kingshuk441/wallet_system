package com.example.wallet_system.config;

import com.example.wallet_system.entities.SagaStep;
import com.example.wallet_system.enums.SagaStepType;
import com.example.wallet_system.saga.ISagaStep;
import com.example.wallet_system.saga.steps.CreditDestinationWalletSetup;
import com.example.wallet_system.saga.steps.DebitSourceWalletStep;
import com.example.wallet_system.saga.steps.UpdateTransactionStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class SagaConfiguration {

    @Bean
    public Map<String, ISagaStep> sagaStepsMap(DebitSourceWalletStep debitSourceWalletStep, CreditDestinationWalletSetup creditDestinationWalletSetup, UpdateTransactionStatus updateTransactionStatus) {
        return Map.of(SagaStepType.DEBIT_SOURCE_WALLET_STEP.name(), debitSourceWalletStep,
                SagaStepType.CREDIT_DESTINATION_WALLET_STEP.name(), creditDestinationWalletSetup,
                SagaStepType.UPDATE_TRANSACTION_STATUS_STEP.name(), updateTransactionStatus);

    }
}
