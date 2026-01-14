package com.adl.isms.service;

import com.adl.isms.dto.FacultyDTO;
import com.adl.isms.dto.StudentDTO;
import com.adl.isms.repository.*;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.adl.isms.assests.Role;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.MappingIterator;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CSVService {

    private final Logger logger = LoggerFactory.getLogger(CSVService.class);
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    public CSVService(UserRepository userRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder, FacultyRepository facultyRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.facultyRepository = facultyRepository;
    }

    @Transactional
    public String updateStudentData(String username, MultipartFile file) {
        Optional<UserEntity> operator = userRepository.findByUserName(username);

        if (operator.isEmpty()) return "User not found";
        if (operator.get().getRole() != Role.ADMIN) return "You don't have enough permissions!";

        try {
            List<StudentDTO> dtos = parseStudentCsv(file);

            List<StudentEntity> entities = dtos.stream().map(dto -> {
                UserEntity user = new UserEntity(dto.name(), passwordEncoder.encode(dto.dateOfBirth().toString()), Role.STUDENT);
                return new StudentEntity(user, dto.name(), dto.dateOfBirth(), dto.email(), dto.enrolmentStatus());
            }).toList();

            studentRepository.saveAll(entities);
            return "Successfully updated Student data!";
        } catch (Exception e) {
            logger.error("Error processing StudentDTO: {}", e.getMessage());
            return "Failed to update data";
        }
    }

    @Transactional
    public String updateFacultyData(String username, MultipartFile file) {
        Optional<UserEntity> operator = userRepository.findByUserName(username);

        if (operator.isEmpty()) return "User not found";
        if (operator.get().getRole() != Role.ADMIN) return "You don't have enough permissions!";

        try {
            List<FacultyDTO> dtos = parseFacultyCsv(file);

            List<FacultyEntity> entities = dtos.stream().map(dto -> {
                UserEntity user = new UserEntity(dto.name(), passwordEncoder.encode(UUID.randomUUID().toString()), Role.FACULTY);
                return new FacultyEntity(user, dto.name(), dto.department(), dto.designation());
            }).toList();

            facultyRepository.saveAll(entities);
            return "Successfully updated Faculty data!";
        } catch (Exception e) {
            logger.error("Error processing FacultyDTO: {}", e.getMessage());
            return "Failed to update data";
        }
    }

    protected List<StudentDTO> parseStudentCsv(MultipartFile file) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        try (MappingIterator<StudentDTO> it = csvMapper.readerFor(StudentDTO.class).with(csvSchema).readValues(file.getInputStream())) {
            return it.readAll();
        }
    }

    protected List<FacultyDTO> parseFacultyCsv(MultipartFile file) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        try (MappingIterator<FacultyDTO> it = csvMapper.readerFor(FacultyDTO.class).with(csvSchema).readValues(file.getInputStream())) {
            return it.readAll();
        }
    }
}