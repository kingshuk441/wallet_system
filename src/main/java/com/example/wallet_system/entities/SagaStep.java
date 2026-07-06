package com.example.wallet_system.entities;

import com.example.wallet_system.enums.StepStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "saga_step")
@Entity
public class SagaStep {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "saga_instance_id",nullable = false)
    private Long sagaInstanceId;

    @Column(name = "step_name",nullable = false)
    private String stepName;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private StepStatus sagaStepStatus;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "step_data",columnDefinition = "json")
    private String stepData;
}
