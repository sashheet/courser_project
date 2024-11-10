import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class RegistrationApp extends Application {
    private int userAge;

    @Override
    public void start(Stage primaryStage) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Логин");
        usernameField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 20px; -fx-background-color: #ffffff; -fx-border-color: #4CAF50; -fx-border-radius: 20px;");
        usernameField.setOpacity(0);

        Tooltip usernameTooltip = new Tooltip("Логин может содержать только латинские буквы и цифры.");
        usernameField.setTooltip(usernameTooltip);

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
                showNameInputScreen(primaryStage);
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
        String usernameRegex = "^[a-zA-Z0-9]+$";

        if (!username.matches(usernameRegex)) {
            showWarning("Логин может содержать только латинские буквы и цифры.");
            return false;
        }

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
            String url = "jdbc:mysql://localhost:3306/nutrition_db";
            String user = "root";
            String password = "A10254229sds@";
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

    private void showNameInputScreen(Stage primaryStage) {
        Label nameLabel = new Label("Как вас зовут?");
        nameLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");


        TextField nameField = new TextField();
        nameField.setPromptText("Имя");
        nameField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 20px; -fx-background-color: #ffffff; -fx-border-color: #4CAF50; -fx-border-radius: 20px;");
        nameField.setOpacity(0);

        Button submitButton = new Button("Далее");
        submitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOpacity(0);

        submitButton.setOnAction(event -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                System.out.println("Имя успешно введено: " + name);
                showGenderSelectionScreen(primaryStage);
            } else {
                showWarning("Пожалуйста, введите ваше имя.");
            }
        });

        Button backButton = new Button("<");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        backButton.setOpacity(0);


        backButton.setOnAction(event -> {
            showMainMenu(primaryStage);
        });

        VBox vbox = new VBox(20, nameLabel, nameField, submitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        HBox hbox = new HBox(10, backButton);
        hbox.setAlignment(Pos.TOP_LEFT);

        VBox mainLayout = new VBox(20, hbox, vbox);
        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout, 400, 300);  // Используем mainLayout вместо vbox
        primaryStage.setTitle("Ввод имени");

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), mainLayout);
        translateTransition.setFromX(primaryStage.getWidth());
        translateTransition.setToX(0);
        translateTransition.play();

        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInNameField = new FadeTransition(Duration.seconds(1), nameField);
        fadeInNameField.setFromValue(0);
        fadeInNameField.setToValue(1);
        fadeInNameField.play();

        FadeTransition fadeInSubmitButton = new FadeTransition(Duration.seconds(1), submitButton);
        fadeInSubmitButton.setFromValue(0);
        fadeInSubmitButton.setToValue(1);
        fadeInSubmitButton.setDelay(Duration.seconds(0.5));
        fadeInSubmitButton.play();

        FadeTransition fadeInBackButton = new FadeTransition(Duration.seconds(1), backButton);
        fadeInBackButton.setFromValue(0);
        fadeInBackButton.setToValue(1);
        fadeInBackButton.setDelay(Duration.seconds(1));
        fadeInBackButton.play();
    }

    private void showGenderSelectionScreen(Stage primaryStage) {
        Label genderLabel = new Label("Выберите пол");
        genderLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        Button maleButton = new Button("Мужчина");
        maleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        maleButton.setMaxWidth(Double.MAX_VALUE);

        Button femaleButton = new Button("Женщина");
        femaleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        femaleButton.setMaxWidth(Double.MAX_VALUE);

        maleButton.setOnAction(event -> {
            maleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-radius: 5px; -fx-padding: 10px 30px; -fx-background-color: transparent;");
            femaleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        });

        femaleButton.setOnAction(event -> {
            femaleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-radius: 5px; -fx-padding: 10px 30px; -fx-background-color: transparent;");
            maleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        });

        Button submitButton = new Button("Далее");
        submitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOpacity(0);

        submitButton.setOnAction(event -> {
            if (maleButton.getStyle().contains("4CAF50") || femaleButton.getStyle().contains("4CAF50")) {
                showBirthdateSelectionScreen(primaryStage);
                System.out.println("Пол выбран.");
            } else {
                showWarning("Пожалуйста, выберите пол.");
            }
        });

        Button backButton = new Button("<");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        backButton.setOpacity(0);

        backButton.setOnAction(event -> {
            showNameInputScreen(primaryStage);
        });

        VBox vbox = new VBox(20, genderLabel, maleButton, femaleButton, submitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        HBox hbox = new HBox(10, backButton);
        hbox.setAlignment(Pos.TOP_LEFT);

        VBox mainLayout = new VBox(20, hbox, vbox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(Double.MAX_VALUE);

        Scene scene = new Scene(mainLayout, 400, 300);
        primaryStage.setTitle("Выбор пола");

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), mainLayout);
        translateTransition.setFromX(primaryStage.getWidth());
        translateTransition.setToX(0);
        translateTransition.play();

        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInMaleButton = new FadeTransition(Duration.seconds(1), maleButton);
        fadeInMaleButton.setFromValue(0);
        fadeInMaleButton.setToValue(1);
        fadeInMaleButton.play();

        FadeTransition fadeInFemaleButton = new FadeTransition(Duration.seconds(1), femaleButton);
        fadeInFemaleButton.setFromValue(0);
        fadeInFemaleButton.setToValue(1);
        fadeInFemaleButton.setDelay(Duration.seconds(0.5));
        fadeInFemaleButton.play();

        FadeTransition fadeInSubmitButton = new FadeTransition(Duration.seconds(1), submitButton);
        fadeInSubmitButton.setFromValue(0);
        fadeInSubmitButton.setToValue(1);
        fadeInSubmitButton.setDelay(Duration.seconds(1));
        fadeInSubmitButton.play();

        FadeTransition fadeInBackButton = new FadeTransition(Duration.seconds(1), backButton);
        fadeInBackButton.setFromValue(0);
        fadeInBackButton.setToValue(1);
        fadeInBackButton.setDelay(Duration.seconds(1));
        fadeInBackButton.play();
    }

    private void showBirthdateSelectionScreen(Stage primaryStage) {
        Label birthdateLabel = new Label("Укажите дату вашего рождения");
        birthdateLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        DatePicker birthdatePicker = new DatePicker();
        birthdatePicker.setPromptText("Выберите дату");
        birthdatePicker.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 20px; -fx-background-color: #ffffff; -fx-border-color: #4CAF50; -fx-border-radius: 20px;");

        Button submitButton = new Button("Далее");
        submitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOpacity(0);

        submitButton.setOnAction(event -> {
            LocalDate birthdate = birthdatePicker.getValue();
            if (birthdate != null) {
                int age = Period.between(birthdate, LocalDate.now()).getYears();
                if (age >= 18) {
                    userAge = age;
                    System.out.println("Возраст успешно введен: " + age);
                    showSummaryScreen(primaryStage);
                } else {
                    showWarning("Вы должны быть старше 18 лет.");
                }
            } else {
                showWarning("Пожалуйста, выберите дату рождения.");
            }
        });

        Button backButton = new Button("<");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        backButton.setOpacity(0);

        backButton.setOnAction(event -> {
            showGenderSelectionScreen(primaryStage);
        });

        VBox vbox = new VBox(20, birthdateLabel, birthdatePicker, submitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        HBox hbox = new HBox(10, backButton);
        hbox.setAlignment(Pos.TOP_LEFT);

        VBox mainLayout = new VBox(20, hbox, vbox);
        mainLayout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(mainLayout, 400, 300);
        primaryStage.setTitle("Выбор даты рождения");

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), mainLayout);
        translateTransition.setFromX(primaryStage.getWidth());
        translateTransition.setToX(0);
        translateTransition.play();

        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInBirthdatePicker = new FadeTransition(Duration.seconds(1), birthdatePicker);
        fadeInBirthdatePicker.setFromValue(0);
        fadeInBirthdatePicker.setToValue(1);
        fadeInBirthdatePicker.play();

        FadeTransition fadeInSubmitButton = new FadeTransition(Duration.seconds(1), submitButton);
        fadeInSubmitButton.setFromValue(0);
        fadeInSubmitButton.setToValue(1);
        fadeInSubmitButton.setDelay(Duration.seconds(0.5));
        fadeInSubmitButton.play();

        FadeTransition fadeInBackButton = new FadeTransition(Duration.seconds(1), backButton);
        fadeInBackButton.setFromValue(0);
        fadeInBackButton.setToValue(1);
        fadeInBackButton.setDelay(Duration.seconds(1));
        fadeInBackButton.play();
    }

    private void showSummaryScreen(Stage primaryStage) {
        Label summaryLabel = new Label("Регистрация завершена!");
        summaryLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        Label ageLabel = new Label("Ваш возраст: " + userAge);
        ageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");

        Button finishButton = new Button("Готово");
        finishButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");

        finishButton.setOnAction(event -> {
            showMainMenu(primaryStage);
        });

        VBox vbox = new VBox(20, summaryLabel, ageLabel, finishButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setTitle("Подтверждение регистрации");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
