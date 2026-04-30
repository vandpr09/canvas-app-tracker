module com.example.coursemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.coursemanager to javafx.fxml;
    exports com.example.coursemanager;
}