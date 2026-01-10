package com.adl.isms.repository;

import com.adl.isms.assests.EnrolmentStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id", unique = true)
    private UserEntity userId;
    private String name;
    private LocalDate dateOfBirth;
    private String email;
    @Enumerated(EnumType.STRING)
    private EnrolmentStatus enrolmentStatus;

    public StudentEntity() {
    }

    public StudentEntity(UserEntity userId, String name, LocalDate dateOfBirth, String email, EnrolmentStatus enrolmentStatus) {
        this.userId = userId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.enrolmentStatus = enrolmentStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EnrolmentStatus getEnrolmentStatus() {
        return enrolmentStatus;
    }

    public void setEnrolmentStatus(EnrolmentStatus enrolmentStatus) {
        this.enrolmentStatus = enrolmentStatus;
    }
}
