package classes;

public class RestaurantTable {
    private final Integer id;
    private final Integer tableNumber;

    public RestaurantTable(Integer id, Integer tableNumber) {
        this.id = id;
        this.tableNumber = tableNumber;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }
}
