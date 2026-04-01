package com.adl.isms.service;

import com.adl.isms.repository.FinanceEntity;
import com.adl.isms.repository.FinanceRepository;
import com.adl.isms.repository.StudentEntity;
import com.adl.isms.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FinanceSchedulerService {
    private final StudentRepository studentRepository;
    private final FinanceRepository financeRepository;

    public FinanceSchedulerService(StudentRepository studentRepository, FinanceRepository financeRepository) {
        this.studentRepository = studentRepository;
        this.financeRepository = financeRepository;
    }

    @Scheduled(fixedDelay = 1_000)
    @Transactional
    public void scheduled() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<StudentEntity> page;

        do {
            page = studentRepository.findByFinanceUpdated(false, pageable);

            List<FinanceEntity> financeEntities = new ArrayList<>();
            for (var student : page.getContent()) {
                FinanceEntity financeEntity = new FinanceEntity(student, 69_000, 0);
                financeEntities.add(financeEntity);
                student.setFinanceUpdated(true);
            }

            financeRepository.saveAll(financeEntities);
            studentRepository.saveAll(page);
        } while (page.hasNext());
    }
}
