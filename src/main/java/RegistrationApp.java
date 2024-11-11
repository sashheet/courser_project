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
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Node;


public class RegistrationApp extends Application {
    private String name;
    private int userAge;
    private double weight;
    private double height;
    private String activityLevel;
    private String selectedGoal;
    private String username;
    private String password;

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

        Scene scene = new Scene(vbox, 600, 600);
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
        this.username = username;
        this.password = password;
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
                insertStatement.setString(3, "0");
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
            name = nameField.getText();
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

        Scene scene = new Scene(mainLayout, 600, 600);
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

        final StringProperty selectedGender = new SimpleStringProperty("");

        maleButton.setOnAction(event -> {
            maleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-radius: 5px; -fx-padding: 10px 30px; -fx-background-color: transparent;");
            femaleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
            selectedGender.set("male");
        });

        femaleButton.setOnAction(event -> {
            femaleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-radius: 5px; -fx-padding: 10px 30px; -fx-background-color: transparent;");
            maleButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
            selectedGender.set("female");
        });

        Button submitButton = new Button("Далее");
        submitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOpacity(0);

        submitButton.setOnAction(event -> {
            if (!selectedGender.get().isEmpty()) {
                System.out.println("Выбран пол: " + selectedGender.get());
                showBirthdateSelectionScreen(primaryStage);
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

        Scene scene = new Scene(mainLayout, 600, 600);
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
                calculateAge(birthdate);
                showWeightHeightSelectionScreen(primaryStage);
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

        Scene scene = new Scene(mainLayout, 600, 600);
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

    private void calculateAge(LocalDate birthdate) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(birthdate, currentDate);
        userAge = period.getYears();
        System.out.println("Возраст: " + userAge);
    }

    private void showWeightHeightSelectionScreen(Stage primaryStage) {
        Label weightHeightLabel = new Label("Укажите ваш вес и рост");
        weightHeightLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        Label weightLabel = new Label("Вес (кг):");
        weightLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080;");
        TextField weightField = new TextField();
        weightField.setPromptText("Введите вес");
        weightField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 20px; -fx-background-color: #ffffff; -fx-border-color: #4CAF50; -fx-border-radius: 20px;");

        Label heightLabel = new Label("Рост (см):");
        heightLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080;");
        TextField heightField = new TextField();
        heightField.setPromptText("Введите рост");
        heightField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 20px; -fx-background-color: #ffffff; -fx-border-color: #4CAF50; -fx-border-radius: 20px;");

        Button submitButton = new Button("Далее");
        submitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOpacity(0);

        submitButton.setOnAction(event -> {
            String weightText = weightField.getText();
            String heightText = heightField.getText();

            if (weightText.isEmpty() || heightText.isEmpty()) {
                showWarning("Пожалуйста, введите ваш вес и рост.");
            } else {
                try {
                    weight = Double.parseDouble(weightText);
                    height = Double.parseDouble(heightText);
                    if (weight <= 0 || height <= 0) {
                        showWarning("Вес и рост должны быть положительными числами.");
                    } else {
                        System.out.println("Вес: " + weight + ", Рост: " + height);
                        showActivityLevelSelectionScreen(primaryStage);
                    }
                } catch (NumberFormatException e) {
                    showWarning("Пожалуйста, введите корректные данные для веса и роста.");
                }
            }
        });

        Button backButton = new Button("<");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        backButton.setOpacity(0);

        backButton.setOnAction(event -> {
            showGenderSelectionScreen(primaryStage);
        });

        VBox vbox = new VBox(20, weightHeightLabel, weightLabel, weightField, heightLabel, heightField, submitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        HBox hbox = new HBox(10, backButton);
        hbox.setAlignment(Pos.TOP_LEFT);

        VBox mainLayout = new VBox(20, hbox, vbox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(Double.MAX_VALUE);

        Scene scene = new Scene(mainLayout, 600, 600);
        primaryStage.setTitle("Выбор веса и роста");

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), mainLayout);
        translateTransition.setFromX(primaryStage.getWidth());
        translateTransition.setToX(0);
        translateTransition.play();

        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInWeightField = new FadeTransition(Duration.seconds(1), weightField);
        fadeInWeightField.setFromValue(0);
        fadeInWeightField.setToValue(1);
        fadeInWeightField.play();

        FadeTransition fadeInHeightField = new FadeTransition(Duration.seconds(1), heightField);
        fadeInHeightField.setFromValue(0);
        fadeInHeightField.setToValue(1);
        fadeInHeightField.setDelay(Duration.seconds(0.5));
        fadeInHeightField.play();

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

    private void showActivityLevelSelectionScreen(Stage primaryStage) {
        Label activityLevelLabel = new Label("Выберите уровень активности");
        activityLevelLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        Button lowActivityButton = new Button("Низкая активность");
        Button moderateActivityButton = new Button("Умеренная активность");
        Button highActivityButton = new Button("Высокая активность");

        lowActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        moderateActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        highActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");

        lowActivityButton.setOnAction(event -> {
            activityLevel = "Low";
            lowActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-radius: 5px; -fx-padding: 10px 30px; -fx-background-color: transparent;");
            moderateActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
            highActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        });

        moderateActivityButton.setOnAction(event -> {
            activityLevel = "Medium";
            moderateActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-radius: 5px; -fx-padding: 10px 30px; -fx-background-color: transparent;");
            lowActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
            highActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        });

        highActivityButton.setOnAction(event -> {
            activityLevel = "High";
            highActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-radius: 5px; -fx-padding: 10px 30px; -fx-background-color: transparent;");
            lowActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
            moderateActivityButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        });

        Button submitButton = new Button("Далее");
        submitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOpacity(0);

        submitButton.setOnAction(event -> {
            System.out.println("Выбран уровень активности: " + activityLevel);
            showGoalSelectionScreen(primaryStage);
        });

        Button backButton = new Button("<");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        backButton.setOpacity(0);

        backButton.setOnAction(event -> {
            showWeightHeightSelectionScreen(primaryStage);
        });

        VBox vbox = new VBox(20, activityLevelLabel, lowActivityButton, moderateActivityButton, highActivityButton, submitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        HBox hbox = new HBox(10, backButton);
        hbox.setAlignment(Pos.TOP_LEFT);

        VBox mainLayout = new VBox(20, hbox, vbox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(Double.MAX_VALUE);

        Scene scene = new Scene(mainLayout, 600, 600);
        primaryStage.setTitle("Выбор уровня активности");

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), mainLayout);
        translateTransition.setFromX(primaryStage.getWidth());
        translateTransition.setToX(0);
        translateTransition.play();

        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInLowButton = new FadeTransition(Duration.seconds(1), lowActivityButton);
        fadeInLowButton.setFromValue(0);
        fadeInLowButton.setToValue(1);
        fadeInLowButton.play();

        FadeTransition fadeInModerateButton = new FadeTransition(Duration.seconds(1), moderateActivityButton);
        fadeInModerateButton.setFromValue(0);
        fadeInModerateButton.setToValue(1);
        fadeInModerateButton.setDelay(Duration.seconds(0.5));
        fadeInModerateButton.play();

        FadeTransition fadeInHighButton = new FadeTransition(Duration.seconds(1), highActivityButton);
        fadeInHighButton.setFromValue(0);
        fadeInHighButton.setToValue(1);
        fadeInHighButton.setDelay(Duration.seconds(1));
        fadeInHighButton.play();

        FadeTransition fadeInSubmitButton = new FadeTransition(Duration.seconds(1), submitButton);
        fadeInSubmitButton.setFromValue(0);
        fadeInSubmitButton.setToValue(1);
        fadeInSubmitButton.setDelay(Duration.seconds(1.5));
        fadeInSubmitButton.play();

        FadeTransition fadeInBackButton = new FadeTransition(Duration.seconds(1), backButton);
        fadeInBackButton.setFromValue(0);
        fadeInBackButton.setToValue(1);
        fadeInBackButton.setDelay(Duration.seconds(1.5));
        fadeInBackButton.play();
    }


    private void showGoalSelectionScreen(Stage primaryStage) {
        Label goalSelectionLabel = new Label("Выберите вашу цель");
        goalSelectionLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        Button gainWeightButton = new Button("Набор массы");
        Button loseWeightButton = new Button("Сбросить вес");
        Button stayFitButton = new Button("Быть в тонусе");

        gainWeightButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        loseWeightButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        stayFitButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");

        gainWeightButton.setOnAction(event -> {
            selectedGoal = "Gain Weight";
            updateButtonStyles(gainWeightButton, loseWeightButton, stayFitButton);
        });

        loseWeightButton.setOnAction(event -> {
            selectedGoal = "Lose Weight";
            updateButtonStyles(loseWeightButton, gainWeightButton, stayFitButton);
        });

        stayFitButton.setOnAction(event -> {
            selectedGoal = "Maintain Weight";
            updateButtonStyles(stayFitButton, gainWeightButton, loseWeightButton);
        });


        Button submitButton = new Button("Далее");
        submitButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitButton.setOpacity(0);

        submitButton.setOnAction(event -> {
            showConfirmationScreen(primaryStage);
            String selectedGoal = "";
            if (gainWeightButton.getStyle().contains("#4CAF50")) {
                selectedGoal = "Gain Weight";
            } else if (loseWeightButton.getStyle().contains("#4CAF50")) {
                selectedGoal = "Lose Weight";
            } else if (stayFitButton.getStyle().contains("#4CAF50")) {
                selectedGoal = "Maintain Weight";
            }

            System.out.println("Selected goal: " + selectedGoal);
        });

        Button backButton = new Button("<");
        backButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 15px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        backButton.setOpacity(0);

        backButton.setOnAction(event -> {
            showActivityLevelSelectionScreen(primaryStage);
        });

        VBox vbox = new VBox(20, goalSelectionLabel, gainWeightButton, loseWeightButton, stayFitButton, submitButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        HBox hbox = new HBox(10, backButton);
        hbox.setAlignment(Pos.TOP_LEFT);

        VBox mainLayout = new VBox(20, hbox, vbox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setMaxWidth(Double.MAX_VALUE);

        Scene scene = new Scene(mainLayout, 600, 600);
        primaryStage.setTitle("Выбор цели");

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), mainLayout);
        translateTransition.setFromX(primaryStage.getWidth());
        translateTransition.setToX(0);
        translateTransition.play();

        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInGainWeightButton = new FadeTransition(Duration.seconds(1), gainWeightButton);
        fadeInGainWeightButton.setFromValue(0);
        fadeInGainWeightButton.setToValue(1);
        fadeInGainWeightButton.play();

        FadeTransition fadeInLoseWeightButton = new FadeTransition(Duration.seconds(1), loseWeightButton);
        fadeInLoseWeightButton.setFromValue(0);
        fadeInLoseWeightButton.setToValue(1);
        fadeInLoseWeightButton.setDelay(Duration.seconds(0.5));
        fadeInLoseWeightButton.play();

        FadeTransition fadeInStayFitButton = new FadeTransition(Duration.seconds(1), stayFitButton);
        fadeInStayFitButton.setFromValue(0);
        fadeInStayFitButton.setToValue(1);
        fadeInStayFitButton.setDelay(Duration.seconds(1));
        fadeInStayFitButton.play();

        FadeTransition fadeInSubmitButton = new FadeTransition(Duration.seconds(1), submitButton);
        fadeInSubmitButton.setFromValue(0);
        fadeInSubmitButton.setToValue(1);
        fadeInSubmitButton.setDelay(Duration.seconds(1.5));
        fadeInSubmitButton.play();

        FadeTransition fadeInBackButton = new FadeTransition(Duration.seconds(1), backButton);
        fadeInBackButton.setFromValue(0);
        fadeInBackButton.setToValue(1);
        fadeInBackButton.setDelay(Duration.seconds(1.5));
        fadeInBackButton.play();
    }

    private void updateButtonStyles(Button selectedButton, Button... otherButtons) {
        selectedButton.setStyle("-fx-font-size: 16px; -fx-text-fill: #4CAF50; -fx-border-color: #4CAF50; -fx-border-radius: 5px; -fx-padding: 10px 30px; -fx-background-color: transparent;");
        for (Button button : otherButtons) {
            button.setStyle("-fx-font-size: 16px; -fx-text-fill: #808080; -fx-border-color: #808080; -fx-border-radius: 5px; -fx-padding: 10px 30px;");
        }
    }

    private void showConfirmationScreen(Stage primaryStage) {
        Label confirmationLabel = new Label("Подтверждение данных");
        confirmationLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");

        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(10);
        infoGrid.setAlignment(Pos.CENTER);
        infoGrid.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2px; -fx-background-color: #ffffff; -fx-background-radius: 10px; -fx-padding: 20px;");

        Label nameLabel = new Label("Имя:");
        Label nameValue = new Label(name);
        Label ageLabel = new Label("Возраст:");
        Label ageValue = new Label(userAge + " лет");
        Label weightLabel = new Label("Вес:");
        Label weightValue = new Label(weight + " кг");
        Label heightLabel = new Label("Рост:");
        Label heightValue = new Label(height + " см");
        Label activityLabel = new Label("Активность:");
        Label activityValue = new Label(activityLevel);
        Label goalLabel = new Label("Цель:");
        Label goalValue = new Label(selectedGoal);

        String labelStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;";
        String valueStyle = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D5006D;";

        nameLabel.setStyle(labelStyle);
        nameValue.setStyle(valueStyle);
        ageLabel.setStyle(labelStyle);
        ageValue.setStyle(valueStyle);
        weightLabel.setStyle(labelStyle);
        weightValue.setStyle(valueStyle);
        heightLabel.setStyle(labelStyle);
        heightValue.setStyle(valueStyle);
        activityLabel.setStyle(labelStyle);
        activityValue.setStyle(valueStyle);
        goalLabel.setStyle(labelStyle);
        goalValue.setStyle(valueStyle);

        infoGrid.add(nameLabel, 0, 0);
        infoGrid.add(nameValue, 1, 0);
        infoGrid.add(ageLabel, 0, 1);
        infoGrid.add(ageValue, 1, 1);
        infoGrid.add(weightLabel, 0, 2);
        infoGrid.add(weightValue, 1, 2);
        infoGrid.add(heightLabel, 0, 3);
        infoGrid.add(heightValue, 1, 3);
        infoGrid.add(activityLabel, 0, 4);
        infoGrid.add(activityValue, 1, 4);
        infoGrid.add(goalLabel, 0, 5);
        infoGrid.add(goalValue, 1, 5);

        Button editButton = new Button("Заполнить данные заново");
        editButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px; -fx-background-radius: 20px; -fx-background-color: rgba(76, 175, 80, 0.5); -fx-text-fill: white;");
        editButton.setOnAction(event -> showNameInputScreen(primaryStage));

        Label confirmPasswordLabel = new Label("Подтвердите пароль");
        confirmPasswordLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D5006D;"); // Малиновый цвет

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Повторите пароль");
        confirmPasswordField.setStyle("-fx-font-size: 16px; -fx-padding: 10px; -fx-background-radius: 20px; -fx-background-color: #ffffff; -fx-border-color: #4CAF50; -fx-border-radius: 20px;");

        Button finishButton = new Button("Завершить регистрацию");
        finishButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        finishButton.setOnAction(event -> {
            String passwordConfirmation = confirmPasswordField.getText();
            if (passwordConfirmation.equals(password)) {
                saveUserInfoToDatabase();
                System.out.println("Регистрация завершена!");
                showMainMenu(primaryStage);
            } else {
                showWarning("Пароли не совпадают. Пожалуйста, повторите ввод.");
            }
        });

        VBox vbox = new VBox(20, confirmationLabel, infoGrid, confirmPasswordLabel, confirmPasswordField, finishButton, editButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        Scene scene = new Scene(vbox, 600, 600);
        primaryStage.setTitle("Подтверждение регистрации");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void saveUserInfoToDatabase() {
        try (Connection connection = connectToDatabase()) {
            if (connection == null) {
                showError("Не удалось подключиться к базе данных.");
                return;
            }

            String insertQuery = "INSERT INTO userinfo (login, name, age, weight, height, activity_level, goal) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, this.username);
                insertStatement.setString(2, name);
                insertStatement.setInt(3, userAge);
                insertStatement.setDouble(4, weight);
                insertStatement.setDouble(5, height);
                insertStatement.setString(6, activityLevel);
                insertStatement.setString(7, selectedGoal);

                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Ошибка при сохранении данных: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}