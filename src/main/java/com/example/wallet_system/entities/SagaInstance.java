package com.example.wallet_system.entities;


import com.example.wallet_system.enums.SagaStatus;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.NoArgsConstructor;


import org.apache.calcite.model.JsonType;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "saga_instance")
@Entity
public class SagaInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "context",columnDefinition = "json")
    @Type(JsonType.class)
    private String context;

    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private SagaStatus status;


    @Column(name = "current_step")
    private String currentStep;

}
