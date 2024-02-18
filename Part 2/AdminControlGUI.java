import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;

// 1. Class description.
/**
 * AdminControlGUI is a JavaFX application for an admin control interface with login functionality
 * and the ability to create user accounts, storing the information in a CSV file.
 */
public class AdminControlGUI extends Application {

    private TextField usernameField;
    private PasswordField passwordField;

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
        primaryStage.setTitle("Admin Control GUI");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        Label usernameLabel = new Label("Username:");
        Label passwordLabel = new Label("Password:");

        usernameField = new TextField();
        passwordField = new PasswordField();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin());

        grid.add(usernameLabel, 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(loginButton, 1, 2);

        Scene scene = new Scene(grid, 300, 150);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    // 2. Constructor and method description.
    /**
     * Handles the login button action, validating admin credentials.
     */
    private void handleLogin() {
        // Login validation code...
        // Original code for login validation
        String adminUsername = "admin";
        String adminPassword = "123";

        String enteredUsername = usernameField.getText();
        String enteredPassword = passwordField.getText();

        if (enteredUsername.equals(adminUsername) && enteredPassword.equals(adminPassword)) {
            showAdminPanel();
        } else {
            showAlert("Invalid Credentials", "Please enter correct admin username and password.");
        }
    }

    // 2. Constructor and method description.
    /**
     * Displays the admin panel upon successful login.
     */
    private void showAdminPanel() {
        // Admin panel initialization code...
        // Original code for admin panel initialization
        Stage adminPanelStage = new Stage();
        adminPanelStage.setTitle("Admin Panel");

        GridPane adminPanelGrid = new GridPane();
        adminPanelGrid.setHgap(10);
        adminPanelGrid.setVgap(10);
        adminPanelGrid.setPadding(new Insets(20, 20, 20, 20));

        Label userLabel = new Label("User Info:");
        Label nameLabel = new Label("Name:");
        Label ageLabel = new Label("Age:");
        Label idLabel = new Label("ID:");
        Label typeLabel = new Label("Type:");
        Label passwordLabel = new Label("Password:");

        TextField nameField = new TextField();
        TextField ageField = new TextField();
        TextField idField = new TextField();
        TextField passwordField = new PasswordField();

        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll("Student", "Lecturer");
        typeChoiceBox.setValue("Student");

        Button createUserButton = new Button("Create User");
        createUserButton.setOnAction(e -> createUserAccount(
                nameField.getText(),
                ageField.getText(),
                idField.getText(),
                typeChoiceBox.getValue(),
                passwordField.getText()
        ));

        adminPanelGrid.add(userLabel, 0, 0);
        adminPanelGrid.add(nameLabel, 0, 1);
        adminPanelGrid.add(nameField, 1, 1);
        adminPanelGrid.add(ageLabel, 0, 2);
        adminPanelGrid.add(ageField, 1, 2);
        adminPanelGrid.add(idLabel, 0, 3);
        adminPanelGrid.add(idField, 1, 3);
        adminPanelGrid.add(typeLabel, 0, 4);
        adminPanelGrid.add(typeChoiceBox, 1, 4);
        adminPanelGrid.add(passwordLabel, 0, 5);
        adminPanelGrid.add(passwordField, 1, 5);
        adminPanelGrid.add(createUserButton, 1, 6);

        Scene adminPanelScene = new Scene(adminPanelGrid, 400, 250);
        adminPanelStage.setScene(adminPanelScene);

        adminPanelStage.show();
    }

    // 2. Constructor and method description.
    /**
     * Creates a user account and writes the information to a CSV file.
     * @param name User's name.
     * @param age User's age.
     * @param id User's ID.
     * @param type User's type (Student or Lecturer).
     * @param password User's password.
     */
    private void createUserAccount(String name, String age, String id, String type, String password) {
        // User account creation and CSV writing code...
        // Original code for user account creation and CSV writing
        try (FileWriter csvWriter = new FileWriter("database.csv", true)) {
            csvWriter.append(type + "," + name + "," + age + "," + id + "," + password + "\n");
            csvWriter.flush();
            showAlert("Success", type + " account created successfully!");
        } catch (IOException e) {
            showAlert("Error", "An error occurred while writing to the database.");
        }
    }

    // 2. Constructor and method description.
    /**
     * Displays an alert dialog with the specified title and message.
     * @param title Title of the alert dialog.
     * @param message Message to be displayed in the alert dialog.
     */
    private void showAlert(String title, String message) {
        // Alert dialog code...
        // Original code for alert dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
