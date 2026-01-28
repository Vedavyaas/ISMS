package com.adl.isms.configuration;


import com.adl.isms.repository.CourseEntity;
import com.adl.isms.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseDataInitializer implements CommandLineRunner {

    private final CourseRepository courseRepository;

    public CourseDataInitializer(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<CourseEntity> courseEntityList = List.of(
                new CourseEntity("Calculus", "23M101", 4),
                new CourseEntity("Transforms", "23M2023", 4),
                new CourseEntity("Linear Algebra", "23M201", 3),
                new CourseEntity("Discrete Mathematics", "23M202", 3),
                new CourseEntity("Probability and Statistics", "23M301", 3),

                new CourseEntity("Introduction to Programming", "23CS101", 4),
                new CourseEntity("Data Structures", "23CS201", 4),
                new CourseEntity("Algorithms", "23CS301", 4),
                new CourseEntity("Database Management Systems", "23CS302", 3),
                new CourseEntity("Operating Systems", "23CS303", 4),
                new CourseEntity("Computer Networks", "23CS304", 3),
                new CourseEntity("Software Engineering", "23CS305", 3),

                new CourseEntity("Basic Electrical Engineering", "23EE101", 3),
                new CourseEntity("Circuit Theory", "23EE201", 4),
                new CourseEntity("Digital Electronics", "23EE202", 3),
                new CourseEntity("Signals and Systems", "23EE301", 4),
                new CourseEntity("Power Systems", "23EE302", 4),

                new CourseEntity("Engineering Mechanics", "23ME101", 3),
                new CourseEntity("Thermodynamics", "23ME201", 4),
                new CourseEntity("Fluid Mechanics", "23ME202", 4),
                new CourseEntity("Machine Design", "23ME301", 3),
                new CourseEntity("Heat Transfer", "23ME302", 3),

                new CourseEntity("Engineering Drawing", "23CE101", 3),
                new CourseEntity("Structural Analysis", "23CE201", 4),
                new CourseEntity("Geotechnical Engineering", "23CE202", 3),
                new CourseEntity("Transportation Engineering", "23CE301", 3),
                new CourseEntity("Environmental Engineering", "23CE302", 3),

                new CourseEntity("Electronic Devices", "23EC201", 3),
                new CourseEntity("Analog Circuits", "23EC202", 3),
                new CourseEntity("Digital Communication", "23EC301", 4),
                new CourseEntity("Microprocessors", "23EC302", 3),

                new CourseEntity("Engineering Physics", "23P101", 3),
                new CourseEntity("Quantum Mechanics", "23P201", 4),
                new CourseEntity("Electromagnetic Theory", "23P301", 4),

                new CourseEntity("Engineering Chemistry", "23CH101", 3),
                new CourseEntity("Physical Chemistry", "23CH201", 3),
                new CourseEntity("Organic Chemistry", "23CH202", 3),

                new CourseEntity("Cell Biology", "23BT101", 3),
                new CourseEntity("Genetics", "23BT201", 3),
                new CourseEntity("Biochemistry", "23BT301", 4),

                new CourseEntity("Engineering Economics", "23ECON101", 3),
                new CourseEntity("Microeconomics", "23ECON201", 3),
                new CourseEntity("Macroeconomics", "23ECON202", 3),

                new CourseEntity("Principles of Management", "23MG101", 3),
                new CourseEntity("Organizational Behavior", "23MG201", 3),
                new CourseEntity("Financial Management", "23MG301", 4),
                new CourseEntity("Marketing Management", "23MG302", 3),

                new CourseEntity("Architectural Design I", "23AR101", 4),
                new CourseEntity("Building Materials", "23AR201", 3),
                new CourseEntity("Urban Planning", "23AR301", 3),

                new CourseEntity("Technical Communication", "23HS101", 2),
                new CourseEntity("Professional Ethics", "23HS201", 2),
                new CourseEntity("Environmental Studies", "23HS202", 2),

                new CourseEntity("Artificial Intelligence", "23AI301", 4),
                new CourseEntity("Machine Learning", "23AI302", 4),
                new CourseEntity("Data Science", "23DS301", 4),
                new CourseEntity("Cyber Security", "23CS401", 3),
                new CourseEntity("Internet of Things", "23EC401", 3),
                new CourseEntity("Cloud Computing", "23CS402", 3),
                new CourseEntity("Blockchain Technology", "23CS403", 3)
        );

        courseRepository.saveAllAndFlush(courseEntityList);
    }
}
