import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserPageApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Label welcomeLabel = new Label("Добро пожаловать, пользователь!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #4CAF50;");

        Button logoutButton = new Button("Выйти");
        logoutButton.setStyle("-fx-font-size: 18px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        logoutButton.setOnAction(event -> {
            showLoginPage(primaryStage);
        });

        VBox vbox = new VBox(20, welcomeLabel, logoutButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        Scene scene = new Scene(vbox, 600, 600);
        primaryStage.setTitle("Пользовательская страница");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showLoginPage(Stage primaryStage) {
        LoginApp loginApp = new LoginApp();
        loginApp.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
