package com.example.wallet_system.controllers;

import com.example.wallet_system.dtos.TransferRequestDTO;
import com.example.wallet_system.dtos.TransferResponseDTO;
import com.example.wallet_system.services.TransactionService;
import com.example.wallet_system.services.TransferSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransferSagaService transferSagaService;

    @PostMapping
    public ResponseEntity<TransferResponseDTO> createTransaction(@RequestBody TransferRequestDTO transferRequestDTO) {
        Long sagaInstanceId = transferSagaService.initiateTransfer(transferRequestDTO.getFromWalletId(), transferRequestDTO.getToWalletId(), transferRequestDTO.getAmount(), transferRequestDTO.getDescription());

        return ResponseEntity.ok().body(TransferResponseDTO.builder().sagaInstanceId(sagaInstanceId).build());
    }

}
