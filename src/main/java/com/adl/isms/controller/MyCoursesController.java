package com.adl.isms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyCoursesController {

    @GetMapping("/my-courses")
    public String myCourses() {
        return "my_courses";
    }
}
