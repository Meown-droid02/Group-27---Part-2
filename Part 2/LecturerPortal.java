import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The `LecturerPortal` class is a JavaFX application representing a login portal for lecturers.
 * Lecturers can log in, and upon successful login, they gain access to a portal to execute commands.
 * The application reads lecturer credentials from a CSV file and allows lecturers to view students
 * associated with their courses.
 */
public class LecturerPortal extends Application {

    // Map to store lecturer credentials (username -> password)
    private Map<String, String> lecturerCredentials;
    // Path to the CSV file containing course information
    private String coursesFilePath = "courses.csv";

    /**
     * The entry point of the JavaFX application.
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the JavaFX application by reading lecturer credentials from the database CSV file.
     * @param primaryStage The primary stage for the application window.
     */
    @Override
    public void start(Stage primaryStage) {
        // Read lecturer credentials from the CSV file
        readLecturerCredentials();

        primaryStage.setTitle("Lecturer Portal Login");

        // Set up the UI components in a VBox
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(20));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");

        vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Password:"), passwordField,
                loginButton);

        // Set up event handler for the login button
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (validateLogin(username, password)) {
                showLecturerPortal(username);
            } else {
                showAlert("Invalid Credentials", "Please check your username and password.");
            }
        });

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Displays the lecturer portal after a successful login.
     * @param username The username of the logged-in lecturer.
     */
    private void showLecturerPortal(String username) {
        // Set up a new stage for the lecturer portal
        Stage lecturerStage = new Stage();
        lecturerStage.setTitle("Lecturer Portal - Welcome " + username);

        // UI components for the lecturer portal
        TextArea commandOutput = new TextArea();
        TextField commandInput = new TextField();
        Button executeButton = new Button("Execute");

        VBox lecturerVBox = new VBox(10);
        lecturerVBox.setPadding(new Insets(20));
        lecturerVBox.getChildren().addAll(commandInput, executeButton, commandOutput);

        // Static note before executing any command
        commandOutput.setText("Note: Use '/view' command to display students.\n");

        // Set up event handler for the execute button
        executeButton.setOnAction(event -> {
            String command = commandInput.getText().trim();
            if (command.equalsIgnoreCase("/view")) {
                String students = getStudentsForLecturer(username);
                commandOutput.setText(students);
            } else {
                commandOutput.setText("Invalid command. Try '/view' to view students.");
            }
        });

        Scene lecturerScene = new Scene(lecturerVBox, 400, 300);
        lecturerStage.setScene(lecturerScene);
        lecturerStage.show();
    }

    /**
     * Retrieves a list of students associated with the given lecturer.
     * @param lecturerName The name of the lecturer.
     * @return A formatted string containing the list of students or an appropriate message.
     */
    private String getStudentsForLecturer(String lecturerName) {
        // StringBuilder to construct the result
        StringBuilder result = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(coursesFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[4].equalsIgnoreCase(lecturerName)) {
                    String studentName = parts[3];
                    result.append(studentName).append(";");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if any students were found
        if (result.length() == 0) {
            return "No students found for lecturer " + lecturerName;
        } else {
            // Format the result string
            String students = result.substring(0, result.length() - 1); // Remove the trailing ';'
            students = students.replace(';', ',');
            return "List of students for lecturer " + lecturerName + ":\n" + students;
        }
    }

    /**
     * Validates the login credentials of the lecturer.
     * @param username The entered username for validation.
     * @param password The entered password for validation.
     * @return True if the credentials are valid, false otherwise.
     */
    private boolean validateLogin(String username, String password) {
        return lecturerCredentials.containsKey(username) && lecturerCredentials.get(username).equals(password);
    }

    /**
     * Reads lecturer credentials from the database CSV file and populates the lecturerCredentials map.
     */
    private void readLecturerCredentials() {
        lecturerCredentials = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("database.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5 && parts[0].equalsIgnoreCase("Lecturer")) {
                    String username = parts[1];
                    String lecturerName = parts[2];
                    String password = parts[4];
                    lecturerCredentials.put(username, password);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays an alert dialog with the specified title and content.
     * @param title   The title of the alert dialog.
     * @param content The content or message of the alert dialog.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
