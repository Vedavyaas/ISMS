package com.adl.isms.repository;

import jakarta.persistence.*;

@Entity
public class AttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private CourseEntity course;
    private double attendancePercentage;

    public AttendanceEntity() {}

    public AttendanceEntity(CourseEntity course, double attendancePercentage) {
        this.course = course;
        this.attendancePercentage = attendancePercentage;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public CourseEntity getCourse() {
        return course;
    }

    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}
