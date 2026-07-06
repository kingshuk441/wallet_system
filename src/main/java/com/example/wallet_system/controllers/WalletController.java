package com.example.wallet_system.controllers;

import com.example.wallet_system.dtos.CreateWalletRequestDTO;
import com.example.wallet_system.dtos.CreditDebitWalletDTO;
import com.example.wallet_system.entities.Wallet;
import com.example.wallet_system.services.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@AllArgsConstructor
@RequestMapping("/wallet")
public class WalletController {
    private final WalletService walletService;


    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletRequestDTO createWalletRequestDTO) {
        Wallet wallet = walletService.createWallet(createWalletRequestDTO.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable Long userId) {
        Wallet wallet = walletService.getWallet(userId);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getWalletBalanceById(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getBalance(userId));
    }

    @PostMapping("/{userId}/debit")
    public ResponseEntity<Wallet> debit(@PathVariable Long userId, @RequestBody CreditDebitWalletDTO creditDebitWalletDTO) {
        walletService.debit(userId, creditDebitWalletDTO.getAmount());
        return ResponseEntity.status(HttpStatus.OK).body(getWalletById(userId).getBody());
    }

    @PostMapping("/{userId}/credit")
    public ResponseEntity<Wallet> credit(@PathVariable Long userId, @RequestBody CreditDebitWalletDTO creditDebitWalletDTO) {
        walletService.credit(userId, creditDebitWalletDTO.getAmount());
        return ResponseEntity.status(HttpStatus.OK).body(getWalletById(userId).getBody());
    }
}
