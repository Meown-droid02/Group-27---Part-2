import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Main App");

        GridPane panel = new GridPane();
        panel.setPadding(new Insets(10, 10, 10, 10));
        panel.setVgap(5);
        panel.setHgap(5);

        Button adminControlButton = new Button("1. Admin Control");
        adminControlButton.setOnAction(e -> launchAdminControl(primaryStage));

        Button adminPortalButton = new Button("2. Admin Portal");
        adminPortalButton.setOnAction(e -> launchAdminPortal(primaryStage));

        Button courseManagementButton = new Button("3. Course Management System");
        courseManagementButton.setOnAction(e -> launchCourseManagementSystem(primaryStage));

        Button loginSystemButton = new Button("4. Login System");
        loginSystemButton.setOnAction(e -> launchLoginSystem(primaryStage));

        Button studentRegistrationButton = new Button("5. Student Registration System");
        studentRegistrationButton.setOnAction(e -> launchStudentRegistrationSystem(primaryStage));

        Button lecturerPortalButton = new Button("6. Lecturer Portal");
        lecturerPortalButton.setOnAction(e -> launchLecturerPortal(primaryStage));
        
        Button exitButton = new Button("7. Exit");
        exitButton.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });

        panel.add(new Label("Choose an option:"), 0, 0);
        panel.add(adminControlButton, 0, 1);
        panel.add(adminPortalButton, 0, 2);
        panel.add(courseManagementButton, 0, 3);
        panel.add(loginSystemButton, 0, 4);
        panel.add(studentRegistrationButton, 0, 5);
        panel.add(lecturerPortalButton, 0, 6);
        panel.add(exitButton, 0, 7);

        Scene scene = new Scene(panel, 400, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void launchAdminControl(Stage primaryStage) {
        AdminControlGUI adminControlGUI = new AdminControlGUI();
        adminControlGUI.start(new Stage());
    }

    
    private void launchAdminPortal(Stage primaryStage) {
        AdminPortalUI adminPortalUI = new AdminPortalUI();
        adminPortalUI.start(new Stage());
    }

    private void launchCourseManagementSystem(Stage primaryStage) {
        CourseManagementSystemUI courseManagementSystemUI = new CourseManagementSystemUI();
        courseManagementSystemUI.start(new Stage());
    }

    private void launchLoginSystem(Stage primaryStage) {
        // Check login before launching Login System
        if (showLoginDialog()) {
            LoginSystemUI loginSystemUI = new LoginSystemUI();
            loginSystemUI.start(new Stage());
        }
    }

    private void launchStudentRegistrationSystem(Stage primaryStage) {
        StudentRegistrationSystemUI studentRegistrationSystemUI = new StudentRegistrationSystemUI();
        studentRegistrationSystemUI.start(new Stage());
    }

    private void launchLecturerPortal(Stage primaryStage) {
        // Check login before launching Lecturer Portal
        if (showLoginDialog()) {
            LecturerPortal lecturerPortal = new LecturerPortal();
            lecturerPortal.start(new Stage());
        }
    }

    private boolean showLoginDialog() {
        LoginSystemUI loginApp = new LoginSystemUI();
        loginApp.start(new Stage());

        // Return true if login is successful
        return loginApp.isUserLoggedIn();
    }
}
