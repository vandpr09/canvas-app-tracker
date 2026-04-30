package com.example.coursemanager;

import java.time.LocalDate;

public class Exam extends CourseItem {
    private String location; // e.g., "Room 101" or "Canvas"
    private double weightPercentage; // e.g., 25.0 for 25% of final grade

    public Exam(String title, LocalDate startDate, LocalDate endDate, String masteryStatus, String location, double weightPercentage) {
        super(title, startDate, endDate, masteryStatus);
        this.location = location;
        this.weightPercentage = weightPercentage;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getWeightPercentage() { return weightPercentage; }
    public void setWeightPercentage(double weightPercentage) { this.weightPercentage = weightPercentage; }

    @Override
    public String getDetails() {
        return "EXAM: " + location + " | Weight: " + weightPercentage + "% of total grade";
    }

    @Override
    public String toCsv() {
        return "EXAM," + title + "," + startDate + "," + endDate + "," + masteryStatus + "," + location + "," + weightPercentage;
    }
}