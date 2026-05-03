package com.example.coursemanager;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.File;
import java.awt.Desktop;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

public class HelloApplication extends Application {

    private Stage window;
    private Scene dashboardScene;
    private FlowPane cardGrid;
    private List<Course> allCourses = new ArrayList<>();
    private boolean isLightMode = false;

    @Override
    public void start(Stage stage) {
        this.window = stage;

        // 1. Load courses from file
        allCourses = CourseFileManager.loadCoursesFromFile("my_courses.csv");

        VBox root = new VBox(20);
        root.setPadding(new Insets(40));

        HBox headerRow = new HBox(20);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label headerLabel = new Label("Canvas Tracker");
        headerLabel.getStyleClass().add("header-text");

        Button addButton = new Button("+ Add Course");
        addButton.getStyleClass().add("action-button");
        addButton.setOnAction(e -> showAddCourseDialog());

        Button tablesButton = new Button("📊 Tables View");
        tablesButton.getStyleClass().add("action-button");
        tablesButton.setOnAction(e -> showMasterTablesView());

        Button calendarButton = new Button("📅 Calendar View");
        calendarButton.getStyleClass().add("action-button");
        calendarButton.setOnAction(e -> showCalendarView());

        Button themeToggle = new Button("🌓 Toggle Theme");
        themeToggle.getStyleClass().add("theme-toggle");
        themeToggle.setOnAction(e -> {
            isLightMode = !isLightMode;
            updateTheme(root);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        headerRow.getChildren().addAll(headerLabel);

        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(Pos.CENTER_LEFT);
        buttonRow.getChildren().addAll(addButton, tablesButton, calendarButton, spacer, themeToggle);

        cardGrid = new FlowPane();
        cardGrid.setHgap(20);
        cardGrid.setVgap(20);

        root.getChildren().addAll(headerRow, buttonRow, cardGrid);

        dashboardScene = new Scene(root, 900, 600);
        dashboardScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        updateTheme(root);

        // 2. Populate the grid with loaded courses
        refreshDashboard();

        window.setTitle("Canvas Tracker");
        window.setScene(dashboardScene);
        window.show();
    }

    private void updateTheme(Parent root) {
        if (isLightMode) {
            if (!root.getStyleClass().contains("light-mode")) {
                root.getStyleClass().add("light-mode");
            }
        } else {
            root.getStyleClass().removeAll("light-mode");
        }
    }

    private void saveAll() {
        CourseFileManager.saveCoursesToFile(allCourses, "my_courses.csv");
    }

    private void addCardToGrid(Course course) {
        CourseCard card = new CourseCard(course);
        // Use card's inner layout for the main click
        card.getChildren().get(0).setOnMouseClicked(e -> openCourseDetail(course));

        // Setup the mini edit button
        card.getMiniEditBtn().setOnAction(e -> {
            showEditCourseDialog(course, null);
        });

        cardGrid.getChildren().add(card);
    }

    private void refreshDashboard() {
        if (cardGrid == null) return;
        cardGrid.getChildren().clear();
        for (Course course : allCourses) {
            addCardToGrid(course);
        }
    }

    // --- UPDATED: COURSE DETAIL VIEW WITH DELETE BUTTON ---
    private void openCourseDetail(Course course) {
        VBox layout = new VBox(0);
        layout.getStyleClass().add("root");
        if (isLightMode) layout.getStyleClass().add("light-mode");

        // 1. Full-Width Banner
        if (course.getImagePath() != null) {
            try {
                File imgFile = new File(course.getImagePath());
                if (imgFile.exists()) {
                    ImageView iv = new ImageView(new Image(imgFile.toURI().toString()));
                    iv.setPreserveRatio(true);

                    StackPane bannerContainer = new StackPane(iv);
                    bannerContainer.setPrefHeight(250);
                    bannerContainer.setMinHeight(250);
                    bannerContainer.setMaxHeight(250);
                    bannerContainer.getStyleClass().add("course-header-container");

                    // NEW: Dynamic scaling logic
                    iv.fitWidthProperty().bind(window.widthProperty());

                    StackPane.setAlignment(iv, Pos.CENTER);

                    // Dynamic clip that follows window width
                    javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
                    clip.setWidth(900); // Initial
                    clip.setHeight(250);
                    clip.widthProperty().bind(window.widthProperty());
                    bannerContainer.setClip(clip);

                    layout.getChildren().add(bannerContainer);
                }
            } catch (Exception ex) {
                System.out.println("Error loading detail banner");
            }
        }

        // 2. Content Area (with padding)
        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 40, 40, 40));

        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.getStyleClass().add("action-button");
        backBtn.setOnAction(e -> {
            updateTheme(dashboardScene.getRoot());
            window.setScene(dashboardScene);
            refreshDashboard();
        });

        Button deleteItemBtn = new Button("- Delete Selected");
        deleteItemBtn.getStyleClass().add("danger-button");

        Button addItemBtn = new Button("+ Add Item");
        addItemBtn.getStyleClass().add("action-button");

        Button editItemBtn = new Button("✎ Edit Selected");
        editItemBtn.getStyleClass().add("action-button");

        Button editCourseBtn = new Button("✎ Edit Course");
        editCourseBtn.getStyleClass().add("action-button");
        editCourseBtn.setOnAction(e -> showEditCourseDialog(course, layout));

        Button themeToggle = new Button("🌓 Toggle Theme");
        themeToggle.getStyleClass().add("theme-toggle");
        themeToggle.setOnAction(e -> {
            isLightMode = !isLightMode;
            updateTheme(layout);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topRow.getChildren().addAll(backBtn, addItemBtn, editItemBtn, editCourseBtn, deleteItemBtn, spacer, themeToggle);

        Label titleLabel = new Label(course.getCourseName());
        titleLabel.getStyleClass().add("header-text");

        content.getChildren().addAll(topRow, titleLabel);

        // Syllabus Link
        if (course.getSyllabusPath() != null) {
            Hyperlink syllabusLink = new Hyperlink("📄 View Syllabus (" + new File(course.getSyllabusPath()).getName() + ")");
            syllabusLink.getStyleClass().add("syllabus-link");
            syllabusLink.setOnAction(e -> {
                try {
                    Desktop.getDesktop().open(new File(course.getSyllabusPath()));
                } catch (Exception ex) {
                }
            });
            content.getChildren().add(syllabusLink);
        }

        TabPane tabPane = new TabPane();

        TableView<CourseItem> readingsTable = createItemTable();
        TableView<CourseItem> assignmentsTable = createItemTable();
        TableView<CourseItem> quizzesTable = createItemTable();
        TableView<CourseItem> examsTable = createItemTable();

        Tab readTab = new Tab("Readings", readingsTable);
        Tab assnTab = new Tab("Assignments", assignmentsTable);
        Tab quizTab = new Tab("Quizzes", quizzesTable);
        Tab examTab = new Tab("Exams", examsTable);

        readTab.setClosable(false);
        assnTab.setClosable(false);
        quizTab.setClosable(false);
        examTab.setClosable(false);

        tabPane.getTabs().addAll(readTab, assnTab, quizTab, examTab);

        editItemBtn.setOnAction(e -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            TableView<CourseItem> activeTable = (TableView<CourseItem>) selectedTab.getContent();
            CourseItem selected = activeTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditItemDialog(selected, activeTable);
            }
        });

        for (CourseItem item : course.getSyllabus()) {
            if (item instanceof Reading) readingsTable.getItems().add(item);
            else if (item instanceof Assignment) assignmentsTable.getItems().add(item);
            else if (item instanceof Quiz) quizzesTable.getItems().add(item);
            else if (item instanceof Exam) examsTable.getItems().add(item);
        }

        addItemBtn.setOnAction(e -> showAddItemDialog(course, readingsTable, assignmentsTable, quizzesTable, examsTable));

        // NEW: Delete Logic!
        // NEW: Multi-Delete Checkbox Logic!
        deleteItemBtn.setOnAction(e -> {
            Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();
            TableView<CourseItem> activeTable = (TableView<CourseItem>) selectedTab.getContent();

            // 1. Create a list to hold everything we want to delete
            List<CourseItem> itemsToRemove = new ArrayList<>();

            // 2. Loop through the table and find the checked ones
            for (CourseItem item : activeTable.getItems()) {
                if (item.isMarkedForDeletion()) {
                    itemsToRemove.add(item);
                }
            }

            // 3. Remove them from both the frontend table and the backend data!
            activeTable.getItems().removeAll(itemsToRemove);
            course.getSyllabus().removeAll(itemsToRemove);
            saveAll();
        });

        content.getChildren().add(tabPane);
        layout.getChildren().add(content);

        // Wrap the whole thing in a ScrollPane so we can see everything
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("root"); // Pass the root class for background
        if (isLightMode) scrollPane.getStyleClass().add("light-mode");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");

        Scene detailScene = new Scene(scrollPane, 900, 600);
        detailScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        updateTheme(layout);
        window.setScene(detailScene);
    }

