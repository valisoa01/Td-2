import Connection.DBRetriever;
import classes.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        DBRetriever retriever = new DBRetriever();

        System.out.println("=== TEST GESTION DES TABLES ===");

        // Création des tables
        Table table1 = new Table(1, 1);
        Table table2 = new Table(2, 2);
        Table table3 = new Table(3, 3);

        System.out.println("Table 1 créée: id=" + table1.getId() + ", number=" + table1.getNumber());
        System.out.println("Table 2 créée: id=" + table2.getId() + ", number=" + table2.getNumber());
        System.out.println("Table 3 créée: id=" + table3.getId() + ", number=" + table3.getNumber());

        // Définition des horaires
        Instant arrival = Instant.parse("2026-01-29T12:00:00Z");
        Instant departure = Instant.parse("2026-01-29T13:00:00Z");

        System.out.println("\nTest 1: Première commande sur table 1");
        try {
            Order order1 = createOrder("CMD-001", table1, arrival, departure);
            retriever.saveOrder(order1);
            System.out.println("✅ Commande 1 enregistrée (table 1)");
        } catch (Exception e) {
            System.out.println("❌ Erreur commande 1: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nTest 2: Deuxième commande sur table 1 (doit échouer)");
        try {
            Order order2 = createOrder("CMD-002", table1, arrival, departure);
            retriever.saveOrder(order2);
            System.out.println("✅ Commande 2 enregistrée");
        } catch (Exception e) {
            System.out.println("⚠️ " + e.getMessage());
        }

        System.out.println("\nTest 3: Commandes sur tables 2, 3 et réessai table 1");
        try {
            // Commande sur table 2
            Order order3 = createOrder("CMD-003", table2, arrival, departure);
            retriever.saveOrder(order3);
            System.out.println("✅ Commande 3 enregistrée (table 2)");

            // Commande sur table 3
            Order order4 = createOrder("CMD-004", table3, arrival, departure);
            retriever.saveOrder(order4);
            System.out.println("✅ Commande 4 enregistrée (table 3)");

            // Réessai sur table 1 (doit encore échouer)
            Order order5 = createOrder("CMD-005", table1, arrival, departure);
            retriever.saveOrder(order5);
            System.out.println("✅ Commande 5 enregistrée (table 1)");
        } catch (Exception e) {
            System.out.println("⛔ Erreur: " + e.getMessage());
        }

        System.out.println("\n=== FIN DES TESTS ===");
    }

    // Méthode statique pour créer une commande avec un plat "Salade"
    private static Order createOrder(String reference, Table table, Instant arrival, Instant departure) {
        Dish salade = new Dish(1, "Salade composée", DishTypeEnum.MAIN, 2000.0);
        DishOrder dishOrder = new DishOrder(salade, 1); // chaque DishOrder a son Dish
        List<DishOrder> dishOrders = new ArrayList<>();
        dishOrders.add(dishOrder);

        Order order = new Order();
        order.setReference(reference);
        order.setCreationDate(Instant.now());
        order.setTable(table);
        order.setArrival_Datetime(arrival);
        order.setDepartureDatetime(departure);
        order.setDishOrders(dishOrders);

        return order;
    }
}
