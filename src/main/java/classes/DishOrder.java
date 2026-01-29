package classes;

public class DishOrder {
    private Dish dish;
    private int quantity;

    public DishOrder(Dish dish, int quantity) {
        this.dish = dish; // important !
        this.quantity = quantity;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setId(int generatedId) {
    }
}
