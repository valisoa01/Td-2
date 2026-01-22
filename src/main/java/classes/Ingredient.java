package classes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {

    private int id;
    private String name;
    private double price;
    private CategoryEnum category;

    // 🔹 AJOUT OBLIGATOIRE TD 4
    private List<StockMovement> stockMovementList = new ArrayList<>();

    public Ingredient(int id, String name, double price, CategoryEnum category, Dish dish) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
    }

    /* ===================== */
    /* ===== GETTERS ======= */
    /* ===================== */

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public CategoryEnum getCategory() { return category; }

    public void setId(int id) {
        this.id = id;
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    /* ===================== */
    /* ===== SETTERS ======= */
    /* ===================== */

    // 🔹 UTILISÉ PAR DBRetriever
    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    /* ===================== */
    /* ===== STOCK LOGIC ==== */
    /* ===================== */

    public double getStockValueAt(LocalDate date) {
        double stock = 0;

        for (StockMovement sm : stockMovementList) {
            if (!sm.getCreaction_datetime().isAfter(date)) {
                if (sm.getMouvementType() == Mouvement_type.IN) {
                    stock += sm.getQuantity();
                } else {
                    stock -= sm.getQuantity();
                }
            }
        }
        return stock;
    }

    /* ===================== */
    /* ===== OVERRIDES ===== */
    /* ===================== */

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient that)) return false;
        return id == that.id &&
                Double.compare(that.price, price) == 0 &&
                Objects.equals(name, that.name) &&
                category == that.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category);
    }
}
