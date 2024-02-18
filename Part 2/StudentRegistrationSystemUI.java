
/**
 * StudentRegistrationSystemUI is a JavaFX application for managing student course registration.
 * It allows students to log in, register for courses, drop courses, and view their past, current, and future subjects.
 * The application uses CSV files for storing course and student data.
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRegistrationSystemUI extends Application {

    private String currentStudentName;
    private List<String[]> coursesData;
    private List<String[]> studentData;
    private List<String> cartCourses = new ArrayList<>();

    /**
     * The entry point of the JavaFX application.
     *
     * @param primaryStage The primary stage for the application window.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Registration System");

        coursesData = loadCsv("courses.csv");
        studentData = loadCsv("database.csv");

        GridPane gridPane = createGridPane();
        addLoginFields(gridPane);

        Scene loginScene = new Scene(gridPane, 300, 150);
        primaryStage.setScene(loginScene);

        primaryStage.show();
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
     * Adds login form fields to the specified GridPane.
     *
     * @param gridPane The GridPane to which login form fields are added.
     */
    private void addLoginFields(GridPane gridPane) {
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);

        TextField usernameField = new TextField();
        GridPane.setConstraints(usernameField, 1, 0);

        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);

        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);

        // Adding login form elements to the GridPane
        gridPane.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton);

        // Event handler for the login button
        loginButton.setOnAction(e -> {
            String enteredUsername = usernameField.getText();
            String enteredPassword = passwordField.getText();

            // Attempting to authenticate the user
            currentStudentName = authenticateAndGetStudentName(enteredUsername, enteredPassword);

            if (currentStudentName != null) {
                showAlert("Login Successful", "Welcome, " + currentStudentName + "!");
                showStudentMenu();
            } else {
                showAlert("Login Failed", "Invalid username or password. Please try again.");
            }
        });
    }

    /**
     * Authenticates the user by checking the provided username and password against
     * stored data.
     *
     * @param enteredUsername The username entered by the user.
     * @param enteredPassword The password entered by the user.
     * @return The name of the authenticated student if successful, otherwise null.
     */
    private String authenticateAndGetStudentName(String enteredUsername, String enteredPassword) {
        String csvFile = "database.csv";
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] user = line.split(cvsSplitBy);

                // Check if username and password match
                if ("Student".equals(user[0]) && user.length >= 4
                        && user[1].equals(enteredUsername) && user[4].equals(enteredPassword)) {
                    return user[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Displays the student menu by creating a new stage and scene.
     */
    private void showStudentMenu() {
        GridPane studentMenuGrid = createGridPane();
        addStudentMenuFields(studentMenuGrid);

        Scene studentMenuScene = new Scene(studentMenuGrid, 400, 300);

        Stage studentMenuStage = new Stage();
        studentMenuStage.setTitle("Student Menu - " + currentStudentName);
        studentMenuStage.setScene(studentMenuScene);
        studentMenuStage.show();
    }

    /**
     * Adds various buttons for actions like course registration, dropping courses,
     * and viewing subjects to the specified GridPane.
     *
     * @param gridPane The GridPane to which menu fields are added.
     */
    private void addStudentMenuFields(GridPane gridPane) {
        Button registerButton = new Button("Register for Courses");
        GridPane.setConstraints(registerButton, 0, 0);

        Button dropButton = new Button("Drop Courses");
        GridPane.setConstraints(dropButton, 0, 1);

        Button viewPastButton = new Button("View Past Subjects");
        GridPane.setConstraints(viewPastButton, 0, 2);

        Button viewCurrentButton = new Button("View Current Subjects");
        GridPane.setConstraints(viewCurrentButton, 0, 3);

        Button viewFutureButton = new Button("View Future Subjects");
        GridPane.setConstraints(viewFutureButton, 0, 4);

        Button viewCartButton = new Button("View Cart");
        GridPane.setConstraints(viewCartButton, 0, 5);

        gridPane.getChildren().addAll(registerButton, dropButton, viewPastButton, viewCurrentButton, viewFutureButton,
                viewCartButton);

        // Event handlers for the buttons
        registerButton.setOnAction(e -> showRegistrationDialog());
        dropButton.setOnAction(e -> showDropDialog());
        viewPastButton.setOnAction(e -> showSubjectsDialog("Past", getPastSubjects()));
        viewCurrentButton.setOnAction(e -> showSubjectsDialog("Current", getCurrentSubjects()));
        viewFutureButton.setOnAction(e -> showSubjectsDialog("Future", getFutureSubjects()));
        viewCartButton.setOnAction(e -> viewCart());
    }

    /**
     * Displays a dialog for course registration, allowing the user to select
     * courses and add them to the cart.
     */
    private void showRegistrationDialog() {
        List<String> availableCourses = getAvailableCourses();
        if (availableCourses.isEmpty()) {
            showAlert("Registration Error", "No available courses for registration.");
            return;
        }

        ListView<String> courseListView = new ListView<>();
        courseListView.getItems().addAll(availableCourses);
        courseListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Display the credit information directly in the course selection dialog
        courseListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    int creditsToAdd = getCreditsFromCourse(getCodeFromCourse(item));
                    setText(item + ", Credits: " + creditsToAdd);
                }
            }
        });

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Course Registration");
        dialog.setHeaderText("Select courses to add to cart:");

        // Add a label to instruct the user about multi-selection
        Label instructionLabel = new Label("Hold Ctrl for multi-selection");
        dialog.getDialogPane().setContent(new VBox(instructionLabel, courseListView));

        ButtonType addToCartButtonType = new ButtonType("Add to Cart", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addToCartButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addToCartButtonType) {
                // Get the selected items and add them to the cart
                cartCourses.addAll(courseListView.getSelectionModel().getSelectedItems());
            }
            return null;
        });

        Optional<Void> result = dialog.showAndWait();

        result.ifPresent(voidResult -> {
            // Display a confirmation message with the selected courses and their credits
            StringBuilder cartContentWithCredits = new StringBuilder();
            for (String selectedCourse : cartCourses) {
                int creditsToAdd = getCreditsFromCourse(getCodeFromCourse(selectedCourse));
                cartContentWithCredits.append(selectedCourse).append(", Credits: ").append(creditsToAdd).append("\n");
            }

            showAlert("Courses Added to Cart",
                    "Selected courses have been added to the cart:\n" + cartContentWithCredits.toString());
        });
    }

    /**
     * Displays the contents of the student's cart, performing validations before
     * showing the cart details.
     */
    private void viewCart() {
        if (cartCourses.isEmpty()) {
            showAlert("Cart Empty", "Your cart is currently empty.");
        } else {
            // Calculate the sum of credits in the cart
            int totalCreditsInCart = calculateTotalCreditsInCart();

            // Validate cart credits
            if (totalCreditsInCart < 3) {
                showAlert("Invalid Cart", "You must have a minimum of 3 credits in your cart.");
                return;
            }

            if (totalCreditsInCart > 12) {
                showAlert("Invalid Cart", "You cannot have more than 12 credits in your cart.");
                return;
            }

            String cartContent = String.join("\n", cartCourses);
            showAlert("Cart Contents", "Courses in your cart:\n" + cartContent);

            // Save the cart and update courses.csv
            saveCartToCourses();
        }
    }

    /**
     * Calculates the total credits in the cart based on the selected courses.
     *
     * @return The total credits in the cart.
     */
    private int calculateTotalCreditsInCart() {
        int totalCredits = 0;
        for (String selectedCourse : cartCourses) {
            int creditsToAdd = getCreditsFromCourse(getCodeFromCourse(selectedCourse));
            totalCredits += creditsToAdd;
        }
        return totalCredits;
    }

    /**
     * Checks if the student is already registered for a given course.
     *
     * @param courseCode The code of the course to check registration status.
     * @return True if the student is already registered for the course; false
     *         otherwise.
     */
    private boolean isAlreadyRegistered(String courseCode) {
        for (String[] course : coursesData.subList(1, coursesData.size())) {
            if (course[1].equals(courseCode) && course[3] != null && course[3].contains(currentStudentName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Saves the courses in the cart to the student's registration and updates the
     * courses data.
     */
    private void saveCartToCourses() {
        for (String selectedCourse : cartCourses) {
            registerForCourse(selectedCourse);
        }

        // Clear the cart after registering courses
        cartCourses.clear();
    }

    /**
     * Registers the student for a selected course, updating the course registration
     * data.
     *
     * @param selectedCourse The course selected for registration.
     */
    private void registerForCourse(String selectedCourse) {
        String courseCode = getCodeFromCourse(selectedCourse);

        int creditsToAdd = getCreditsFromCourse(courseCode);

        // Check if the total credits after registration exceed the limit
        if (getCreditsRegistered() + creditsToAdd > 12) {
            showAlert("Registration Failed", "You cannot register for more than 12 credits.");
            return;
        }

        // Check if the student is already registered for this course
        if (isAlreadyRegistered(courseCode)) {
            showAlert("Registration Failed", "You are already registered for course: " + courseCode);
            return;
        }

        // Check if the student meets the prerequisites
        if (!meetsPrerequisites(courseCode)) {
            showAlert("Registration Failed", "You do not meet the prerequisites for course: " + courseCode);
            return;
        }

        // Update the student registration for the course
        for (String[] course : coursesData.subList(1, coursesData.size())) {
            if (course[1].equals(courseCode)) {
                if (course[3] == null || course[3].equals("-")) {
                    course[3] = currentStudentName;
                } else {
                    course[3] += ";" + currentStudentName;
                }
                break;
            }
        }

        // Save the updated courses
        saveCsv("courses.csv", coursesData);

        showAlert("Registration Successful", "Successfully registered for course: " + courseCode);
    }

    private boolean meetsPrerequisites(String courseCode) {
        for (String[] course : coursesData.subList(1, coursesData.size())) {
            if (course[1].equals(courseCode)) {
                String prerequisites = course[2];
                if (prerequisites.equals("Nil")) {
                    return true; // No prerequisites
                }

                String[] prerequisiteCourses = prerequisites.split(";");
                for (String prerequisite : prerequisiteCourses) {
                    if (!isAlreadyRegistered(prerequisite.trim())) {
                        return false; // Student does not meet a prerequisite
                    }
                }

                return true; // Student meets all prerequisites
            }
        }
        return false; // Course not found
    }

    /**
     * Retrieves the number of credits associated with a given course code.
     *
     * @param courseCode The code of the course.
     * @return The number of credits for the course, or 0 if the course is not found
     *         or there's an error parsing the credits.
     */
    private int getCreditsFromCourse(String courseCode) {
        for (String[] course : coursesData.subList(1, coursesData.size())) {
            if (course[1].equals(courseCode)) {
                try {
                    return Integer.parseInt(course[0].split(" ")[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0; // Return 0 if the course is not found or there's an error parsing the credits
    }

    /**
     * Calculates the total credits registered by the current student.
     *
     * @return The total credits registered by the current student.
     */
    private int getCreditsRegistered() {
        int totalCredits = 0;

        for (String[] course : coursesData.subList(1, coursesData.size())) {
            if (course[3] != null && course[3].contains(currentStudentName) && !course[3].equals("-")) {
                totalCredits += getCreditsFromCourse(course[1]);
            }
        }

        return totalCredits;
    }

    /**
     * Retrieves a list of available courses for the current student.
     *
     * @return A list of available courses in the format "Course Code, Course
     *         Title".
     */
    private List<String> getAvailableCourses() {
        List<String> availableCourses = new ArrayList<>();
        for (String[] course : coursesData.subList(1, coursesData.size())) {
            if (course[3] == null || course[3].equals("-") || !course[3].contains(currentStudentName)) {
                availableCourses.add(course[1] + ", " + course[4]);
            }
        }
        return availableCourses;
    }

    // Method: showDropDialog
    // Description: Displays a dialog for the user to select a course to drop. If
    // the user is not registered
    // for any courses, it shows an alert. The selected course is then dropped using
    // the dropCourse method.
    // Parameters: None
    // Return value: None
    private void showDropDialog() {
        List<String> registeredCourses = getRegisteredCourses();
        if (registeredCourses.isEmpty()) {
            showAlert("Drop Error", "You are not registered for any courses.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(registeredCourses.get(0), registeredCourses);
        dialog.setTitle("Drop Course");
        dialog.setHeaderText("Select a course to drop:");
        dialog.setContentText("Course:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(this::dropCourse);
    }

    // Method: dropCourse
    // Description: Drops the selected course by updating the list of students for
    // that course.
    // Parameters:
    // - selectedCourse: The course selected by the user to drop.
    // Return value: None
    private void dropCourse(String selectedCourse) {
        String courseCode = getCodeFromCourse(selectedCourse);

        for (String[] course : coursesData.subList(1, coursesData.size())) {
            if (course[1].equals(courseCode)) {
                String[] students = course[3].split(";");
                StringBuilder updatedStudents = new StringBuilder();

                for (String student : students) {
                    if (!student.equals(currentStudentName)) {
                        if (updatedStudents.length() > 0) {
                            updatedStudents.append(";");
                        }
                        updatedStudents.append(student);
                    }
                }

                course[3] = updatedStudents.toString();
            }
        }

        saveCsv("courses.csv", coursesData);

        showAlert("Drop Successful", "Successfully dropped course: " + courseCode);
    }

    // Method: getRegisteredCourses
    // Description: Retrieves a list of registered courses for the current student.
    // Parameters: None
    // Return value: List<String> - A list of registered courses in the format
    // "Course Code, Course Name".
    private List<String> getRegisteredCourses() {
        List<String> registeredCourses = new ArrayList<>();
        for (String[] course : coursesData.subList(1, coursesData.size())) {
            if (course[3] != null && course[3].contains(currentStudentName) && !course[3].equals("-")) {
                registeredCourses.add(course[1] + ", " + course[4]);
            }
        }
        return registeredCourses;
    }

    // Method: getPastSubjects
    // Description: Retrieves a list of past subjects (presumably dropped courses)
    // for the current student.
    // Parameters: None
    // Return value: List<String> - A list of past subjects in the format "Course
    // Code, Course Name".
    private List<String> getPastSubjects() {
        List<String> pastSubjects = new ArrayList<>();

        for (String[] course : coursesData.subList(1, coursesData.size())) {
            // Check if the course was dropped by the current student
            if (course[3] != null && course[3].contains(currentStudentName) && !course[3].equals("-")) {
                pastSubjects.add(course[1] + ", " + course[4]);
            }
        }

        return pastSubjects;
    }

    // Method: getCurrentSubjects
    // Description: Retrieves a list of subjects currently taken by the current
    // student.
    // Parameters: None
    // Return value: List<String> - A list of current subjects in the format "Course
    // Code, Course Name".
    private List<String> getCurrentSubjects() {
        List<String> currentSubjects = new ArrayList<>();

        for (String[] course : coursesData.subList(1, coursesData.size())) {
            // Check if the course is taken by the current student
            if (course[3] != null && course[3].contains(currentStudentName) && !course[3].equals("-")) {
                currentSubjects.add(course[1] + ", " + course[4]);
            }
        }

        return currentSubjects;
    }

    // Method: getFutureSubjects
    // Description: Retrieves a list of subjects not yet taken by the current
    // student.
    // Parameters: None
    // Return value: List<String> - A list of future subjects in the format "Course
    // Code, Course Name".
    private List<String> getFutureSubjects() {
        List<String> futureSubjects = new ArrayList<>();

        for (String[] course : coursesData.subList(1, coursesData.size())) {
            // Check if the course is not yet taken by the current student
            if (course[3] == null || !course[3].contains(currentStudentName) || course[3].equals("-")) {
                futureSubjects.add(course[1] + ", " + course[4]);
            }
        }

        return futureSubjects;
    }

    // Method: showSubjectsDialog
    // Description: Shows a dialog with information about the given type of
    // subjects.
    // Parameters:
    // - subjectsType: The type of subjects (e.g., "Current" or "Future").
    // - subjects: The list of subjects to display.
    // Return value: None
    private void showSubjectsDialog(String subjectsType, List<String> subjects) {
        if (subjects.isEmpty()) {
            showAlert("No " + subjectsType + " Subjects", "You have no " + subjectsType.toLowerCase() + " subjects.");
        } else {
            String message = "Your " + subjectsType.toLowerCase() + " subjects:\n" + String.join("\n", subjects);
            showAlert(subjectsType + " Subjects", message);
        }
    }

    // Method: showAlert
    // Description: Shows an information alert dialog.
    // Parameters:
    // - title: The title of the alert.
    // - content: The content text of the alert.
    // Return value: None
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Method: loadCsv
    // Description: Loads data from a CSV file into a List of String arrays.
    // Parameters:
    // - filename: The name of the CSV file to load.
    // Return value: List<String[]> - The loaded data.
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

    // Method: saveCsv
    // Description: Saves data to a CSV file from a List of String arrays.
    // Parameters:
    // - filename: The name of the CSV file to save to.
    // - data: The data to be saved.
    // Return value: None
    private void saveCsv(String filename, List<String[]> data) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method: getCodeFromCourse
    // Description: Extracts the course code from a string representing a course.
    // Parameters:
    // - course: The string representing a course in the format "Course Code, Course
    // Name".
    // Return value: String - The extracted course code.
    private String getCodeFromCourse(String course) {
        String[] parts = course.split(",");
        return parts[0].trim();
    }

    // Main method
    public static void main(String[] args) {
        launch(args);
    }
}
