package com.example.wallet_system.repositories;


import com.example.wallet_system.entities.SagaStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    List<SagaStep> findBySagaInstanceId(Long sagaId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaId AND s.status = 'COMPLETED'")
    List<SagaStep> findCompletedStepsBySagaInstanceId(@Param("sagaId") Long sagaId);


    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaId AND s.status IN ('COMPLETED', 'COMPENSATED')")
    List<SagaStep> findCompletedOrCompensatedStepsBySagaInstanceId(@Param("sagaId") Long sagaId);
}
