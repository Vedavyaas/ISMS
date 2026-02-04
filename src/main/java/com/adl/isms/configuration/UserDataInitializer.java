package com.adl.isms.configuration;

import com.adl.isms.assests.Department;
import com.adl.isms.assests.Designation;
import com.adl.isms.assests.EnrolmentStatus;
import com.adl.isms.assests.Role;
import com.adl.isms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    public UserDataInitializer(UserRepository userRepository, StudentRepository studentRepository, FacultyRepository facultyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeAdmin();
        initializeStudents();
        initializeFaculty();
    }

    private void initializeAdmin() {
        if (userRepository.existsByUserName("admin")) return;
        userRepository.save(new UserEntity("admin", passwordEncoder.encode("admin123"), Role.ADMIN));
    }

    private void initializeStudents() {
        if (studentRepository.count() > 0) {
            return;
        }

        List<StudentEntity> students = new ArrayList<>();

        // Hardcoded initial students
        students.add(createStudent("vedavyaas", "password", "Vedavyaas", "vedavyaas@example.com", EnrolmentStatus.ACTIVELY_STUDYING, 5, Department.CSE));
        students.add(createStudent("johndoe", "password", "John Doe", "john.doe@example.com", EnrolmentStatus.ACTIVELY_STUDYING, 3, Department.ECE));
        students.add(createStudent("student", "student123", "Default Student", "student@example.com", EnrolmentStatus.ACTIVELY_STUDYING, 1, Department.IT));
        students.add(createStudent("maths_student", "password", "Maths Genius", "maths.student@example.com", EnrolmentStatus.ACTIVELY_STUDYING, 2, Department.MATHS));
        
        // Generate ~38 more students
        String[] firstNames = {"James", "Mary", "Robert", "Patricia", "John", "Jennifer", "Michael", "Linda", "David", "Elizabeth", "William", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Sarah", "Charles", "Karen"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"};

        for (int i = 0; i < 38; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String name = firstName + " " + lastName;
            String username = (firstName + lastName + i).toLowerCase().replaceAll("[^a-z0-9]", ""); // Ensure username integrity
            String email = username + "@student.isms.edu";
            EnrolmentStatus status = EnrolmentStatus.values()[random.nextInt(EnrolmentStatus.values().length)];
            
            // Check if username/email already added in this batch to avoid duplicates
            boolean exists = students.stream().anyMatch(s -> s.getEmail().equals(email));
            if (!exists) {
                int semester = 1 + random.nextInt(8); // Semester 1 to 8
                Department department = Department.values()[random.nextInt(Department.values().length)];
                students.add(createStudent(username, "password", name, email, status, semester, department));
            }
        }

        studentRepository.saveAll(students);
        System.out.println("Initialized " + students.size() + " Students");
    }

    private StudentEntity createStudent(String username, String password, String name, String email, EnrolmentStatus status, Integer semester, Department department) {
        UserEntity user = new UserEntity(username, passwordEncoder.encode(password), Role.STUDENT);
        // Random DOB between 18 and 25 years ago
        LocalDate dob = LocalDate.now().minusYears(18 + random.nextInt(8)).minusDays(random.nextInt(365));
        return new StudentEntity(user, name, dob, email, status, semester, department);
    }

    private void initializeFaculty() {
        if (facultyRepository.count() > 0) {
            return;
        }

        List<FacultyEntity> facultyList = new ArrayList<>();

        // Hardcoded initial faculty
        facultyList.add(createFaculty("alanturing", "password", "Dr. Alan Turing", "alan.turing@example.com", Department.CSE, Designation.PROFESSOR));
        facultyList.add(createFaculty("faculty", "faculty123", "Default Faculty", "faculty@example.com", Department.CSE, Designation.LECTURER));
        facultyList.add(createFaculty("ramanujan", "password", "Srinivasa Ramanujan", "ramanujan@example.com", Department.MATHS, Designation.PROFESSOR));
        facultyList.add(createFaculty("maths_fac", "password", "Dr. Math", "dr.math@example.com", Department.MATHS, Designation.ASSOCIATE_PROFESSOR));

        String[] firstNames = {"George", "Lisa", "Kevin", "Sandra", "Brian", "Ashley", "Edward", "Kimberly", "Ronald", "Donna", "Anthony", "Carol", "Jason", "Michelle", "Jeffrey", "Emily", "Ryan", "Amanda", "Jacob", "Melissa"};
        String[] lastNames = {"White", "Clark", "Hall", "Lewis", "Young", "Walker", "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores", "Green", "Adams", "Nelson", "Baker", "Carter", "Mitchell"};

        for (int i = 0; i < 39; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String name = (random.nextBoolean() ? "Dr. " : "Prof. ") + firstName + " " + lastName;
            String username = ("fac_" + firstName + lastName + i).toLowerCase().replaceAll("[^a-z0-9_]", "");
            String email = username + "@faculty.isms.edu";
            Department dept = Department.values()[random.nextInt(Department.values().length)];
            Designation desig = Designation.values()[random.nextInt(Designation.values().length)];

            boolean exists = facultyList.stream().anyMatch(f -> f.getEmail().equals(email));
            if (!exists) {
                facultyList.add(createFaculty(username, "password", name, email, dept, desig));
            }
        }

        facultyRepository.saveAll(facultyList);
        System.out.println("Initialized " + facultyList.size() + " Faculty members");
    }

    private FacultyEntity createFaculty(String username, String password, String name, String email, Department dept, Designation desig) {
        UserEntity user = new UserEntity(username, passwordEncoder.encode(password), Role.FACULTY);
        return new FacultyEntity(user, name, email, dept, desig);
    }
}
