package com.example.wallet_system.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;


    @Column(name = "balance", nullable = false)
    private BigDecimal balance;


    public boolean hasSufficientBalance(BigDecimal balance) {
        return this.balance.compareTo(balance) >= 0;
    }
    public void deductBalance(BigDecimal amount) throws IllegalAccessException {
        if(!hasSufficientBalance(amount)){
            throw new IllegalAccessException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void creditBalance(BigDecimal amount) throws IllegalAccessException {
        if(!hasSufficientBalance(amount)){
            throw new IllegalAccessException("Insufficient balance");
        }
        this.balance = this.balance.add(amount);
    }



}
