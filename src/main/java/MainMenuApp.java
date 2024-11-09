import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenuApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button loginButton = new Button("У меня уже есть аккаунт");
        loginButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        loginButton.setOpacity(0);
        loginButton.setOnAction(event -> showLoginScreen(primaryStage));

        Button registerButton = new Button("У меня нет аккаунта");
        registerButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        registerButton.setOpacity(0);
        registerButton.setOnAction(event -> showRegistrationScreen(primaryStage));

        VBox vbox = new VBox(20, loginButton, registerButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setTitle("Nutrition Diary - Меню");
        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInLoginButton = new FadeTransition(Duration.seconds(1), loginButton);
        fadeInLoginButton.setFromValue(0);
        fadeInLoginButton.setToValue(1);
        fadeInLoginButton.play();

        FadeTransition fadeInRegisterButton = new FadeTransition(Duration.seconds(1), registerButton);
        fadeInRegisterButton.setFromValue(0);
        fadeInRegisterButton.setToValue(1);
        fadeInRegisterButton.setDelay(Duration.seconds(0.5));
        fadeInRegisterButton.play();
    }

    private void showLoginScreen(Stage primaryStage) {
        LoginApp loginApp = new LoginApp();
        loginApp.start(primaryStage);
    }

    private void showRegistrationScreen(Stage primaryStage) {
        RegistrationApp registrationApp = new RegistrationApp();
        registrationApp.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
