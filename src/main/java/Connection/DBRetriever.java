package Connection;

import classes.*;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBRetriever {

    public Ingredient findIngredientById(int ingredientId) {
        System.out.println("DEBUG: Recherche ingrédient id=" + ingredientId);

        String sql = """
            SELECT id, name, price, category
            FROM ingredient
            WHERE id = ?
        """;

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Ingredient not found (id=" + ingredientId + ")");
            }

            Ingredient ingredient = new Ingredient(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    CategoryEnum.valueOf(rs.getString("category")),
                    null
            );

            ingredient.setStockMovementList(
                    findStockMovementsByIngredientId(ingredientId)
            );

            return ingredient;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'ingrédient", e);
        }
    }

    public List<StockMouvement> findStockMovementsByIngredientId(int ingredientId) {
        String sql = """
            SELECT id_stock,
                   quantity,
                   type,
                   unit,
                   creation_datetime
            FROM stockmovement
            WHERE id_ingredient = ?
            ORDER BY creation_datetime
        """;

        List<StockMouvement> movements = new ArrayList<>();

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StockMouvement sm = new StockMouvement();
                sm.setId(rs.getInt("id_stock"));
                sm.setQuantity(rs.getDouble("quantity"));
                sm.setMouvementType(
                        MouvementType.valueOf(rs.getString("type"))
                );
                sm.setUnit(Unit.valueOf(rs.getString("unit")));
                sm.setCreaction_datetime(
                        rs.getDate("creation_datetime").toLocalDate()
                );

                movements.add(sm);
            }

            return movements;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des mouvements de stock", e);
        }
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        System.out.println("DEBUG: Sauvegarde ingrédient: " + ingredient.getName());

        Connection conn = DBConnection.getDBConnection();

        try {
            conn.setAutoCommit(false);

            // Insertion de l'ingrédient s'il n'existe pas
            if (ingredient.getId() == 0) {
                String insertIngredient = """
                    INSERT INTO ingredient(name, price, category)
                    VALUES (?, ?, ?::category_enum)
                    RETURNING id
                """;

                try (PreparedStatement ps = conn.prepareStatement(insertIngredient)) {
                    ps.setString(1, ingredient.getName());
                    ps.setDouble(2, ingredient.getPrice());
                    ps.setString(3, ingredient.getCategory().name());

                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    ingredient.setId(rs.getInt(1));
                    System.out.println("DEBUG: Ingrédient créé avec id=" + ingredient.getId());
                }
            }

            // Insertion des mouvements de stock
            for (StockMouvement sm : ingredient.getStockMovementList()) {
                String insertMovement = """
                    INSERT INTO stockmovement
                    (id_stock, id_ingredient, quantity, type, unit, creation_datetime)
                    VALUES (?, ?, ?, ?::mouvement_type, ?::unit_type, ?)
                    ON CONFLICT (id_stock) DO NOTHING
                """;

                try (PreparedStatement ps = conn.prepareStatement(insertMovement)) {
                    ps.setInt(1, sm.getId());
                    ps.setInt(2, ingredient.getId());
                    ps.setDouble(3, sm.getQuantity());
                    ps.setString(4, sm.getMouvementType().name());
                    ps.setString(5, sm.getUnit().name());
                    ps.setDate(6, Date.valueOf(sm.getCreaction_datetime()));
                    ps.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("DEBUG: Ingredient sauvegardé avec succès");
            return ingredient;

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            throw new RuntimeException("Erreur lors de la sauvegarde de l'ingrédient", e);

        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
        }
    }

    public double getStockValueAt(int ingredientId, LocalDate date) {
        Ingredient ingredient = findIngredientById(ingredientId);

        double stock = 0;

        for (StockMouvement sm : ingredient.getStockMovementList()) {
            if (!sm.getCreaction_datetime().isAfter(date)) {
                if (sm.getMouvementType() == MouvementType.IN) {
                    stock += sm.getQuantity();
                } else {
                    stock -= sm.getQuantity();
                }
            }
        }
        return stock;
    }

    public Table findTableById(Integer id) {
        System.out.println("DEBUG: Recherche table id=" + id);

        String sql = "SELECT id, number FROM restaurant_table WHERE id = ?";

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Table non trouvée (id=" + id + ")");
            }

            Table table = new Table();
            table.setId(rs.getInt("id"));
            table.setNumber(rs.getInt("number")); // CORRECTION ICI
            System.out.println("DEBUG: Table trouvée: id=" + table.getId() + ", numéro=" + table.getNumber());
            return table;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la table", e);
        }
    }

    public Order saveOrder(Order orderToSave) {
        System.out.println("\nDEBUG: Début saveOrder pour " + orderToSave.getReference());

        if (orderToSave.getTable() == null) {
            System.out.println("DEBUG: Table est null");
            throw new RuntimeException("Table non fournie");
        }

        Connection conn = DBConnection.getDBConnection();

        try {
            conn.setAutoCommit(false);

            Integer tableId = orderToSave.getTable().getId();
            Instant arrival = orderToSave.getArrival_Datetime();
            Instant departure = orderToSave.getDepartureDatetime();

            System.out.println("DEBUG: Table ID=" + tableId + ", Arrival=" + arrival + ", Departure=" + departure);

            // Vérifier si la table est disponible
            if (!isTableAvailable(tableId, arrival, departure, conn)) {
                System.out.println("DEBUG: Table " + tableId + " n'est pas disponible");

                List<Integer> freeTables = findAvailableTables(arrival, departure, conn);

                if (freeTables.isEmpty()) {
                    throw new RuntimeException("Aucune table n'est disponible pour ce créneau");
                } else {
                    throw new RuntimeException(
                            "La table " + orderToSave.getTable().getNumber()
                                    + " n'est pas disponible. Tables disponibles : "
                                    + freeTables
                    );
                }
            }

            System.out.println("DEBUG: Table " + tableId + " est disponible");

            // Insertion de la commande - CORRECTION DES NOMS DE COLONNES
            String insertOrder = """
                     INSERT INTO orders
                     (reference, creaction_datetime, table_id, arrriva_datetime, departure_datetime)
                    VALUES (?, ?, ?, ?, ?)
                    RETURNING id
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertOrder)) {
                ps.setString(1, orderToSave.getReference());
                ps.setTimestamp(2, Timestamp.from(orderToSave.getCreationDate()));
                ps.setInt(3, tableId);
                ps.setTimestamp(4, Timestamp.from(arrival));
                ps.setTimestamp(5, Timestamp.from(departure));

                System.out.println("DEBUG: Exécution INSERT orders: ref=" + orderToSave.getReference());

                ResultSet rs = ps.executeQuery();
                rs.next();
                orderToSave.setId(rs.getInt(1));
                System.out.println("DEBUG: Order créé avec id=" + orderToSave.getId());
            }

            // Sauvegarde des plats de la commande
            saveDishOrders(orderToSave, conn);

            conn.commit();
            System.out.println("DEBUG: Commande " + orderToSave.getReference() + " sauvegardée avec succès");
            return orderToSave;

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {}
            System.out.println("DEBUG: Erreur lors de la sauvegarde: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);

        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    private void saveDishOrders(Order order, Connection conn) throws SQLException {
        if (order.getDishOrders() == null || order.getDishOrders().isEmpty()) {
            System.out.println("DEBUG: Aucun dish order à sauvegarder");
            return;
        }

        String sql = """
        INSERT INTO dishorder (id_order, id_dish, quantity)
        VALUES (?, ?, ?)
        RETURNING id
    """;

        System.out.println("DEBUG: Sauvegarde de " + order.getDishOrders().size() + " dish orders");

        for (DishOrder dishOrder : order.getDishOrders()) {
            if (dishOrder.getDish() == null) {
                throw new RuntimeException("DishOrder contient un Dish null");
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, order.getId());
                ps.setInt(2, dishOrder.getDish().getId());
                ps.setInt(3, dishOrder.getQuantity());

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    dishOrder.setId(generatedId); // assigner l'id généré si nécessaire
                    System.out.println("DEBUG: DishOrder ajouté avec id=" + generatedId +
                            " pour dish id=" + dishOrder.getDish().getId());
                }
            }
        }
    }


    public boolean isTableAvailable(
            Integer tableId,
            Instant arrival,
            Instant departure,
            Connection conn
    ) throws SQLException {

        String sql = """
            SELECT 1
            FROM orders
            WHERE table_id = ?
              AND arrriva_datetime < ?
              AND departure_datetime > ?
            LIMIT 1
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setTimestamp(2, Timestamp.from(departure));
            ps.setTimestamp(3, Timestamp.from(arrival));

            ResultSet rs = ps.executeQuery();
            boolean isAvailable = !rs.next();
            System.out.println("DEBUG: isTableAvailable(" + tableId + ") = " + isAvailable);
            return isAvailable;
        }
    }


    private List<Integer> findAvailableTables(
            Instant arrival,
            Instant departure,
            Connection conn
    ) throws SQLException {

        String sql = """
            SELECT id
            FROM restaurant_table
            WHERE id NOT IN (
                SELECT table_id
                FROM orders
                WHERE orders.arrriva_datetime < ?
                  AND departure_datetime > ?
            )
            ORDER BY number
        """;

        List<Integer> tables = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.from(departure));
            ps.setTimestamp(2, Timestamp.from(arrival));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tables.add(rs.getInt("id"));
            }
        }

        System.out.println("DEBUG: Tables disponibles: " + tables);
        return tables;
    }

    // Classe de conversion d'unités (rendue statique)
    public static class UnitConversionService {

        private static final Map<String, ConversionRule> RULES = Map.of(
                "Tomate",   new ConversionRule(10.0, null),
                "Laitue",   new ConversionRule(12.0, null),
                "Poulet",   new ConversionRule(8.0, null),
                "Chocolat", new ConversionRule(null, 2.5),
                "Beurre",   new ConversionRule(4.0, 5.0)
        );

        public static double convert(
                String ingredientName,
                double quantity,
                Unit from,
                Unit to
        ) {
            if (from == to) {
                return quantity;
            }

            ConversionRule rule = RULES.get(ingredientName);
            if (rule == null) {
                throw new IllegalArgumentException("Aucune règle pour " + ingredientName);
            }

            // Conversion PCS <-> KG
            if (from == Unit.PCS && to == Unit.KG) {
                if (rule.getPcsPerKg() == null) {
                    throw new IllegalArgumentException("Conversion PCS->KG impossible pour " + ingredientName);
                }
                return quantity / rule.getPcsPerKg();
            }
            if (from == Unit.KG && to == Unit.PCS) {
                if (rule.getPcsPerKg() == null) {
                    throw new IllegalArgumentException("Conversion KG->PCS impossible pour " + ingredientName);
                }
                return quantity * rule.getPcsPerKg();
            }

            // Conversion L <-> KG
            if (from == Unit.L && to == Unit.KG) {
                if (rule.getLPerKg() == null) {
                    throw new IllegalArgumentException("Conversion L->KG impossible pour " + ingredientName);
                }
                return quantity / rule.getLPerKg();
            }
            if (from == Unit.KG && to == Unit.L) {
                if (rule.getLPerKg() == null) {
                    throw new IllegalArgumentException("Conversion KG->L impossible pour " + ingredientName);
                }
                return quantity * rule.getLPerKg();
            }

            throw new IllegalArgumentException(
                    "Conversion impossible pour " + ingredientName +
                            " de " + from + " vers " + to
            );
        }
    }

    // Classe interne pour les règles de conversion
    public static class ConversionRule {
        private final Double pcsPerKg;
        private final Double lPerKg;

        public ConversionRule(Double pcsPerKg, Double lPerKg) {
            this.pcsPerKg = pcsPerKg;
            this.lPerKg = lPerKg;
        }

        public Double getPcsPerKg() {
            return pcsPerKg;
        }

        public Double getLPerKg() {
            return lPerKg;
        }
    }
}