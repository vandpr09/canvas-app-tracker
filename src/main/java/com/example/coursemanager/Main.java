package com.example.coursemanager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //Create a dummy course
        Course myCourse = new Course("COMM 290: Comm Problems", "Prof. Turkiewicz");

        myCourse.addItem(new Reading("Chap 8 & 10", LocalDate.of(2026, 4, 20), LocalDate.of(2026, 4, 26), "none", "Chapters 8, 10"));
        myCourse.addItem(new Assignment("Final Research Paper", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 10), "in progress", 150.0));
        myCourse.addItem(new Quiz("Midterm Check-in", LocalDate.of(2026, 3, 15), LocalDate.of(2026, 3, 15), "ranger", 20));
        myCourse.addItem(new Exam("Final Exam", LocalDate.of(2026, 5, 15), LocalDate.of(2026, 5, 15), "none", "Room 304", 25.0));

        System.out.println("--- BEFORE SAVING ---");
        myCourse.printSyllabus();

        // Save it to a file
        List<Course> coursesToSave = new ArrayList<>();
        coursesToSave.add(myCourse);
        CourseFileManager.saveCoursesToFile(coursesToSave, "my_courses.csv");

        System.out.println("\n--- AFTER LOADING FROM FILE ---");
        List<Course> recoveredCourses = CourseFileManager.loadCoursesFromFile("my_courses.csv");

        for (Course c : recoveredCourses) {
            c.printSyllabus();
        }
    }
}