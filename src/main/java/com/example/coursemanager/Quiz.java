package com.example.coursemanager;

import java.time.LocalDate;

public class Quiz extends CourseItem {
    private int numberOfQuestions;
    private double score;

    public Quiz(String title, LocalDate startDate, LocalDate endDate, String masteryStatus, int numberOfQuestions) {
        super(title, startDate, endDate, masteryStatus);
        this.numberOfQuestions = numberOfQuestions;
        this.score = 0.0; // Default until graded
    }

    public int getNumberOfQuestions() { return numberOfQuestions; }
    public void setNumberOfQuestions(int numberOfQuestions) { this.numberOfQuestions = numberOfQuestions; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    @Override
    public String getDetails() {
        return "Quiz (" + numberOfQuestions + " questions) | Current Score: " + score;
    }

    @Override
    public String toCsv() {
        return "QUIZ," + title + "," + startDate + "," + endDate + "," + masteryStatus + "," + numberOfQuestions + "," + score;
    }
}