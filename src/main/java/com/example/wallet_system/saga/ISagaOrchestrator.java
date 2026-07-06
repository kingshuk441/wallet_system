package com.example.wallet_system.saga;

import com.example.wallet_system.entities.SagaInstance;

public interface ISagaOrchestrator {

    Long startSaga(SagaContext sagaContext); // new saga obj created and saved in db

    boolean executeStep(Long sagaId, String StepName);

    boolean compensateStep(Long sagaId, String stepName);

    SagaInstance getSagaInstance(Long sagaId);

    void compensateSaga(SagaInstance sagaInstance); // revert all completed saga steps

    void failSaga(SagaInstance sagaInstance); // mark saga as failed

    void completeSaga(SagaInstance sagaInstance);


}
