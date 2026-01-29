package classes;

import java.time.Instant;
import java.util.List;

public class Order {
    private static final double VAT_RAT = 0.2;
    private int id;
    private String reference;
    private Instant creationDate;
    private List<DishOrder> dishOrders;
    private Instant arrival_Datetime;
    private Instant departureDatetime;
    private Table table;

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

    public Instant getArrival_Datetime() {
        return arrival_Datetime;
    }

    public void setArrival_Datetime(Instant arrival_Datetime) {
        this.arrival_Datetime = arrival_Datetime;
    }

    public Instant getDepartureDatetime() {
        return departureDatetime;
    }

    public void setDepartureDatetime(Instant departureDatetime) {
        this.departureDatetime = departureDatetime;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }
    public Double getTotalAmountWithoutVAT() {
        if (dishOrders == null || dishOrders.isEmpty()) {
            return 0.0;
        }

        return dishOrders.stream()
                .mapToDouble(dishOrder ->
                        dishOrder.getDish().getPrice() * dishOrder.getQuantity()
                )
                .sum();

    }
    public Double getTotalAmountWithVAT() {
        double totalWithoutVAT = getTotalAmountWithoutVAT();
        return totalWithoutVAT * (1 + VAT_RAT);
    }
}
