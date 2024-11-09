import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;

import java.sql.*;

public class RegistrationApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Логин");
        usernameField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 20px; -fx-background-color: #ffffff; -fx-border-color: #4CAF50; -fx-border-radius: 20px;");
        usernameField.setOpacity(0);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Пароль");
        passwordField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 20px; -fx-background-color: #ffffff; -fx-border-color: #4CAF50; -fx-border-radius: 20px;");
        passwordField.setOpacity(0);

        Tooltip passwordTooltip = new Tooltip("Пароль должен быть не менее 8 символов");
        passwordField.setTooltip(passwordTooltip);

        Button registerButton = new Button("Зарегистрироваться");
        registerButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        registerButton.setOpacity(0);

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 20px; -fx-background-color: rgba(76, 175, 80, 0.5); -fx-text-fill: white;");
        backButton.setOpacity(0);

        registerButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (register(username, password)) {
                System.out.println("Регистрация успешна");
            } else {
                return;
            }
        });

        backButton.setOnAction(event -> {
            showMainMenu(primaryStage);
        });

        VBox vbox = new VBox(20, usernameField, passwordField, registerButton, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setTitle("Регистрация в Nutrition Diary");
        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInUsername = new FadeTransition(Duration.seconds(1), usernameField);
        fadeInUsername.setFromValue(0);
        fadeInUsername.setToValue(1);
        fadeInUsername.play();

        FadeTransition fadeInPassword = new FadeTransition(Duration.seconds(1), passwordField);
        fadeInPassword.setFromValue(0);
        fadeInPassword.setToValue(1);
        fadeInPassword.setDelay(Duration.seconds(0.5));
        fadeInPassword.play();

        FadeTransition fadeInRegisterButton = new FadeTransition(Duration.seconds(1), registerButton);
        fadeInRegisterButton.setFromValue(0);
        fadeInRegisterButton.setToValue(1);
        fadeInRegisterButton.setDelay(Duration.seconds(1));
        fadeInRegisterButton.play();

        FadeTransition fadeInBackButton = new FadeTransition(Duration.seconds(1), backButton);
        fadeInBackButton.setFromValue(0);
        fadeInBackButton.setToValue(1);
        fadeInBackButton.setDelay(Duration.seconds(1.5));
        fadeInBackButton.play();
    }

    private boolean register(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showError("Пожалуйста, заполните все поля.");
            return false;
        }

        if (password.length() < 8) {
            showWarning("Пароль должен быть не менее 8 символов.");
            return false;
        }

        try (Connection connection = connectToDatabase()) {
            if (connection == null) {
                showError("Не удалось подключиться к базе данных.");
                return false;
            }

            String checkQuery = "SELECT COUNT(*) FROM users WHERE login = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, username);
                ResultSet resultSet = checkStatement.executeQuery();
                resultSet.next();
                if (resultSet.getInt(1) > 0) {
                    showWarning("Пользователь с таким логином уже существует.");
                    return false;
                }
            }

            String insertQuery = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.setString(3, "0"); // Роль по умолчанию "user"
                int rowsAffected = insertStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Ошибка при регистрации: " + e.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #f8d7da; -fx-font-size: 14px; -fx-text-fill: #721c24;");
        alert.showAndWait();
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Предупреждение");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #f8d7da; -fx-font-size: 14px; -fx-text-fill: #721c24;");
        alert.showAndWait();
    }

    private Connection connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/nutrition_db"; // Укажите вашу базу данных
            String user = "root"; // Имя пользователя
            String password = "A10254229sds@"; // Ваш пароль
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showMainMenu(Stage primaryStage) {
        MainMenuApp menuApp = new MainMenuApp();
        menuApp.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
