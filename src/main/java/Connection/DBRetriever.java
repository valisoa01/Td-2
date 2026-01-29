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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }
    }


    public Ingredient saveIngredient(Ingredient ingredient) {

        Connection conn = DBConnection.getDBConnection();

        try {
            conn.setAutoCommit(false);

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
                }
            }

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
            return ingredient;

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
            throw new RuntimeException(e);

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


    public Order saveOrder(Order orderToSave) {

        if (orderToSave.getTable() == null) {
            throw new RuntimeException("Table non fournie");
        }

        Connection conn = DBConnection.getDBConnection();

        try {
            conn.setAutoCommit(false);

            Integer tableId = orderToSave.getTable().getId();
            Instant arrival = orderToSave.getArrival_Datetime();
            Instant departure = orderToSave.getDepartureDatetime();

             if (!isTableAvaible(tableId, arrival, departure, conn)) {

                List<Integer> freeTables =
                        findAvailableTables(arrival, departure, conn);

                if (freeTables.isEmpty()) {
                    throw new RuntimeException(
                            "Aucune table n'est disponible pour ce créneau"
                    );
                } else {
                    throw new RuntimeException(
                            "La table " + orderToSave.getTable().getNumber()
                                    + " n'est pas disponible. Tables disponibles : "
                                    + freeTables
                    );
                }
            }

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

                ResultSet rs = ps.executeQuery();
                rs.next();
                orderToSave.setId(rs.getInt(1));
            }

             saveDishOrders(orderToSave, conn);

            conn.commit();
            return orderToSave;

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException(e.getMessage(), e);

        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    private void saveDishOrders(Order order, Connection conn) throws SQLException {
        String sql = """
        INSERT INTO Dishorder (id_order, id_dish, quantity)
        VALUES (?, ?, ?)
    """;

        for (DishOrder dishOrder : order.getDishOrders()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, order.getId());
                ps.setInt(2, dishOrder.getDish().getId());
                ps.setInt(3, dishOrder.getQuantity());
                ps.executeUpdate();
            }
        }
    }

    public boolean isTableAvaible(
            Integer tableId,
            Instant arrival,
            Instant departure,
            Connection conn
    ) throws SQLException {

        String sql = """
        SELECT 1
        FROM orders
        WHERE table_id = ?
          AND orders.arrriva_datetime < ?
          AND departure_datetime > ?
        LIMIT 1
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setTimestamp(2, Timestamp.from(departure));
            ps.setTimestamp(3, Timestamp.from(arrival));

            ResultSet rs = ps.executeQuery();
            return !rs.next();
        }
    }

    private List<Integer> findAvailableTables(
            Instant arrival,
            Instant departure,
            Connection conn
    ) throws SQLException {

        String sql = """
        SELECT number
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
                tables.add(rs.getInt("number"));
            }
        }

        return tables;
    }


    public class UnitConversionService {

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

             if (from == Unit.PCS && to == Unit.KG) {
                return quantity / rule.getPcsPerKg();
            }
            if (from == Unit.KG && to == Unit.PCS) {
                return quantity * rule.getPcsPerKg();
            }

             if (from == Unit.L && to == Unit.KG) {
                return quantity / rule.getLPerKg();
            }
            if (from == Unit.KG && to == Unit.L) {
                return quantity * rule.getLPerKg();
            }

             throw new IllegalArgumentException(
                    "Conversion impossible pour " + ingredientName +
                            " de " + from + " vers " + to
            );
        }
    }
}


