package com.example.coursemanager;

import java.time.LocalDate;

public class Assignment extends CourseItem {
    private double maxPoints;
    private double earnedPoints;

    public Assignment(String title, LocalDate startDate, LocalDate endDate, String masteryStatus, double maxPoints) {
        super(title, startDate, endDate, masteryStatus);
        this.maxPoints = maxPoints;
        this.earnedPoints = 0.0; // Default to 0 until graded
    }
    public double getMaxPoints() { return maxPoints; }
    public void setMaxPoints(double maxPoints) { this.maxPoints = maxPoints; }

    public double getEarnedPoints() { return earnedPoints; }
    public void setEarnedPoints(double earnedPoints) { this.earnedPoints = earnedPoints; }

    // Fulfilling the polymorphic requirement
    @Override
    public String getDetails() {
        return "Graded Assignment: " + earnedPoints + " / " + maxPoints + " pts";
    }

    @Override
    public String toCsv() {
        return "ASSIGNMENT," + title + "," + startDate + "," + endDate + "," + masteryStatus + "," + maxPoints + "," + earnedPoints;
    }
}