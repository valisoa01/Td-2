package Connection;

import classes.*;
import org.aspectj.runtime.internal.Conversions;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DBRetriever {

    /* =============================== */
    /* ===== INGREDIENT ============== */
    /* =============================== */

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

            // Charger les mouvements de stock
            ingredient.setStockMovementList(
                    findStockMovementsByIngredientId(ingredientId)
            );

            return ingredient;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /* ===================================== */
    /* ===== STOCK MOVEMENTS (READ) ======== */
    /* ===================================== */
//    private List<Ingredient> findIngredientDishId(int dishId) {
//
//    }
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

    /* ===================================== */
    /* ===== INGREDIENT + STOCK (SAVE) ===== */
    /* ===================================== */

    public Ingredient saveIngredient(Ingredient ingredient) {

        Connection conn = DBConnection.getDBConnection();

        try {
            conn.setAutoCommit(false);

            /* 1️⃣ Sauvegarde de l’ingredient si nouveau */
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

            /* 2️⃣ Sauvegarde des mouvements de stock */
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

    /* ===================================== */
    /* ===== STOCK CALCULATION ============= */
    /* ===================================== */

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
        Connection conn = DBConnection.getDBConnection();

        try {
            conn.setAutoCommit(false);

            if (orderToSave.getId() == 0) {

                String sql = """
                INSERT INTO orders (id, reference, creaction_datetime)
                VALUES (?, ?, ?)
            """;

                try (PreparedStatement ps = conn.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )) {
                    ps.setString(1, orderToSave.getReference());
                    ps.setTimestamp(2,Timestamp.from(orderToSave.getCreationDate()));
                    ps.executeUpdate();

                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        orderToSave.setId(rs.getInt(1));
                    }
                }
                saveDishOrders(orderToSave, conn);
            }
            conn.commit();
            return orderToSave;

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException(e);

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

            // PCS <-> KG
            if (from == Unit.PCS && to == Unit.KG) {
                return quantity / rule.getPcsPerKg();
            }
            if (from == Unit.KG && to == Unit.PCS) {
                return quantity * rule.getPcsPerKg();
            }

            // L <-> KG
            if (from == Unit.L && to == Unit.KG) {
                return quantity / rule.getLPerKg();
            }
            if (from == Unit.KG && to == Unit.L) {
                return quantity * rule.getLPerKg();
            }

            // Interdit : PCS <-> L
            throw new IllegalArgumentException(
                    "Conversion impossible pour " + ingredientName +
                            " de " + from + " vers " + to
            );
        }
    }
}


