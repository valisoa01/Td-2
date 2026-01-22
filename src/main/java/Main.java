import Connection.DBRetriever;
import classes.Ingredient;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        DBRetriever db = new DBRetriever();

        Ingredient ing = db.findIngredientById(1);

        double stock = ing.getStockValueAt(
                LocalDate.of(2024, 1, 6)
        );

        System.out.println("Stock Laitue au 06/01/2024 = " + stock + " KG");
    }
}
