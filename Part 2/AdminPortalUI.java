import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;

// 1. Class description.
/**
 * AdminPortalUI is a JavaFX application for an admin portal interface with login functionality
 * and commands to view course information stored in a CSV file.
 */
public class AdminPortalUI extends Application {

    private TextField usernameField;
    private PasswordField passwordField;
    private TextArea outputArea;
    private TextField commandField;
    private Stage primaryStage;

    // 2. Constructor and method description.
    /**
     * The main method to launch the JavaFX application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    // 2. Constructor and method description.
    /**
     * Start method to initialize the primary stage and set up the login GUI.
     * @param primaryStage The primary stage for the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        // GUI initialization code...
        // Original code for GUI initialization
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Admin Portal");

        // Create UI components
        usernameField = new TextField();
        passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        outputArea = new TextArea();
        outputArea.setEditable(false);
        commandField = new TextField();
        GridPane grid = new GridPane();

        // Layout setup
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // Add components to the grid
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(new Label("Output:"), 0, 3);
        grid.add(outputArea, 1, 3);

        // Login button event handler
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            try {
                boolean isAuthenticated = authenticateAdmin(username, password);

                if (isAuthenticated) {
                    outputArea.setText("Login successful.\nEnter command (/view [course name] or /view all): ");
                    primaryStage.setScene(createCommandPage());
                } else {
                    outputArea.setText("Invalid credentials. Please try again.");
                }
            } catch (IOException ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        });

        Scene scene = new Scene(grid, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 2. Constructor and method description.
    /**
     * Creates a scene for the command page with a command input field and output area.
     * @return The Scene for the command page.
     */
    private Scene createCommandPage() {
        // Command page setup code...
        // Original code for command page setup
        GridPane commandGrid = new GridPane();
        commandGrid.setPadding(new Insets(20, 20, 20, 20));
        commandGrid.setVgap(10);
        commandGrid.setHgap(10);

        // Add components to the commandGrid
        commandGrid.add(new Label("Command:"), 0, 0);
        commandGrid.add(commandField, 1, 0);
        commandGrid.add(new Label("Output:"), 0, 1);
        commandGrid.add(outputArea, 1, 1);

        commandField.setOnAction(e -> setupCommandInput());

        return new Scene(commandGrid, 600, 400);
    }

    // 3. Parameter description.
    /**
     * Processes the user's command input and displays the output accordingly.
     */
    private void setupCommandInput() {
        // Command processing code...
        // Original code for command processing
        String command = commandField.getText().trim();

        if ("/view all".equalsIgnoreCase(command)) {
            try {
                String output = viewAllCourses();
                displayOutputInNewScene(output);
            } catch (IOException e) {
                outputArea.setText("Error: " + e.getMessage());
            }
        } else if (command.startsWith("/view ")) {
            String courseName = command.substring("/view ".length()).trim();
            try {
                String output = viewCourse(courseName);
                displayOutputInNewScene(output);
            } catch (IOException e) {
                outputArea.setText("Error: " + e.getMessage());
            }
        } else {
            outputArea.setText("Invalid command.");
        }
    }

    // 4. Return value description.
    /**
     * Authenticates the admin based on the provided username and password.
     * @param username The entered username.
     * @param password The entered password.
     * @return true if authentication is successful, false otherwise.
     * @throws IOException If an error occurs while reading user data.
     */
    private boolean authenticateAdmin(String username, String password) throws IOException {
        // Authentication code...
        // Original code for admin authentication
        return "admin".equals(username) && "123".equals(password);
    }

    // 4. Return value description.
    /**
     * Retrieves and displays information about a specific course.
     * @param courseName The name of the course to be viewed.
     * @return A formatted string containing information about the course.
     * @throws IOException If an error occurs while reading course data.
     */
    private String viewCourse(String courseName) throws IOException {
        // View course information code...
        // Original code for viewing course information
        String coursesFilePath = "courses.csv";
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(coursesFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                if (row.length >= 5 && row[1].equalsIgnoreCase(courseName)) {
                    String students = row[3];
                    String lecturer = row[4];

                    output.append("Students and Lecturer for ").append(courseName).append(":\n");

                    if (!"-".equals(students)) {
                        String[] studentNames = students.split(";");
                        output.append("Students:\n");
                        for (String studentName : studentNames) {
                            output.append("- ").append(studentName).append("\n");
                        }
                    } else {
                        output.append("No students in ").append(courseName).append(".\n");
                    }

                    if (!"no assigned lecturer".equalsIgnoreCase(lecturer)) {
                        String lecturerList = lecturer.replace(";", ",");
                        output.append("Lecturer(s): ").append(lecturerList).append("\n");
                    } else {
                        output.append("No assigned lecturer for ").append(courseName).append(".\n");
                    }

                    return output.toString();
                }
            }

        } catch (IOException e) {
            throw new IOException("Error reading courses CSV file", e);
        }

        return "Course not found.";
    }

    // 4. Return value description.
    /**
     * Retrieves and displays information about all available courses.
     * @return A formatted string containing information about all courses.
     * @throws IOException If an error occurs while reading course data.
     */
    private String viewAllCourses() throws IOException {
        // View all courses information code...
        // Original code for viewing all courses information
        String coursesFilePath = "courses.csv";
        StringBuilder output = new StringBuilder("Output for viewing all courses\n");

        try (BufferedReader reader = new BufferedReader(new FileReader(coursesFilePath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                String courseCode = row[1];
                String students = row[3];
                String lecturer = row[4];

                output.append("Students and Lecturer for ").append(courseCode).append(":\n");

                // Display students
                if (!"-".equals(students)) {
                    String[] studentNames = students.split(";");
                    output.append("Students:\n");
                    for (String studentName : studentNames) {
                        output.append("- ").append(studentName).append("\n");
                    }
                } else {
                    output.append("No students in ").append(courseCode).append(".\n");
                }

                // Display lecturer
                if (!"no assigned lecturer".equalsIgnoreCase(lecturer)) {
                    String lecturerList = lecturer.replace(";", ",");
                    output.append("Lecturer(s): ").append(lecturerList).append("\n");
                } else {
                    output.append("No assigned lecturer for ").append(courseCode).append(".\n");
                }

                // Separate each course output
                output.append("\n");
            }
        } catch (IOException e) {
            throw new IOException("Error reading courses CSV file", e);
        }

        return output.toString();
    }

    // 4. Return value description.
    /**
     * Retrieves the user's name from the database based on the provided username.
     * @param username The username for which to retrieve the name.
     * @return The user's name if found, an empty string otherwise.
     * @throws IOException If an error occurs while reading user data.
     */
    private String getUserNameFromDatabase(String username) throws IOException {
        // Retrieve user name from database code...
        // Original code for retrieving user name from the database
        String databaseFilePath = "database.csv";
        try (BufferedReader reader = new BufferedReader(new FileReader(databaseFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userInfo = line.split(",");
                if (userInfo.length >= 2 && userInfo[1].trim().equals(username)) {
                    System.out.println("Found user in database: " + userInfo[1].trim());
                    return userInfo[1].trim(); // Use index 1 for the name
                }
            }
        }
        System.out.println("User not found in database: " + username);
        return "";
    }

    // 3. Parameter description.
    /**
     * Displays the provided output in a new scene and goes back to the command page.
     * @param output The output to be displayed.
     */
    private void displayOutputInNewScene(String output) {
        // Display output in a new scene code...
        // Original code for displaying output in a new scene
        outputArea.setText(output);
        primaryStage.setScene(createCommandPage()); // Go back to the command page after displaying the output
    }
}
