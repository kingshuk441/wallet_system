package com.example.wallet_system.saga;

import com.example.wallet_system.entities.SagaInstance;
import com.example.wallet_system.entities.SagaStep;
import com.example.wallet_system.enums.SagaStatus;
import com.example.wallet_system.enums.StepStatus;
import com.example.wallet_system.repositories.SagaInstanceRepository;
import com.example.wallet_system.repositories.SagaStepRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SagaOrchestratorImpl implements ISagaOrchestrator {
    private static final Logger log = LogManager.getLogger(SagaOrchestratorImpl.class);
    private final ObjectMapper objectMapper;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepRepository sagaStepRepository;
    private final SagaStepFactory sagaStepFactory;

    @Override
    @Transactional
    public Long startSaga(SagaContext sagaContext) {
        try {
            String contextJson = objectMapper.writeValueAsString(sagaContext);
            SagaInstance sagaInstance = SagaInstance.builder().context(contextJson).status(SagaStatus.STARTED).build();

            sagaInstance = sagaInstanceRepository.save(sagaInstance);
            log.info("Saga instance has been started with id {}", sagaInstance.getId());
            return sagaInstance.getId();
        } catch (Exception e) {
            log.error("Error starting saga: {}", e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional
    public boolean executeStep(Long sagaId, String stepName) {

        // fetch saga instance
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaId).orElseThrow(() -> new RuntimeException("Saga instance with id " + sagaId + " not found"));

        // get saga step from stepName
        ISagaStep executionStep = sagaStepFactory.getSagaStep(stepName);
        if (executionStep == null) throw new RuntimeException("executionStep is null");

        // fetch saga step from db if exists else create new saga step with status PENDING
        SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaId, stepName, StepStatus.PENDING).orElse(SagaStep.builder().sagaInstanceId(sagaId).stepName(stepName).stepStatus(StepStatus.PENDING).build());

        if (sagaStepDB.getId() == null) {
            sagaStepRepository.save(sagaStepDB);
        }

        try {
            SagaContext sagaContext = objectMapper.readValue(sagaInstance.getContext(), SagaContext.class);
            sagaStepDB.setStepStatus(StepStatus.RUNNING);
            sagaStepRepository.save(sagaStepDB);
            boolean success = executionStep.execute(sagaContext);


            if (success) {
                log.info("Step Executed Successfully: {}", stepName);
                sagaStepDB.setStepStatus(StepStatus.COMPLETED);
                sagaStepRepository.save(sagaStepDB);

                sagaInstance.setCurrentStep(stepName);
                sagaInstance.setStatus(SagaStatus.RUNNING);
                sagaInstanceRepository.save(sagaInstance);

                return true;
            } else {
                sagaStepDB.setStepStatus(StepStatus.FAILED);
                sagaStepRepository.save(sagaStepDB);
                log.info("Error while Step Execution: {}", stepName);
                return false;
            }

        } catch (Exception e) {
            sagaStepDB.setStepStatus(StepStatus.FAILED);
            sagaStepRepository.save(sagaStepDB);
            log.error("Failed Step execution: {}", sagaInstance.getCurrentStep());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean compensateStep(Long sagaId, String stepName) {
        // fetch saga instance
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaId).orElseThrow(() -> new RuntimeException("Saga instance with id " + sagaId + " not found"));

        // get saga step from stepName
        ISagaStep executionStep = sagaStepFactory.getSagaStep(stepName);
        if (executionStep == null) throw new RuntimeException("executionStep is null");

        // fetch saga step from db if exists else create new saga step with status PENDING
        SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaId, stepName, StepStatus.COMPLETED).orElse(null);

        if (sagaStepDB == null) {
            log.info("No completed step found for compensation: {} for saga: {}", stepName, sagaId);
            return true;
        }

            try {
                SagaContext sagaContext = objectMapper.readValue(sagaInstance.getContext(), SagaContext.class);
                sagaStepDB.setStepStatus(StepStatus.COMPENSATING);
                sagaStepRepository.save(sagaStepDB);
                boolean success = executionStep.compensate(sagaContext);


                if (success) {
                    log.info("Step Compensated Successfully: {}", stepName);
                    sagaStepDB.setStepStatus(StepStatus.COMPENSATED);
                    sagaStepRepository.save(sagaStepDB);

                    return true;
                } else {
                    sagaStepDB.setStepStatus(StepStatus.FAILED);
                    sagaStepRepository.save(sagaStepDB);
                    log.info("Error while Step Execution: {}", stepName);
                    return false;
                }

            } catch (Exception e) {
                sagaStepDB.setStepStatus(StepStatus.FAILED);
                sagaStepRepository.save(sagaStepDB);
                log.error("Failed Step execution: {}", sagaInstance.getCurrentStep());
                return false;
            }
    }

    @Override
    public SagaInstance getSagaInstance(Long sagaId) {
        return sagaInstanceRepository.findById(sagaId).orElseThrow(() -> new RuntimeException("Saga instance with id " + sagaId + " not found"));
    }

    @Override
    @Transactional
    public void compensateSaga(Long sagaId) {


        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaId).orElseThrow(() -> new RuntimeException("Saga instance with id " + sagaId + " not found"));

        sagaInstance.setStatus(SagaStatus.COMPENSATING);
        sagaInstanceRepository.save(sagaInstance);

        List<SagaStep> completedStepsBySagaInstanceId = sagaStepRepository.findCompletedStepsBySagaInstanceId(sagaId);

        long count = completedStepsBySagaInstanceId.stream().filter(sagaStep -> compensateStep(sagaStep.getId(), sagaStep.getStepName())).count();
        if (count != completedStepsBySagaInstanceId.size()) {
            log.info("Saga instance has failed with id {}", sagaInstance.getId());
        } else {
            sagaInstance.setStatus(SagaStatus.COMPENSATED);
            sagaInstanceRepository.save(sagaInstance);
            log.info("Saga instance has been compensated with id {}", sagaInstance.getId());
        }

    }

    @Override
    @Transactional
    public void failSaga(Long sagaId) {

        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaId).orElseThrow(() -> new RuntimeException("Saga instance with id " + sagaId + " not found"));

        sagaInstance.setStatus(SagaStatus.FAILED);

        sagaInstanceRepository.save(sagaInstance);
        log.info("Saga instance has been Failed with id {}", sagaInstance.getId());

        compensateSaga(sagaInstance.getId());
        log.info("Saga compensated {}", sagaInstance.getId());
    }

    @Override
    @Transactional
    public void completeSaga(Long sagaId) {

        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaId).orElseThrow(() -> new RuntimeException("Saga instance with id " + sagaId + " not found"));

        sagaInstance.setStatus(SagaStatus.COMPLETED);

        sagaInstanceRepository.save(sagaInstance);
        log.info("Saga instance has been completed with id {}", sagaInstance.getId());


    }
}
