package classes;

import java.time.LocalDate;

public class StockMovement {
    private int id;
    private Ingredient ingredient;
    private Mouvement_type mouvementType;
    private Unit unit;
    private LocalDate creaction_datetime;

    public StockMovement() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Mouvement_type getMouvementType() {
        return mouvementType;
    }

    public void setMouvementType(Mouvement_type mouvementType) {
        this.mouvementType = mouvementType;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public LocalDate getCreaction_datetime() {
        return creaction_datetime;
    }

    public void setCreaction_datetime(LocalDate creaction_datetime) {
        this.creaction_datetime = creaction_datetime;
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "id=" + id +
                ", ingredient=" + ingredient +
                ", mouvementType=" + mouvementType +
                ", unit=" + unit +
                ", creaction_datetime=" + creaction_datetime +
                '}';
    }
}
