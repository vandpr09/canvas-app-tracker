package com.example.coursemanager;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;

public class CourseCard extends StackPane {

    private Course course; 
    private Button miniEditBtn;

    public CourseCard(Course course) {
        this.course = course;

        VBox mainLayout = new VBox(0);
        mainLayout.getStyleClass().add("course-card");

        // 1. Content Area
        VBox contentArea = new VBox(5);
        contentArea.setPadding(new Insets(15));

        Label titleLabel = new Label(course.getCourseName());
        titleLabel.getStyleClass().add("card-title");
        
        Label profLabel = new Label(course.getProfessorName());
        profLabel.getStyleClass().add("card-subtitle");

        long completed = course.getSyllabus().stream()
                .filter(item -> item.getMasteryStatus().equalsIgnoreCase("ranger"))
                .count();
        int total = course.getSyllabus().size();
        double progress = total > 0 ? (double) completed / total : 0;

        Label progressLabel = new Label(completed + " / " + total + " items completed");
        progressLabel.getStyleClass().add("progress-label");

        ProgressBar progressBar = new ProgressBar(progress);
        progressBar.setPrefWidth(160);
        progressBar.setStyle("-fx-accent: #27AE60;");

        contentArea.getChildren().addAll(titleLabel, profLabel, progressLabel, progressBar);

        // 2. Image Header
        if (course.getImagePath() != null) {
            try {
                File file = new File(course.getImagePath());
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString(), 220, 120, true, true);
                    ImageView imageView = new ImageView(image);
                    
                    StackPane imageContainer = new StackPane(imageView);
                    imageContainer.getStyleClass().add("course-card-image");
                    imageContainer.setClip(createRoundedClip(220, 120, 12));
                    
                    mainLayout.getChildren().add(imageContainer);
                }
            } catch (Exception e) {
                System.out.println("Error loading image: " + e.getMessage());
            }
        }

        mainLayout.getChildren().add(contentArea);
        
        // 3. Mini Edit Button
        miniEditBtn = new Button("✎");
        miniEditBtn.getStyleClass().add("mini-edit-button");
        StackPane.setAlignment(miniEditBtn, Pos.TOP_RIGHT);
        StackPane.setMargin(miniEditBtn, new Insets(10));

        this.getChildren().addAll(mainLayout, miniEditBtn);
    }

    private javafx.scene.shape.Rectangle createRoundedClip(double width, double height, double radius) {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(width, height);
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);
        return clip;
    }

    public Button getMiniEditBtn() {
        return miniEditBtn;
    }

    public Course getCourse() {
        return course;
    }
}
