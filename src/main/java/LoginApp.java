import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.*;

public class LoginApp extends Application {

    private Connection connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/nutrition_db";
            String user = "root";
            String password = "A10254229sds@";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

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

        Button loginButton = new Button("Войти");
        loginButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOpacity(0);

        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 20px; -fx-background-color: rgba(76, 175, 80, 0.5); -fx-text-fill: white;");
        backButton.setOpacity(0);

        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Пожалуйста, заполните оба поля");
            } else {
                String role = authenticate(username, password);
                if (role != null) {
                    System.out.println("Авторизация успешна, роль: " + role);
                    if (role.equals("1")) {
                        showAdminPage(primaryStage);
                    } else {
                        showUserPage(primaryStage);
                    }
                } else {
                    showError("Неверный логин или пароль");
                }
            }
        });

        backButton.setOnAction(event -> {
            showMainMenu(primaryStage);
        });

        VBox vbox = new VBox(20, usernameField, passwordField, loginButton, backButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setTitle("Вход в Nutrition Diary");
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

        FadeTransition fadeInLoginButton = new FadeTransition(Duration.seconds(1), loginButton);
        fadeInLoginButton.setFromValue(0);
        fadeInLoginButton.setToValue(1);
        fadeInLoginButton.setDelay(Duration.seconds(1));
        fadeInLoginButton.play();

        FadeTransition fadeInBackButton = new FadeTransition(Duration.seconds(1), backButton);
        fadeInBackButton.setFromValue(0);
        fadeInBackButton.setToValue(1);
        fadeInBackButton.setDelay(Duration.seconds(1.5));
        fadeInBackButton.play();
    }

    private String authenticate(String username, String password) {
        try (Connection connection = connectToDatabase()) {
            if (connection == null) {
                showError("Не удалось подключиться к базе данных");
                return null;
            }

            String query = "SELECT Password, Role FROM users WHERE Login = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("Password");
                    String role = resultSet.getString("Role");
                    if (password.equals(storedPassword)) {
                        return role;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setStyle("-fx-background-color: #f8d7da; -fx-font-size: 14px; -fx-text-fill: #721c24;");
        alert.showAndWait();
    }

    private void showAdminPage(Stage primaryStage) {
        AdminPageApp adminPageApp = new AdminPageApp();
        adminPageApp.start(primaryStage);
    }

    private void showUserPage(Stage primaryStage) {
        UserPageApp userPageApp = new UserPageApp();
        userPageApp.start(primaryStage);
    }

    private void showMainMenu(Stage primaryStage) {
        MainMenuApp menuApp = new MainMenuApp();
        menuApp.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
