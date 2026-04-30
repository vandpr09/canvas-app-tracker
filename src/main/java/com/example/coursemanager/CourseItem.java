package com.example.coursemanager;

import java.time.LocalDate;

public abstract class CourseItem {
    protected String title;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected String masteryStatus; // e.g., "ranger", "none", "in progress"

    // Constructor
    public CourseItem(String title, LocalDate startDate, LocalDate endDate, String masteryStatus) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.masteryStatus = masteryStatus;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getMasteryStatus() { return masteryStatus; }
    public void setMasteryStatus(String masteryStatus) { this.masteryStatus = masteryStatus; }

    // --- NEW: For the Checkbox Deletion feature ---
    protected boolean markedForDeletion = false;

    public boolean isMarkedForDeletion() { return markedForDeletion; }
    public void setMarkedForDeletion(boolean markedForDeletion) { this.markedForDeletion = markedForDeletion; }

    // This is where Polymorphism happens
    public abstract String getDetails();

    // NEW: For Polymorphic Saving
    public abstract String toCsv();
}