package com.adl.isms.service;

import com.adl.isms.assests.PaymentStatus;
import com.adl.isms.repository.FinanceEntity;
import com.adl.isms.repository.FinanceRepository;
import com.adl.isms.repository.StudentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FinanceServiceTest {

    @Mock
    private FinanceRepository financeRepository;

    @InjectMocks
    private FinanceService financeService;

    private FinanceEntity financeEntity;
    private StudentEntity studentEntity;

    @BeforeEach
    void setUp() {
        studentEntity = new StudentEntity();
        financeEntity = new FinanceEntity(studentEntity, 69000, 0);
        financeEntity.setId(1L);
    }

    @Test
    void getFinanceStatus_Success() {
        when(financeRepository.findAll()).thenReturn(List.of(financeEntity));

        List<FinanceEntity> result = financeService.getFinanceStatus();

        assertEquals(1, result.size());
        verify(financeRepository, times(1)).findAll();
    }

    @Test
    void updateAmountPaid_Success() {
        when(financeRepository.findById(1L)).thenReturn(Optional.of(financeEntity));

        String result = financeService.updateAmountPaid(1L, 10000);

        assertEquals("Finance data for given student successfully updated", result);
        assertEquals(10000, financeEntity.getAmountPaid());
        assertEquals(59000, financeEntity.getAmountDue());
        verify(financeRepository, times(1)).save(financeEntity);
    }

    @Test
    void updateAmountPaid_FullyPaid_Success() {
        when(financeRepository.findById(1L)).thenReturn(Optional.of(financeEntity));

        String result = financeService.updateAmountPaid(1L, 69000);

        assertEquals("Finance data for given student successfully updated", result);
        assertEquals(69000, financeEntity.getAmountPaid());
        assertEquals(0, financeEntity.getAmountDue());
        assertEquals(PaymentStatus.PAID, financeEntity.getPaymentStatus());
        verify(financeRepository, times(1)).save(financeEntity);
    }

    @Test
    void updateAmountPaid_NotFound() {
        when(financeRepository.findById(1L)).thenReturn(Optional.empty());

        String result = financeService.updateAmountPaid(1L, 10000);

        assertEquals("Finance data for given student not found", result);
        verify(financeRepository, never()).save(any());
    }

    @Test
    void updateAmountPaid_InvalidAmount() {
        when(financeRepository.findById(1L)).thenReturn(Optional.of(financeEntity));

        String result = financeService.updateAmountPaid(1L, -10);

        assertEquals("Amount is negative - Invalid", result);
        verify(financeRepository, never()).save(any());
    }

    @Test
    void updateAmountPaid_AlreadyPaid() {
        financeEntity.setPaymentStatus(PaymentStatus.PAID);
        when(financeRepository.findById(1L)).thenReturn(Optional.of(financeEntity));

        String result = financeService.updateAmountPaid(1L, 10000);

        assertEquals("Amount paid already.", result);
        verify(financeRepository, never()).save(any());
    }

    @Test
    void updateAmountPaid_GreaterThanDue() {
        when(financeRepository.findById(1L)).thenReturn(Optional.of(financeEntity));

        String result = financeService.updateAmountPaid(1L, 70000);

        assertEquals("Amount is greater than due amount", result);
        verify(financeRepository, never()).save(any());
    }
}
