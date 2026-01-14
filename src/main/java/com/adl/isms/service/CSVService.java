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
        return processCsv(username, file, StudentDTO.class, dto -> {
            UserEntity user = new UserEntity(dto.name(), passwordEncoder.encode(dto.dateOfBirth().toString()), Role.STUDENT);
            return new StudentEntity(user, dto.name(), dto.dateOfBirth(), dto.email(), dto.enrolmentStatus());
        }, studentRepository);
    }

    @Transactional
    public String updateFacultyData(String username, MultipartFile file) {
        return processCsv(username, file, FacultyDTO.class, dto -> {
            UserEntity user = new UserEntity(dto.name(), passwordEncoder.encode(UUID.randomUUID().toString()), Role.FACULTY);
            return new FacultyEntity(user, dto.name(), dto.department(), dto.designation());
        }, facultyRepository);
    }
    private <D, E> String processCsv(String username, MultipartFile file, Class<D> dtoClass, java.util.function.Function<D, E> mapper, org.springframework.data.jpa.repository.JpaRepository<E, ?> repository) {

        UserEntity operator = userRepository.findByUserName(username).orElse(null);

        if (operator == null) return "User not found";
        if (operator.getRole() != Role.ADMIN) return "You don't have enough permissions!";

        try {
            List<D> dtos = parseCsv(file, dtoClass);
            List<E> entities = dtos.stream().map(mapper).toList();
            repository.saveAll(entities);
            return "Successfully updated " + dtoClass.getSimpleName().replace("DTO", "") + " data!";
        } catch (Exception e) {
            logger.error("Error processing {}: {}", dtoClass.getSimpleName(), e.getMessage());
            return "Failed to update data";
        }
    }

    private <T> List<T> parseCsv(MultipartFile file, Class<T> clazz) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        csvMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        try (MappingIterator<T> it = csvMapper.readerFor(clazz).with(csvSchema).readValues(file.getInputStream())) {
            return it.readAll();
        }
    }
}