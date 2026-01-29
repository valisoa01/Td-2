import Connection.DBRetriever;
import classes.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        DBRetriever retriever = new DBRetriever();

        System.out.println("=== TEST GESTION DES TABLES ===");


        Table table1 = new Table(1, 1);
        Table table2 = new Table(2, 2);
        Table table3 = new Table(3, 3);


        Dish salade = new Dish(
                1,
                "Salade composée",
                DishTypeEnum.MAIN,
                2000.0
        );

        DishOrder dishOrder = new DishOrder(
                salade,
                1
        );

        List<DishOrder> dishOrders = new ArrayList<>();
        dishOrders.add(dishOrder);


        Instant arrival = Instant.parse("2026-01-29T12:00:00Z");
        Instant departure = Instant.parse("2026-01-29T13:00:00Z");


        try {
            Order order1 = new Order();
            order1.setReference("CMD-001");
            order1.setCreationDate(Instant.now());
            order1.setTable(table1);
            order1.setArrival_Datetime(arrival);
            order1.setDepartureDatetime(departure);
            order1.setDishOrders(dishOrders);

            retriever.saveOrder(order1);
            System.out.println("✅ Commande 1 enregistrée (table 1)");

        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
        }


        try {
            Order order2 = new Order();
            order2.setReference("CMD-002");
            order2.setCreationDate(Instant.now());
            order2.setTable(table1); // déjà occupée
            order2.setArrival_Datetime(arrival);
            order2.setDepartureDatetime(departure);
            order2.setDishOrders(dishOrders);

            retriever.saveOrder(order2);
            System.out.println("✅ Commande 2 enregistrée");

        } catch (Exception e) {
            System.out.println("⚠️ " + e.getMessage());
        }



        try {
             Order order3 = new Order();
            order3.setReference("CMD-003");
            order3.setCreationDate(Instant.now());
            order3.setTable(table2);
            order3.setArrival_Datetime(arrival);
            order3.setDepartureDatetime(departure);
            order3.setDishOrders(dishOrders);
            retriever.saveOrder(order3);

             Order order4 = new Order();
            order4.setReference("CMD-004");
            order4.setCreationDate(Instant.now());
            order4.setTable(table3);
            order4.setArrival_Datetime(arrival);
            order4.setDepartureDatetime(departure);
            order4.setDishOrders(dishOrders);
            retriever.saveOrder(order4);

             Order order5 = new Order();
            order5.setReference("CMD-005");
            order5.setCreationDate(Instant.now());
            order5.setTable(table1);
            order5.setArrival_Datetime(arrival);
            order5.setDepartureDatetime(departure);
            order5.setDishOrders(dishOrders);

            retriever.saveOrder(order5);

        } catch (Exception e) {
            System.out.println("⛔ " + e.getMessage());
        }

        System.out.println("=== FIN DES TESTS ===");
    }
}
