import javafx.beans.property.*;

public class Product {
    private IntegerProperty productId;
    private StringProperty name;
    private StringProperty composition;
    private IntegerProperty calories;
    private DoubleProperty protein;
    private DoubleProperty fat;
    private DoubleProperty carbs;
    private StringProperty nutritionalValue;

    public Product(int productId, String name, String composition, int calories, double protein, double fat, double carbs, String nutritionalValue) {
        this.productId = new SimpleIntegerProperty(productId);
        this.name = new SimpleStringProperty(name);
        this.composition = new SimpleStringProperty(composition);
        this.calories = new SimpleIntegerProperty(calories);
        this.protein = new SimpleDoubleProperty(protein);
        this.fat = new SimpleDoubleProperty(fat);
        this.carbs = new SimpleDoubleProperty(carbs);
        this.nutritionalValue = new SimpleStringProperty(nutritionalValue);
    }

    public IntegerProperty productIdProperty() {
        return productId;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty compositionProperty() {
        return composition;
    }

    public IntegerProperty caloriesProperty() {
        return calories;
    }

    public DoubleProperty proteinProperty() {
        return protein;
    }

    public DoubleProperty fatProperty() {
        return fat;
    }

    public DoubleProperty carbsProperty() {
        return carbs;
    }

    public StringProperty nutritionalValueProperty() {
        return nutritionalValue;
    }
}
