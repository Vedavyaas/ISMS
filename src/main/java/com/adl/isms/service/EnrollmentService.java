package com.adl.isms.service;

import com.adl.isms.assests.Department;
import com.adl.isms.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EnrollmentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final EnrolmentRepository enrolmentRepository;

    public EnrollmentService(StudentRepository studentRepository, CourseRepository courseRepository, FacultyRepository facultyRepository, EnrolmentRepository enrolmentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.enrolmentRepository = enrolmentRepository;
    }

    @Scheduled(fixedDelay = 100_000)
    @Async
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void scheduleAllStudents() {
        System.out.println("Starting async scheduling for all students...");
        List<StudentEntity> students = studentRepository.findAll();
        List<FacultyEntity> allFaculty = facultyRepository.findAll();

        if (allFaculty.isEmpty()) {
            return;
        }

        // To calculate proper workload, keep track of how many students each faculty is handling for a course
        Map<FacultyEntity, Integer> workload = new HashMap<>();
        for (FacultyEntity f : allFaculty) {
            workload.put(f, 0);
        }

        // Clear existing enrolments to ensure fresh scheduling based on accuracy
        enrolmentRepository.deleteAll();

        for (StudentEntity student : students) {
            Integer semester = student.getCurrentSemester();
            if (semester == null) continue;

            List<CourseEntity> courses = courseRepository.findAllBySemester(semester);

            for (CourseEntity course : courses) {
                if (isCourseForStudent(course, student)) {
                    FacultyEntity assignedFaculty = assignFacultyWithBalancedWorkload(course, allFaculty, workload);

                    EnrolmentEntity enrolment = new EnrolmentEntity();
                    enrolment.setStudent(student);
                    enrolment.setCourse(course);
                    enrolment.setFacultyEntity(assignedFaculty);
                    int year = (semester + 1) / 2;
                    enrolment.setSemesterID("YEAR_" + year + "_SEM_" + semester);

                    enrolmentRepository.save(enrolment);
                }
            }
        }
        System.out.println("Finished async scheduling for " + students.size() + " students.");
    }

    private boolean isCourseForStudent(CourseEntity course, StudentEntity student) {
        String subjCode = getSubjectCode(course.getCourseCode());
        int sem = student.getCurrentSemester();

        // In first year (sem 1 & 2), math is to all
        if ((sem == 1 || sem == 2) && "M".equals(subjCode)) {
            return true;
        }

        // Map exactly with the course code like CS for cs student
        String deptPrefix = getPrefixForDepartment(student.getDepartment());
        return deptPrefix.equals(subjCode);
    }

    private String getSubjectCode(String courseCode) {
        return courseCode.replaceAll("^\\d+", "").replaceAll("\\d+$", "");
    }

    private String getPrefixForDepartment(Department dept) {
        if (dept == null) return "";
        switch (dept) {
            case CSE: return "CS";
            case ECE: return "EC";
            case EEE: return "EE";
            case CIVIL: return "CE";
            case MECHANICAL: return "ME";
            case IT: return "IT";
            case AIML: return "AI";
            case BIOMEDICAL: return "BT";
            case MATHS: return "M";
            case AUTOMOBILE: return "AU";
            case PRODUCTION: return "PR";
            case TEXTILE: return "TE";
            case FASHION: return "FA";
            default: return dept.name().substring(0, 2).toUpperCase();
        }
    }

    private Department getDepartmentForPrefix(String prefix) {
        switch (prefix) {
            case "CS": return Department.CSE;
            case "EC": return Department.ECE;
            case "EE": return Department.EEE;
            case "CE": return Department.CIVIL;
            case "ME": return Department.MECHANICAL;
            case "IT": return Department.IT;
            case "AI": return Department.AIML;
            case "BT": return Department.BIOMEDICAL;
            case "M": return Department.MATHS;
            case "AU": return Department.AUTOMOBILE;
            case "PR": return Department.PRODUCTION;
            case "TE": return Department.TEXTILE;
            case "FA": return Department.FASHION;
            default: return null;
        }
    }

    private FacultyEntity assignFacultyWithBalancedWorkload(CourseEntity course, List<FacultyEntity> allFaculty, Map<FacultyEntity, Integer> workload) {
        String subjCode = getSubjectCode(course.getCourseCode());
        Department courseDept = getDepartmentForPrefix(subjCode);

        List<FacultyEntity> candidateFaculties = allFaculty.stream()
                .filter(f -> f.getDepartment() == courseDept)
                .toList();

        if (candidateFaculties.isEmpty()) {
            candidateFaculties = allFaculty;
        }

        FacultyEntity selected = candidateFaculties.get(0);
        int minLoad = workload.get(selected);

        for (FacultyEntity f : candidateFaculties) {
            int load = workload.get(f);
            if (load < minLoad) {
                minLoad = load;
                selected = f;
            }
        }

        workload.put(selected, minLoad + 1);
        return selected;
    }

    public List<com.adl.isms.dto.StudentCourseViewDTO> getStudentCourses(String username) {
        List<EnrolmentEntity> enrolments = enrolmentRepository.findAllByStudent_UserId_UserName(username);
        return enrolments.stream()
                .map(e -> new com.adl.isms.dto.StudentCourseViewDTO(
                        e.getCourse().getCourseName(),
                        e.getCourse().getCourseCode(),
                        e.getFacultyEntity().getName(),
                        e.getSemesterID()
                ))
                .toList();
    }

    public List<com.adl.isms.dto.FacultyCourseViewDTO> getFacultyCourses(String username) {
        List<EnrolmentEntity> enrolments = enrolmentRepository.findAllByFacultyEntity_User_UserName(username);
        
        return enrolments.stream()
                .collect(java.util.stream.Collectors.groupingBy(e -> e.getCourse()))
                .entrySet().stream()
                .map(entry -> {
                    CourseEntity course = entry.getKey();
                    long count = entry.getValue().size();
                    String semesterId = entry.getValue().get(0).getSemesterID(); 
                    return new com.adl.isms.dto.FacultyCourseViewDTO(
                            course.getCourseName(),
                            course.getCourseCode(),
                            semesterId,
                            count
                    );
                })
                .toList();
    }
}
