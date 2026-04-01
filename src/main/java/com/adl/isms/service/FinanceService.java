package com.adl.isms.service;

import com.adl.isms.assests.PaymentStatus;
import com.adl.isms.repository.FinanceEntity;
import com.adl.isms.repository.FinanceRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinanceService {
    private final FinanceRepository financeRepository;

    public FinanceService(FinanceRepository financeRepository) {
        this.financeRepository = financeRepository;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public List<FinanceEntity> getFinanceStatus() {
        return financeRepository.findAll();
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public String updateAmountPaid(Long id, double amountPaid) {
        Optional<FinanceEntity> financeEntity = financeRepository.findById(id);
        if (financeEntity.isEmpty()) {
            return "Finance data for given student not found";
        }

        if (amountPaid <= 0) return "Amount is negative - Invalid";
        if (financeEntity.get().getPaymentStatus().equals(PaymentStatus.PAID)) return "Amount paid already.";
        if (amountPaid > financeEntity.get().getAmountDue()) return "Amount is greater than due amount";

        financeEntity.get().setAmountPaid(financeEntity.get().getAmountPaid() + amountPaid);
        financeEntity.get().setAmountDue(69_000 - financeEntity.get().getAmountPaid());

        if (financeEntity.get().getAmountDue() <= 0) financeEntity.get().setPaymentStatus(PaymentStatus.PAID);
        financeRepository.save(financeEntity.get());
        return "Finance data for given student successfully updated";
    }
}
