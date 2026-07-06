package com.example.wallet_system.saga;

import com.example.wallet_system.entities.SagaInstance;

public interface ISagaOrchestrator {

    Long startSaga(SagaContext sagaContext); // new saga obj created and saved in db

    boolean executeStep(Long sagaId, String StepName);

    boolean compensateStep(Long sagaId, String stepName);

    SagaInstance getSagaInstance(Long sagaId);

    void compensateSaga(Long sagaId); // revert all completed saga steps

    void failSaga(Long sagaId); // mark saga as failed

    void completeSaga(Long sagaId);


}
