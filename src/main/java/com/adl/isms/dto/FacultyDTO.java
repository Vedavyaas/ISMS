package com.adl.isms.dto;

import com.adl.isms.assests.Department;
import com.adl.isms.assests.Designation;

public record FacultyDTO(String name, String email, Department department, Designation designation) {
}
