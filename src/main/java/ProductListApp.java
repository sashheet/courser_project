import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class ProductListApp extends Application {

    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private TableView<Product> tableView;

    @Override
    public void start(Stage primaryStage) {
        tableView = new TableView<>();

        TableColumn<Product, String> nameColumn = new TableColumn<>("Продукт");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Product, String> compositionColumn = new TableColumn<>("Состав");
        compositionColumn.setCellValueFactory(cellData -> cellData.getValue().compositionProperty());

        TableColumn<Product, Integer> caloriesColumn = new TableColumn<>("Калории");
        caloriesColumn.setCellValueFactory(cellData -> cellData.getValue().caloriesProperty().asObject());

        TableColumn<Product, Double> proteinColumn = new TableColumn<>("Белки");
        proteinColumn.setCellValueFactory(cellData -> cellData.getValue().proteinProperty().asObject());

        TableColumn<Product, Double> fatColumn = new TableColumn<>("Жиры");
        fatColumn.setCellValueFactory(cellData -> cellData.getValue().fatProperty().asObject());

        TableColumn<Product, Double> carbsColumn = new TableColumn<>("Углеводы");
        carbsColumn.setCellValueFactory(cellData -> cellData.getValue().carbsProperty().asObject());

        TableColumn<Product, String> nutritionalValueColumn = new TableColumn<>("Питательная ценность");
        nutritionalValueColumn.setCellValueFactory(cellData -> cellData.getValue().nutritionalValueProperty());

        tableView.getColumns().addAll(nameColumn, compositionColumn, caloriesColumn, proteinColumn, fatColumn, carbsColumn, nutritionalValueColumn);

        Button loadButton = new Button("Загрузить продукты");
        loadButton.setStyle("-fx-font-size: 16px; -fx-padding: 10px 20px; -fx-background-radius: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        loadButton.setOpacity(0);

        loadButton.setOnAction(event -> loadProducts());

        VBox vbox = new VBox(20, loadButton, tableView);
        vbox.setAlignment(Pos.CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 30px;");

        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setTitle("Список продуктов");
        primaryStage.setScene(scene);
        primaryStage.show();

        FadeTransition fadeInLoadButton = new FadeTransition(Duration.seconds(1), loadButton);
        fadeInLoadButton.setFromValue(0);
        fadeInLoadButton.setToValue(1);
        fadeInLoadButton.play();
    }

    private void loadProducts() {
        productList.clear();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT ProductID, Name, Composition, Calories, Protein, Fat, Carbs, NutritionalValue FROM products";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int productId = rs.getInt("ProductID");
                String name = rs.getString("Name");
                String composition = rs.getString("Composition");
                int calories = rs.getInt("Calories");
                double protein = rs.getDouble("Protein");
                double fat = rs.getDouble("Fat");
                double carbs = rs.getDouble("Carbs");
                String nutritionalValue = rs.getString("NutritionalValue");

                productList.add(new Product(productId, name, composition, calories, protein, fat, carbs, nutritionalValue));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Ошибка при загрузке данных: " + e.getMessage());
        }

        tableView.setItems(productList);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
