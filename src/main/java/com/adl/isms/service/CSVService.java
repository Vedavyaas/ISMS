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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.MappingIterator;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@Service
public class CSVService {
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
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public String updateStudentData(MultipartFile file) {
        List<StudentDTO> dtos;
        try {
            dtos = parseStudentCsv(file);
        } catch (IOException e){
            return "Retry later";
        }
        if(csvCheck(dtos, StudentDTO::email)) return "Duplicate data found";

        List<StudentEntity> entities = dtos.stream().map(dto -> {
            UserEntity user = new UserEntity(dto.name(), passwordEncoder.encode(dto.dateOfBirth().toString()), Role.STUDENT);
            return new StudentEntity(user, dto.name(), dto.dateOfBirth(), dto.email(), dto.enrolmentStatus(), dto.currentSemester(), dto.department());
        }).toList();

        studentRepository.saveAllAndFlush(entities);
        return "Successfully updated Student data!";
    }

    @Transactional
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public String updateFacultyData(MultipartFile file) {
        List<FacultyDTO> dtos;
        try {
            dtos = parseFacultyCsv(file);
        } catch (IOException e) {
            return "Retry later";
        }

        if(csvCheck(dtos, FacultyDTO::email)) return "Duplicate data found";

        List<FacultyEntity> entities = dtos.stream().map(dto -> {
            UserEntity user = new UserEntity(dto.name(), passwordEncoder.encode(UUID.randomUUID().toString()), Role.FACULTY);
            return new FacultyEntity(user, dto.name(), dto.email(), dto.department(), dto.designation());
        }).toList();

        facultyRepository.saveAllAndFlush(entities);
        return "Successfully updated Faculty data!";
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

    private <T> boolean csvCheck(List<T> dtos, Function<T, String> key){
        Set<String> seenEmails = new HashSet<>();

        for(T dto : dtos){
            if (!seenEmails.add(key.apply(dto))) {
                return true;
            }
        }
        return false;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Transactional
    public String updateFaculty(FacultyDTO facultyDTO) {
        if(facultyRepository.existsByEmail(facultyDTO.email())) return "Already exists";

        UserEntity userEntity = new UserEntity(facultyDTO.name(), passwordEncoder.encode(UUID.randomUUID().toString()), Role.FACULTY);
        FacultyEntity facultyEntity = new FacultyEntity(userEntity, facultyDTO.name(), facultyDTO.email(), facultyDTO.department(), facultyDTO.designation());

        facultyRepository.save(facultyEntity);

        return "Successfully updated faculty data";
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Transactional
    public String updateStudent(StudentDTO studentDTO) {
        if(studentRepository.existsByEmail(studentDTO.email())) return "Already exists";

        UserEntity userEntity = new UserEntity(studentDTO.name(), passwordEncoder.encode(studentDTO.dateOfBirth().toString()), Role.STUDENT);

        StudentEntity studentEntity = new StudentEntity(userEntity, studentDTO.name(), studentDTO.dateOfBirth(), studentDTO.email(), studentDTO.enrolmentStatus(), studentDTO.currentSemester(), studentDTO.department());
        studentRepository.save(studentEntity);

        return "Successfully updated student data";
    }
}