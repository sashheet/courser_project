import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class NutritionApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        Text welcomeText = new Text("Nutrition Diary");
        welcomeText.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-fill: #4CAF50;");

        StackPane root = new StackPane();
        root.getChildren().add(welcomeText);

        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("Nutrition Diary");
        primaryStage.setScene(scene);
        primaryStage.show();

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), welcomeText);
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);

        FadeTransition colorFade = new FadeTransition(Duration.seconds(1), root);
        colorFade.setFromValue(1);
        colorFade.setToValue(0.7);

        FadeTransition textFadeIn = new FadeTransition(Duration.seconds(1), welcomeText);
        textFadeIn.setFromValue(0);
        textFadeIn.setToValue(1);

        FadeTransition textFadeOut = new FadeTransition(Duration.seconds(0.7), welcomeText);
        textFadeOut.setFromValue(1);
        textFadeOut.setToValue(0);

        scaleTransition.play();
        colorFade.play();
        textFadeIn.play();

        textFadeOut.setOnFinished(event -> {
            showMainMenu(primaryStage);
        });

        textFadeIn.setOnFinished(event -> {
            try {
                Thread.sleep(900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            textFadeOut.play();
        });
    }

    private void showMainMenu(Stage primaryStage) {
        MainMenuApp menuApp = new MainMenuApp();
        menuApp.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