    // --- UPDATED: TABLE NOW SHOWS DUE DATES ---
    // --- UPDATED: NO UGLY BOX AND ADDED CHECKBOXES ---
    private TableView<CourseItem> createItemTable() {
        TableView<CourseItem> table = new TableView<>();
        table.setEditable(true); // Required for checkboxes to be clickable!

        // This line forces the columns to stretch and DESTROYS the ugly blank box
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        // NEW: Checkbox Column
        TableColumn<CourseItem, Boolean> checkCol = new TableColumn<>("☑");
        checkCol.setCellValueFactory(cellData -> {
            CourseItem item = cellData.getValue();
            SimpleBooleanProperty property = new SimpleBooleanProperty(item.isMarkedForDeletion());

            // Listen for when the user clicks the checkbox and update the backend object
            property.addListener((obs, oldVal, newVal) -> {
                item.setMarkedForDeletion(newVal);
                saveAll();
            });
            return property;
        });
        checkCol.setCellFactory(CheckBoxTableCell.forTableColumn(checkCol));
        checkCol.setMaxWidth(40); // Keep it small

        TableColumn<CourseItem, String> titleCol = new TableColumn<>("Item Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(200);
        titleCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: text-main;");

        TableColumn<CourseItem, LocalDate> dateCol = new TableColumn<>("Due Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        dateCol.setPrefWidth(120);
        dateCol.setStyle("-fx-alignment: CENTER; -fx-text-fill: text-main;");

        TableColumn<CourseItem, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("masteryStatus"));
        statusCol.setPrefWidth(100);
        statusCol.setStyle("-fx-alignment: CENTER;");

        // Apply custom cell factory for coloring
        statusCol.setCellFactory(column -> new TableCell<CourseItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    getStyleClass().removeAll("status-ranger", "status-in-progress", "status-none");
                } else {
                    setText(item);
                    getStyleClass().removeAll("status-ranger", "status-in-progress", "status-none");
                    if (item.equalsIgnoreCase("ranger")) getStyleClass().add("status-ranger");
                    else if (item.equalsIgnoreCase("in progress")) getStyleClass().add("status-in-progress");
                    else if (item.equalsIgnoreCase("none")) getStyleClass().add("status-none");
                }
            }
        });

        TableColumn<CourseItem, String> detailsCol = new TableColumn<>("Specifics");
        detailsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDetails()));
        detailsCol.setPrefWidth(350);
        detailsCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: text-main;");

        table.getColumns().addAll(checkCol, titleCol, dateCol, statusCol, detailsCol);

        // NEW: Double-click to Edit!
        table.setRowFactory(tv -> {
            TableRow<CourseItem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CourseItem rowData = row.getItem();
                    showEditItemDialog(rowData, table);
                }
            });
            return row;
        });

        return table;
    }

    private void showEditItemDialog(CourseItem item, TableView<CourseItem> table) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        TextField titleField = new TextField(item.getTitle());
        DatePicker datePicker = new DatePicker(item.getEndDate());

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("none", "in progress", "ranger");
        statusBox.setValue(item.getMasteryStatus());

        TextField specificField = new TextField();
        // Determine what goes in the "Specifics" box based on type
        if (item instanceof Reading) specificField.setText(((Reading) item).getChapter());
        else if (item instanceof Assignment) specificField.setText(String.valueOf(((Assignment) item).getMaxPoints()));
        else if (item instanceof Quiz) specificField.setText(String.valueOf(((Quiz) item).getNumberOfQuestions()));
        else if (item instanceof Exam) specificField.setText(((Exam) item).getLocation());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Due Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Status:"), 0, 2);
        grid.add(statusBox, 1, 2);
        grid.add(new Label("Specifics:"), 0, 3);
        grid.add(specificField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                item.setTitle(titleField.getText());
                item.setEndDate(datePicker.getValue());
                item.setMasteryStatus(statusBox.getValue());

                String spec = specificField.getText();
                try {
                    if (item instanceof Reading) ((Reading) item).setChapter(spec);
                    else if (item instanceof Assignment) ((Assignment) item).setMaxPoints(Double.parseDouble(spec));
                    else if (item instanceof Quiz) ((Quiz) item).setNumberOfQuestions(Integer.parseInt(spec));
                    else if (item instanceof Exam) ((Exam) item).setLocation(spec);
                } catch (Exception ex) {
                    System.out.println("Error updating specifics!");
                }
                return true;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(saved -> {
            table.refresh(); // Tell JavaFX the data changed
            saveAll();       // Save to CSV
        });
    }

    // --- UPDATED: ADD ITEM DIALOG NOW HAS A CALENDAR ---
    private void showAddItemDialog(Course course, TableView<CourseItem> rTable, TableView<CourseItem> aTable, TableView<CourseItem> qTable, TableView<CourseItem> eTable) {
        Dialog<CourseItem> dialog = new Dialog<>();
        dialog.setTitle("New Course Item");
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType addType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Reading", "Assignment", "Quiz", "Exam");
        typeBox.setValue("Reading");

        TextField titleField = new TextField();
        titleField.setPromptText("e.g. Chapter 4");

        TextField specificField = new TextField();
        specificField.setPromptText("Chapter / Points / Questions");

        // NEW: Calendar Date Picker! Default it to today.
        DatePicker datePicker = new DatePicker(LocalDate.now());

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Due Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Specifics:"), 0, 3);
        grid.add(specificField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == addType) {
                String type = typeBox.getValue();
                String title = titleField.getText();
                String spec = specificField.getText();

                // Extract the date the user picked from the calendar
                LocalDate chosenDate = datePicker.getValue();

                try {
                    // Use chosenDate instead of LocalDate.now()
                    if (type.equals("Reading")) return new Reading(title, chosenDate, chosenDate, "none", spec);
                    if (type.equals("Assignment"))
                        return new Assignment(title, chosenDate, chosenDate, "none", Double.parseDouble(spec));
                    if (type.equals("Quiz"))
                        return new Quiz(title, chosenDate, chosenDate, "none", Integer.parseInt(spec));
                    if (type.equals("Exam")) return new Exam(title, chosenDate, chosenDate, "none", spec, 25.0);
                } catch (Exception ex) {
                    System.out.println("Invalid number entered!");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newItem -> {
            course.addItem(newItem);
            saveAll();

            if (newItem instanceof Reading) rTable.getItems().add(newItem);
            else if (newItem instanceof Assignment) aTable.getItems().add(newItem);
            else if (newItem instanceof Quiz) qTable.getItems().add(newItem);
            else if (newItem instanceof Exam) eTable.getItems().add(newItem);
        });
    }

    private void showAddCourseDialog() {
        Dialog<Course> dialog = new Dialog<>();
        dialog.setTitle("New Course");
        dialog.getDialogPane().getStyleClass().add("dialog-pane");
        ButtonType addType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField profField = new TextField();

        // Image Selection
        TextField imageField = new TextField();
        imageField.setEditable(false);
        Button imgBtn = new Button("Browse...");
        imgBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fc.showOpenDialog(window);
            if (file != null) imageField.setText(file.getAbsolutePath());
        });

        // Syllabus Selection
        TextField syllabusField = new TextField();
        syllabusField.setEditable(false);
        Button sylBtn = new Button("Browse...");
        sylBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx", "*.doc", "*.txt"));
            File file = fc.showOpenDialog(window);
            if (file != null) syllabusField.setText(file.getAbsolutePath());
        });

        grid.add(new Label("Course Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Professor:"), 0, 1);
        grid.add(profField, 1, 1);
        grid.add(new Label("Course Image:"), 0, 2);
        grid.add(imageField, 1, 2);
        grid.add(imgBtn, 2, 2);
        grid.add(new Label("Syllabus (PDF/DOCX):"), 0, 3);
        grid.add(syllabusField, 1, 3);
        grid.add(sylBtn, 2, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addType) {
                String imgPath = imageField.getText().isEmpty() ? null : imageField.getText();
                String sylPath = syllabusField.getText().isEmpty() ? null : syllabusField.getText();
                return new Course(nameField.getText(), profField.getText(), imgPath, sylPath);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newCourse -> {
            allCourses.add(newCourse);
            addCardToGrid(newCourse);
            saveAll();
        });
    }

    private void showCalendarView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.getStyleClass().add("root");
        if (isLightMode) layout.getStyleClass().add("light-mode");

        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Back to Dashboard");
        backBtn.getStyleClass().add("action-button");
        backBtn.setOnAction(e -> {
            updateTheme(dashboardScene.getRoot());
            window.setScene(dashboardScene);
            refreshDashboard();
        });
        Label titleLabel = new Label("Academic Calendar");
        titleLabel.getStyleClass().add("header-text");
        topRow.getChildren().addAll(backBtn, titleLabel);

        layout.getChildren().add(topRow);

        // State for calendar
        final java.util.concurrent.atomic.AtomicReference<LocalDate> currentMonth = new java.util.concurrent.atomic.AtomicReference<>(LocalDate.now().withDayOfMonth(1));

        // Filters
        VBox filterSidebar = new VBox(15);
        filterSidebar.setMinWidth(200);
        filterSidebar.setPadding(new Insets(10));
        filterSidebar.setStyle("-fx-background-color: card-bg; -fx-background-radius: 10;");

        Label filterHeader = new Label("Filters");
        filterHeader.getStyleClass().add("sidebar-header");

        VBox courseFilters = new VBox(5);
        List<CheckBox> courseCBs = new ArrayList<>();
        for (Course c : allCourses) {
            CheckBox cb = new CheckBox(c.getCourseName());
            cb.setSelected(true);
            cb.getStyleClass().add("check-box");
            courseCBs.add(cb);
            courseFilters.getChildren().add(cb);
        }

        VBox typeFilters = new VBox(5);
        CheckBox readCB = new CheckBox("Readings");
        readCB.setSelected(true);
        CheckBox assnCB = new CheckBox("Assignments");
        assnCB.setSelected(true);
        CheckBox quizCB = new CheckBox("Quizzes");
        quizCB.setSelected(true);
        CheckBox examCB = new CheckBox("Exams");
        examCB.setSelected(true);
        typeFilters.getChildren().addAll(readCB, assnCB, quizCB, examCB);

        Label courseLabel = new Label("Courses:");
        courseLabel.getStyleClass().add("sidebar-header");
        Label typeLabel = new Label("Types:");
        typeLabel.getStyleClass().add("sidebar-header");

        filterSidebar.getChildren().addAll(filterHeader, courseLabel, courseFilters, typeLabel, typeFilters);

        // Calendar Grid Area
        VBox calendarArea = new VBox(10);
        HBox navRow = new HBox(15);
        navRow.setAlignment(Pos.CENTER);
        Button prevBtn = new Button("◀");
        prevBtn.getStyleClass().add("action-button");
        Button nextBtn = new Button("▶");
        nextBtn.getStyleClass().add("action-button");
        Label monthLabel = new Label();
        monthLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: text-main;");
        navRow.getChildren().addAll(prevBtn, monthLabel, nextBtn);

        GridPane calendarGrid = new GridPane();
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setAlignment(Pos.CENTER);

        Runnable refreshCalendar = () -> {
            calendarGrid.getChildren().clear();
            LocalDate month = currentMonth.get();
            monthLabel.setText(month.getMonth().toString() + " " + month.getYear());

            // Days of week headers
            String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            for (int i = 0; i < 7; i++) {
                Label dayHeader = new Label(days[i]);
                dayHeader.setPrefWidth(100);
                dayHeader.setAlignment(Pos.CENTER);
                dayHeader.setStyle("-fx-font-weight: bold; -fx-text-fill: text-muted;");
                calendarGrid.add(dayHeader, i, 0);
            }

            int dayOfWeek = month.getDayOfWeek().getValue() % 7;
            int daysInMonth = month.lengthOfMonth();

            for (int d = 1; d <= daysInMonth; d++) {
                int row = (d + dayOfWeek - 1) / 7 + 1;
                int col = (d + dayOfWeek - 1) % 7;

                VBox dayBox = new VBox(2);
                dayBox.setPrefSize(100, 80);
                dayBox.setStyle("-fx-border-color: border-color; -fx-background-color: card-bg; -fx-padding: 5;");
                Label dayNum = new Label(String.valueOf(d));
                dayNum.getStyleClass().add("label"); // Uses text-main
                dayBox.getChildren().add(dayNum);

                LocalDate date = month.withDayOfMonth(d);
                for (int i = 0; i < allCourses.size(); i++) {
                    if (courseCBs.get(i).isSelected()) {
                        Course c = allCourses.get(i);
                        for (CourseItem item : c.getSyllabus()) {
                            if (item.getEndDate().equals(date)) {
                                boolean show = false;
                                if (item instanceof Reading && readCB.isSelected()) show = true;
                                if (item instanceof Assignment && assnCB.isSelected()) show = true;
                                if (item instanceof Quiz && quizCB.isSelected()) show = true;
                                if (item instanceof Exam && examCB.isSelected()) show = true;

                                if (show) {
                                    Label itemLbl = new Label("• " + item.getTitle());
                                    itemLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: primary;");
                                    dayBox.getChildren().add(itemLbl);
                                }
                            }
                        }
                    }
                }
                calendarGrid.add(dayBox, col, row);
            }
        };

        prevBtn.setOnAction(e -> {
            currentMonth.set(currentMonth.get().minusMonths(1));
            refreshCalendar.run();
        });
        nextBtn.setOnAction(e -> {
            currentMonth.set(currentMonth.get().plusMonths(1));
            refreshCalendar.run();
        });

        // Wire up filters
        readCB.setOnAction(e -> refreshCalendar.run());
        assnCB.setOnAction(e -> refreshCalendar.run());
        quizCB.setOnAction(e -> refreshCalendar.run());
        examCB.setOnAction(e -> refreshCalendar.run());
        for (CheckBox cb : courseCBs) cb.setOnAction(e -> refreshCalendar.run());

        refreshCalendar.run();
        calendarArea.getChildren().addAll(navRow, calendarGrid);

        HBox mainContent = new HBox(20);
        mainContent.getChildren().addAll(filterSidebar, calendarArea);
        layout.getChildren().add(mainContent);

        Scene scene = new Scene(layout, 1000, 750);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        window.setScene(scene);
    }

    private void showMasterTablesView() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.getStyleClass().add("root");
        if (isLightMode) layout.getStyleClass().add("light-mode");

        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Button backBtn = new Button("← Back to Dashboard");
        backBtn.getStyleClass().add("action-button");
        backBtn.setOnAction(e -> {
            updateTheme(dashboardScene.getRoot());
            window.setScene(dashboardScene);
            refreshDashboard();
        });

        Label titleLabel = new Label("Master Item Tables");
        titleLabel.getStyleClass().add("header-text");

        // Filter Section
        VBox filterBox = new VBox(10);
        Label filterLabel = new Label("Filter by Course:");
        filterLabel.getStyleClass().add("sidebar-header");

        FlowPane filterPane = new FlowPane(10, 10);
        List<CheckBox> courseFilters = new ArrayList<>();

        for (Course course : allCourses) {
            CheckBox cb = new CheckBox(course.getCourseName());
            cb.setSelected(true);
            cb.getStyleClass().add("tab-label"); // reuse text color
            courseFilters.add(cb);
            filterPane.getChildren().add(cb);
        }
        filterBox.getChildren().addAll(filterLabel, filterPane);

        TabPane tabPane = new TabPane();
        TableView<CourseItem> readingsTable = createItemTable();
        TableView<CourseItem> assignmentsTable = createItemTable();
        TableView<CourseItem> quizzesTable = createItemTable();
        TableView<CourseItem> examsTable = createItemTable();

        tabPane.getTabs().addAll(
                new Tab("All Readings", readingsTable),
                new Tab("All Assignments", assignmentsTable),
                new Tab("All Quizzes", quizzesTable),
                new Tab("All Exams", examsTable)
        );
        for (Tab t : tabPane.getTabs()) t.setClosable(false);

        // Update tables function
        Runnable updateTables = () -> {
            readingsTable.getItems().clear();
            assignmentsTable.getItems().clear();
            quizzesTable.getItems().clear();
            examsTable.getItems().clear();

            for (int i = 0; i < allCourses.size(); i++) {
                if (courseFilters.get(i).isSelected()) {
                    Course c = allCourses.get(i);
                    for (CourseItem item : c.getSyllabus()) {
                        if (item instanceof Reading) readingsTable.getItems().add(item);
                        else if (item instanceof Assignment) assignmentsTable.getItems().add(item);
                        else if (item instanceof Quiz) quizzesTable.getItems().add(item);
                        else if (item instanceof Exam) examsTable.getItems().add(item);
                    }
                }
            }
        };

        // Trigger update on filter change
        for (CheckBox cb : courseFilters) {
            cb.setOnAction(e -> updateTables.run());
        }
        updateTables.run();

        layout.getChildren().addAll(backBtn, titleLabel, filterBox, tabPane);

        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("root");
        if (isLightMode) scrollPane.getStyleClass().add("light-mode");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Scene scene = new Scene(scrollPane, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        window.setScene(scene);
    }

    private void showEditCourseDialog(Course course, VBox currentLayout) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Edit Course Details");
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        ButtonType saveType = new ButtonType("Save Changes", ButtonBar.ButtonData.OK_DONE);
        ButtonType deleteType = new ButtonType("Delete Course", ButtonBar.ButtonData.OTHER);

        dialog.getDialogPane().getButtonTypes().addAll(saveType, deleteType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 50, 10, 10));

        TextField nameField = new TextField(course.getCourseName());
        TextField profField = new TextField(course.getProfessorName());

        TextField imageField = new TextField(course.getImagePath() == null ? "" : course.getImagePath());
        imageField.setEditable(false);

        Button imgBtn = new Button("Browse...");
        imgBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = fc.showOpenDialog(window);
            if (file != null) imageField.setText(file.getAbsolutePath());
        });

        TextField syllabusField = new TextField(course.getSyllabusPath() == null ? "" : course.getSyllabusPath());
        syllabusField.setEditable(false);

        Button sylBtn = new Button("Browse...");
        sylBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx", "*.doc", "*.txt"));
            File file = fc.showOpenDialog(window);
            if (file != null) syllabusField.setText(file.getAbsolutePath());
        });

        grid.add(new Label("Course Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Professor:"), 0, 1);
        grid.add(profField, 1, 1);

        grid.add(new Label("Course Image:"), 0, 2);
        grid.add(imageField, 1, 2);
        grid.add(imgBtn, 2, 2);

        grid.add(new Label("Syllabus:"), 0, 3);
        grid.add(syllabusField, 1, 3);
        grid.add(sylBtn, 2, 3);

        dialog.getDialogPane().setContent(grid);

        // Style and handle Delete Course button
        javafx.scene.Node deleteButton = dialog.getDialogPane().lookupButton(deleteType);
        deleteButton.getStyleClass().add("danger-button");

        deleteButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold;");

        deleteButton.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Delete Course");
            confirm.setHeaderText("Delete this course?");
            confirm.setContentText("This will delete the course and all items inside it.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    allCourses.remove(course);
                    saveAll();
                    refreshDashboard();
                    dialog.close();
                    window.setScene(dashboardScene);
                }
            });

            e.consume();
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                course.setCourseName(nameField.getText());
                course.setProfessorName(profField.getText());
                course.setImagePath(imageField.getText().isEmpty() ? null : imageField.getText());
                course.setSyllabusPath(syllabusField.getText().isEmpty() ? null : syllabusField.getText());
                return true;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(saved -> {
            if (saved) {
                saveAll();
                openCourseDetail(course);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
