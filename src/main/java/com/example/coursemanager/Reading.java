package com.example.coursemanager;

import java.time.LocalDate;

public class Reading extends CourseItem {
    private String chapter;

    public Reading(String title, LocalDate startDate, LocalDate endDate, String masteryStatus, String chapter) {
        super(title, startDate, endDate, masteryStatus); // Calls the parent constructor
        this.chapter = chapter;
    }

    public String getChapter() { return chapter; }
    public void setChapter(String chapter) { this.chapter = chapter; }

    // Fulfilling the polymorphic requirement from the parent class
    @Override
    public String getDetails() {
        return "Read Chapter: " + chapter;
    }

    @Override
    public String toCsv() {
        return "READING," + title + "," + startDate + "," + endDate + "," + masteryStatus + "," + chapter;
    }
}