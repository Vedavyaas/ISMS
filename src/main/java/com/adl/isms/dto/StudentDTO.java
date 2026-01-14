package com.adl.isms.dto;

import com.adl.isms.assests.EnrolmentStatus;

import java.time.LocalDate;

public record StudentDTO(String name, LocalDate dateOfBirth, String email, EnrolmentStatus enrolmentStatus) {
}
