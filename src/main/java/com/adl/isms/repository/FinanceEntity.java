package com.adl.isms.repository;

import com.adl.isms.assests.PaymentStatus;
import jakarta.persistence.*;

@Entity
public class FinanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "id", unique = true)
    private StudentEntity student;

    private double amountDue;
    private double amountPaid;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    public FinanceEntity() {}

    public FinanceEntity(StudentEntity student, double amountDue, double amountPaid) {
        this.student = student;
        this.amountDue = amountDue;
        this.amountPaid = amountPaid;
        this.paymentStatus = amountPaid >= amountDue ? PaymentStatus.PAID : PaymentStatus.NOT_PAID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentEntity getStudent() {
        return student;
    }

    public void setStudent(StudentEntity student) {
        this.student = student;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
