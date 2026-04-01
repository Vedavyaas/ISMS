package com.adl.isms.controller;

import com.adl.isms.repository.FinanceEntity;
import com.adl.isms.service.FinanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaymentController {
    private final FinanceService financeService;

    public PaymentController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/get/finance/status")
    public List<FinanceEntity> getPaymentStatus() {
        return financeService.getFinanceStatus();
    }

    @PostMapping("/put/finance/amountPaid")
    public String putPaymentAmountPaid(@RequestParam Long id, @RequestParam double amountPaid) {
        return financeService.updateAmountPaid(id, amountPaid);
    }
}