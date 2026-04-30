package com.example.coursemanager;

import java.util.ArrayList;

public class Course {
    private String courseName;
    private String professorName;
    private String imagePath;    // NEW: Path to course thumbnail
    private String syllabusPath; // NEW: Path to attached document

    // It can hold Readings, Assignments, Quizzes, AND Exams .
    private ArrayList<CourseItem> syllabus;

    public Course(String courseName, String professorName) {
        this(courseName, professorName, null, null);
    }

    public Course(String courseName, String professorName, String imagePath, String syllabusPath) {
        this.courseName = courseName;
        this.professorName = professorName;
        this.imagePath = imagePath;
        this.syllabusPath = syllabusPath;
        this.syllabus = new ArrayList<>();
    }

    // ... getters ...
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getProfessorName() { return professorName; }
    public void setProfessorName(String professorName) { this.professorName = professorName; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getSyllabusPath() { return syllabusPath; }
    public void setSyllabusPath(String syllabusPath) { this.syllabusPath = syllabusPath; }
    public ArrayList<CourseItem> getSyllabus() { return syllabus; }

    public void addItem(CourseItem item) {
        syllabus.add(item);
    }

    public void printSyllabus() {
        System.out.println("--- Syllabus for " + courseName + " ---");
        for (CourseItem item : syllabus) {
            System.out.println(item.getTitle() + " -> " + item.getDetails());
        }
    }
}