package com.example.wallet_system.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequestDTO {
    private Long fromWalletId; // userId
    private Long toWalletId;
    private BigDecimal amount;
    private String description;

}
