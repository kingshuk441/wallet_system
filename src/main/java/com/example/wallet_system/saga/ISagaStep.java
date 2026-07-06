package com.example.wallet_system.saga;

public interface ISagaStep {
    boolean execute(SagaContext context);

    boolean compensate(SagaContext context);

    String getStepName();
}
