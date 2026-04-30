package com.example.coursemanager;

import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;
import java.util.List;

public class HelloController {
    @FXML
    private FlowPane courseContainer;

    @FXML
    public void initialize() {
        // 1. Load the courses from the file
        List<Course> loadedCourses = CourseFileManager.loadCoursesFromFile("my_courses.csv");

        // 2. If it exists, create a card and add it to our UI container
        if (!loadedCourses.isEmpty()) {
            CourseCard card = new CourseCard(loadedCourses.get(0));
            courseContainer.getChildren().add(card);
        }
    }
}
