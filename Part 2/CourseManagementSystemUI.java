
/**
 * CourseManagementSystemUI is a JavaFX application for managing courses and assigning lecturers.
 * It allows an admin user to log in, assign lecturers to courses, and add new courses.
 * The application uses CSV files for storing course and user data.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.application.Platform;
import javafx.scene.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class CourseManagementSystemUI extends Application {

    private List<String[]> coursesData;
    private List<String[]> databaseData;
    private ComboBox<String> courseCodeComboBox;
    private ComboBox<String> lecturerNameComboBox;
    private TextField newCourseCodeField;
    private TextField newCourseCreditField;
    private TextField newCoursePrerequisiteField;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "123";

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The entry point of the JavaFX application.
     *
     * @param primaryStage The primary stage for the application window.
     */
    @Override
    public void start(Stage primaryStage) {
        if (showLoginDialog()) {
            // Continue with the main application
            primaryStage.setTitle("Course Management System");

            // Load data from CSV files
            coursesData = loadCsv("courses.csv");
            databaseData = loadCsv("database.csv");

            // Sort courses based on credits
            sortCoursesByCredits();

            // Create UI components
            GridPane gridPane = createGridPane();
            addLabels(gridPane);
            addComboBoxes(gridPane);
            addNewCourseFields(gridPane);
            addButton(gridPane);

            // Set up the scene
            Scene scene = new Scene(gridPane, 400, 250);
            primaryStage.setScene(scene);

            primaryStage.show();
        }
    }

    /**
     * Shows a login dialog for the admin user.
     *
     * @return true if the login is successful; false otherwise.
     */
    private boolean showLoginDialog() {
        Dialog<Pair<String, String>> loginDialog = new Dialog<>();
        loginDialog.setTitle("Login Dialog");
        loginDialog.setHeaderText("Enter your credentials");

        // Set the button types
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        loginDialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(username::requestFocus);

        // Convert the result to a username-password-pair when the login button is
        // clicked.
        loginDialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = loginDialog.showAndWait();

        return result.isPresent() && result.get().getKey().equals(ADMIN_USERNAME)
                && result.get().getValue().equals(ADMIN_PASSWORD);
    }

    /**
     * Creates a new instance of GridPane with default padding and gap settings.
     *
     * @return A new GridPane instance.
     */
    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        return gridPane;
    }

    /**
     * Adds labels to the specified GridPane.
     *
     * @param gridPane The GridPane to which labels are added.
     */
    private void addLabels(GridPane gridPane) {
        Label courseCodeLabel = new Label("Course Code:");
        GridPane.setConstraints(courseCodeLabel, 0, 0);

        Label lecturerNameLabel = new Label("Lecturer Name:");
        GridPane.setConstraints(lecturerNameLabel, 0, 1);

        Label newCourseLabel = new Label("Add New Course:");
        GridPane.setConstraints(newCourseLabel, 0, 3, 2, 1);

        gridPane.getChildren().addAll(courseCodeLabel, lecturerNameLabel, newCourseLabel);
    }

    /**
     * Adds ComboBoxes for course code and lecturer name to the specified GridPane.
     *
     * @param gridPane The GridPane to which ComboBoxes are added.
     */
    private void addComboBoxes(GridPane gridPane) {
        courseCodeComboBox = new ComboBox<>(FXCollections.observableArrayList(getCourseCodes()));
        GridPane.setConstraints(courseCodeComboBox, 1, 0);

        lecturerNameComboBox = new ComboBox<>(FXCollections.observableArrayList(getLecturerNames()));
        GridPane.setConstraints(lecturerNameComboBox, 1, 1);

        gridPane.getChildren().addAll(courseCodeComboBox, lecturerNameComboBox);
    }

    /**
     * Adds buttons for assigning a lecturer and adding a new course to the
     * specified GridPane.
     *
     * @param gridPane The GridPane to which buttons are added.
     */
    private void addNewCourseFields(GridPane gridPane) {
        Label newCourseCodeLabel = new Label("Course Code:");
        GridPane.setConstraints(newCourseCodeLabel, 0, 4);

        newCourseCodeField = new TextField();
        GridPane.setConstraints(newCourseCodeField, 1, 4);

        Label newCourseCreditLabel = new Label("Course Credit:");
        GridPane.setConstraints(newCourseCreditLabel, 0, 5);

        newCourseCreditField = new TextField();
        GridPane.setConstraints(newCourseCreditField, 1, 5);

        Label newCoursePrerequisiteLabel = new Label("Prerequisite:");
        GridPane.setConstraints(newCoursePrerequisiteLabel, 0, 6);

        newCoursePrerequisiteField = new TextField();
        GridPane.setConstraints(newCoursePrerequisiteField, 1, 6);

        gridPane.getChildren().addAll(newCourseCodeLabel, newCourseCodeField, newCourseCreditLabel,
                newCourseCreditField, newCoursePrerequisiteLabel, newCoursePrerequisiteField);
    }

    private void addButton(GridPane gridPane) {
        Button assignButton = new Button("Assign Lecturer");
        GridPane.setConstraints(assignButton, 0, 2, 2, 1);

        assignButton.setOnAction(e -> {
            assignLecturer();
            showAlert("Lecturer Assigned", "Lecturer assigned successfully!");
        });

        Button addCourseButton = new Button("Add Course");
        GridPane.setConstraints(addCourseButton, 0, 7, 2, 1);

        addCourseButton.setOnAction(e -> {
            addNewCourse();
            showAlert("Course Added", "New course added successfully!");
        });

        gridPane.getChildren().addAll(assignButton, addCourseButton);
    }

    /**
     * Displays an information alert dialog.
     *
     * @param title   The title of the alert.
     * @param content The content text of the alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Loads data from a CSV file into a List of String arrays.
     *
     * @param filename The name of the CSV file to load.
     * @return List<String[]> - The loaded data.
     */
    private List<String[]> loadCsv(String filename) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                data.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Saves data to a CSV file from a List of String arrays.
     *
     * @param filename The name of the CSV file to save to.
     * @param data     The data to be saved.
     */
    private void saveCsv(String filename, List<String[]> data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of course codes from the loaded course data.
     *
     * @return A list of course codes.
     */
    private List<String> getCourseCodes() {
        List<String> courseCodes = new ArrayList<>();
        for (String[] course : coursesData.subList(1, coursesData.size())) {
            courseCodes.add(course[1]); // Assuming course code is at index 1
        }
        return courseCodes;
    }

    /**
     * Retrieves a list of lecturer names from the loaded user data.
     *
     * @return A list of lecturer names.
     */
    private List<String> getLecturerNames() {
        List<String> lecturerNames = new ArrayList<>();
        for (String[] lecturer : databaseData) {
            if ("Lecturer".equals(lecturer[0])) {
                lecturerNames.add(lecturer[1]); // Assuming lecturer name is at index 1
            }
        }
        return lecturerNames;
    }

    /**
     * Assigns a lecturer to a selected course and updates the data accordingly.
     */
    private void assignLecturer() {
        String selectedCourseCode = courseCodeComboBox.getValue();
        String selectedLecturerName = lecturerNameComboBox.getValue();

        if (selectedCourseCode != null && selectedLecturerName != null) {
            // Update the coursesData with the assigned lecturer
            for (String[] course : coursesData.subList(1, coursesData.size())) {
                if (selectedCourseCode.equals(course[1])) {
                    course[4] = selectedLecturerName; // Assuming lecturer name is at index 4
                    break;
                }
            }

            // Save the updated coursesData to the CSV file
            saveCsv("courses.csv", coursesData);

            // Sort courses based on credits after assignment
            sortCoursesByCredits();
        }
    }

    /**
     * Adds a new course with the entered details and updates the data accordingly.
     */
    private void addNewCourse() {
        String newCourseCode = newCourseCodeField.getText().toUpperCase();
        String newCourseCredit = newCourseCreditField.getText();
        String newCoursePrerequisite = newCoursePrerequisiteField.getText();

        if (!newCourseCode.isEmpty() && !newCourseCredit.isEmpty()) {
            // Format the new course data
            String[] newCourse = { newCourseCredit + " Credits", newCourseCode,
                    newCoursePrerequisite.isEmpty() ? "Nil" : newCoursePrerequisite, "-", "no assigned lecturer" };

            // Find the correct position to insert the new course based on credits
            int index = findInsertIndex(newCourse);

            // Insert the new course at the correct position
            coursesData.add(index, newCourse);

            // Save the updated coursesData to the CSV file
            saveCsv("courses.csv", coursesData);

            // Update the course code combo box
            courseCodeComboBox.getItems().setAll(getCourseCodes());
        }
    }

    /**
     * Sorts the courses based on credits using a custom comparator.
     */
    private void sortCoursesByCredits() {
        // Sort the courses based on credits
        Collections.sort(coursesData.subList(1, coursesData.size()),
                Comparator.comparing(course -> Integer.parseInt(course[0].split(" ")[0])));
    }

    /**
     * Finds the correct index to insert a new course based on credits.
     *
     * @param newCourse The new course to be inserted.
     * @return The index where the new course should be inserted.
     */
    private int findInsertIndex(String[] newCourse) {
        // Find the correct position to insert the new course based on credits
        int index = 1; // Skip the header
        while (index < coursesData.size() && Integer.parseInt(newCourse[0].split(" ")[0]) > Integer
                .parseInt(coursesData.get(index)[0].split(" ")[0])) {
            index++;
        }
        return index;
    }
}
