package com.example.wallet_system.saga;


import com.example.wallet_system.entities.SagaStep;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class SagaStepFactory {

    private final Map<String, ISagaStep> sagaStepsMap;

    public ISagaStep getSagaStep(String stepName) {
        return sagaStepsMap.get(stepName);
    }
}
