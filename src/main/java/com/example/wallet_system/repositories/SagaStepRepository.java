package com.example.wallet_system.repositories;


import com.example.wallet_system.entities.SagaStep;
import com.example.wallet_system.enums.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    List<SagaStep> findBySagaInstanceId(Long sagaId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaId AND s.status = 'COMPLETED'")
    List<SagaStep> findCompletedStepsBySagaInstanceId(@Param("sagaId") Long sagaId);


    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaId AND s.status IN ('COMPLETED', 'COMPENSATED')")
    List<SagaStep> findCompletedOrCompensatedStepsBySagaInstanceId(@Param("sagaId") Long sagaId);

    List<SagaStep> findBySagaInstanceIdAndStatus(Long sagaId, StepStatus status);

    Optional<SagaStep> findBySagaInstanceIdAndStepName(Long sagaId, String stepName);

    Optional<SagaStep> findBySagaInstanceIdAndStepNameAndStatus(Long sagaId, String stepName,StepStatus status);
}
