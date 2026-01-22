package classes;

import java.time.Instant;
import java.util.List;

public class Order {
    private int id;
    private String reference;
    private Instant creationDate;
    private List<DishOrder> dishOrders;

    public Order() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }
    public Double getTotalAmountWithoutVAT() {

    }
    public Double getTotalAmountWithVAT() {

    }
}
