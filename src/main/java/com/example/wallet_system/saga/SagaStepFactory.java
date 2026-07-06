package com.example.wallet_system.saga;


import com.example.wallet_system.enums.SagaStepType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class SagaStepFactory {

    private final Map<String, ISagaStep> sagaStepsMap;

    public static final List<SagaStepType> transferMoneySagaSteps = List.of(SagaStepType.DEBIT_SOURCE_WALLET_STEP, SagaStepType.CREDIT_DESTINATION_WALLET_STEP, SagaStepType.UPDATE_TRANSACTION_STATUS_STEP);


    public ISagaStep getSagaStep(String stepName) {
        return sagaStepsMap.get(stepName);
    }
}
