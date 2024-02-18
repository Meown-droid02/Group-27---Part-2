import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a JavaFX application implementing a simple login system with user authentication
 * and account lockout functionality.
 */
public class LoginSystemUI extends Application {

    // Flag to track user login status
    private boolean userLoggedIn = false;
    // Declare usernameField as a class member variable
    private TextField usernameField;
    // Set to track locked usernames
    private Set<String> lockedUsernames = new HashSet<>();
    // Count of failed login attempts for a user
    private int failedAttempts = 0;
    // Label to display countdown
    private Label countdownLabel;
    // Timeline for countdown
    private Timeline countdownTimeline;

    /**
     * The main method to launch the JavaFX application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Override of the start method in Application class. Initializes the UI components and sets up the primary stage.
     * @param primaryStage The primary stage for the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login System");

        // Create the main GridPane layout
        GridPane loginGridPane = createGridPane();
        // Add login fields to the GridPane
        addLoginFields(loginGridPane);

        // Create the Scene with the GridPane and set it to the primary stage
        Scene loginScene = new Scene(loginGridPane, 300, 200);
        primaryStage.setScene(loginScene);

        // Show the primary stage
        primaryStage.show();
    }

    /**
     * Creates and configures a GridPane layout with padding and gaps.
     * @return The configured GridPane.
     */
    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        return gridPane;
    }

    /**
     * Adds login-related fields (labels, text fields, buttons) to the provided GridPane.
     * @param gridPane The GridPane to which login fields will be added.
     */
    private void addLoginFields(GridPane gridPane) {
        // Label for username
        Label usernameLabel = new Label("Username:");
        GridPane.setConstraints(usernameLabel, 0, 0);

        // Text field for entering the username
        usernameField = new TextField();
        GridPane.setConstraints(usernameField, 1, 0);

        // Label for password
        Label passwordLabel = new Label("Password:");
        GridPane.setConstraints(passwordLabel, 0, 1);

        // Password field for entering the password
        PasswordField passwordField = new PasswordField();
        GridPane.setConstraints(passwordField, 1, 1);

        // Button for initiating the login process
        Button loginButton = new Button("Login");
        GridPane.setConstraints(loginButton, 1, 2);

        // Label to display countdown during lockout
        countdownLabel = new Label("");
        GridPane.setConstraints(countdownLabel, 1, 3);

        // Add all components to the GridPane
        gridPane.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, countdownLabel);

        // Set up the action event for the login button
        loginButton.setOnAction(e -> {
            // Get entered username and password
            String enteredUsername = usernameField.getText();
            String enteredPassword = passwordField.getText();

            // Check if this username has already attempted to log in
            if (lockedUsernames.contains(enteredUsername)) {
                showAlert("Login Failed", "User " + enteredUsername + " is locked. Please wait for the countdown to finish.");
                return; // Skip the login attempt
            }

            // Authenticate the user and get the user type
            UserType userType = authenticateAndGetUserType(enteredUsername, enteredPassword);

            if (userType != null) {
                showAlert("Login Successful", "Welcome, " + enteredUsername + "! You are a " + userType + ".");
                userLoggedIn = true; // Set the userLoggedIn flag to true on successful login
                closeWindow(); // Close the login window after successful login
            } else {
                failedAttempts++;
                if (failedAttempts >= 3) {
                    lockUser(enteredUsername);
                }
                showAlert("Login Failed", "Invalid username or password. Please try again.");
            }
        });
    }

    /**
     * Authenticates the user by reading from a CSV file and returns the user type.
     * @param enteredUsername The entered username for authentication.
     * @param enteredPassword The entered password for authentication.
     * @return The UserType of the authenticated user, or null if authentication fails.
     */
    private UserType authenticateAndGetUserType(String enteredUsername, String enteredPassword) {
        String csvFile = "database.csv";
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] user = line.split(cvsSplitBy);

                // Check if username and password match
                if (user.length >= 4 && user[1].equals(enteredUsername) && user[4].equals(enteredPassword)) {
                    return UserType.valueOf(user[0].toUpperCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Locks the user account by adding the username to the set of locked usernames and starting a countdown.
     * @param username The username to be locked.
     */
    private void lockUser(String username) {
        lockedUsernames.add(username);
        countdownLabel.setText("Lockdown: 5 seconds");
        startCountdown();
    }

    /**
     * Starts a countdown timeline to unlock the user account after a specified time.
     */
    private void startCountdown() {
        countdownTimeline = new Timeline();
        countdownTimeline.setCycleCount(5); // 5 seconds countdown
        countdownTimeline.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), event -> {
                    countdownLabel.setText("Lockdown: " + countdownTimeline.getCycleCount() + " seconds");
                    countdownTimeline.setCycleCount(countdownTimeline.getCycleCount() - 1);
                })
        );
        countdownTimeline.setOnFinished(event -> {
            countdownLabel.setText(""); // Clear the countdown label
            failedAttempts = 0; // Reset failed attempts
            lockedUsernames.clear(); // Remove the username from locked set
        });
        countdownTimeline.play();
    }

    /**
     * Displays an alert with the specified title and content.
     * @param title   The title of the alert.
     * @param content The content of the alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Closes the current window.
     */
    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
    }

    /**
     * Checks if the user is logged in.
     * @return True if the user is logged in, false otherwise.
     */
    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    /**
     * Enum representing different user types.
     */
    enum UserType {
        STUDENT,
        LECTURER
    }
}
