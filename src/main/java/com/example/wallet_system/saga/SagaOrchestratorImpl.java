package com.example.wallet_system.saga;

import com.example.wallet_system.entities.SagaInstance;
import org.springframework.stereotype.Service;

@Service
public class SagaOrchestratorImpl implements ISagaOrchestrator{
    @Override
    public Long startSaga(SagaContext sagaContext) {
        return 0L;
    }

    @Override
    public boolean executeStep(Long sagaId, String StepName) {
        return false;
    }

    @Override
    public boolean compensateStep(Long sagaId, String stepName) {
        return false;
    }

    @Override
    public SagaInstance getSagaInstance(Long sagaId) {
        return null;
    }

    @Override
    public void compensateSaga(SagaInstance sagaInstance) {

    }

    @Override
    public void failSaga(SagaInstance sagaInstance) {

    }

    @Override
    public void completeSaga(SagaInstance sagaInstance) {

    }
}
