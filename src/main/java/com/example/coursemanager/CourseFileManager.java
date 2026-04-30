package com.example.coursemanager;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseFileManager {

    // --- SAVING DATA TO A FILE ---
    public static void saveCoursesToFile(List<Course> courses, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            for (Course course : courses) {
                // 1. Save the main course info with a special prefix
                String img = (course.getImagePath() == null || course.getImagePath().isEmpty()) ? "NONE" : course.getImagePath();
                String doc = (course.getSyllabusPath() == null || course.getSyllabusPath().isEmpty()) ? "NONE" : course.getSyllabusPath();
                
                writer.println("COURSE," + course.getCourseName() + "," + course.getProfessorName() + "," + img + "," + doc);

                // 2. Loop through the collection and save using polymorphism
                for (CourseItem item : course.getSyllabus()) {
                    writer.println(item.toCsv());
                }
            }
            System.out.println("Courses successfully saved to " + filename);

        } catch (IOException e) {
            System.out.println("CRITICAL ERROR: Could not save the file! " + e.getMessage());
        }
    }

    // --- LOADING DATA FROM A FILE ---
    public static List<Course> loadCoursesFromFile(String filename) {
        List<Course> loadedCourses = new ArrayList<>();
        Course currentCourse = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String[] parts = currentLine.split(",");
                if (parts.length < 2) continue;

                String type = parts[0];

                if (type.equals("COURSE")) {
                    String name = parts[1];
                    String prof = parts[2];
                    String img = parts.length > 3 && !parts[3].equals("NONE") ? parts[3] : null;
                    String doc = parts.length > 4 && !parts[4].equals("NONE") ? parts[4] : null;
                    
                    currentCourse = new Course(name, prof, img, doc);
                    loadedCourses.add(currentCourse);
                } else if (currentCourse != null) {
                    String title = parts[1];
                    LocalDate start = LocalDate.parse(parts[2]);
                    LocalDate end = LocalDate.parse(parts[3]);
                    String status = parts[4];

                    switch (type) {
                        case "READING":
                            currentCourse.addItem(new Reading(title, start, end, status, parts[5]));
                            break;
                        case "ASSIGNMENT":
                            Assignment a = new Assignment(title, start, end, status, Double.parseDouble(parts[5]));
                            a.setEarnedPoints(Double.parseDouble(parts[6]));
                            currentCourse.addItem(a);
                            break;
                        case "QUIZ":
                            Quiz q = new Quiz(title, start, end, status, Integer.parseInt(parts[5]));
                            q.setScore(Double.parseDouble(parts[6]));
                            currentCourse.addItem(q);
                            break;
                        case "EXAM":
                            currentCourse.addItem(new Exam(title, start, end, status, parts[5], Double.parseDouble(parts[6])));
                            break;
                    }
                } else if (loadedCourses.isEmpty() && !type.equals("COURSE")) {
                    // Legacy support: if the first line isn't "COURSE", it might be an old-style file
                    // But for simplicity in this refactor, we'll assume the new format.
                    // If you want legacy support, we could add it here.
                }
            }
            System.out.println("Courses successfully loaded!");

        } catch (FileNotFoundException e) {
            System.out.println("File not found. Creating a new empty database.");
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
        }

        return loadedCourses;
    }
}