package classes;

import java.util.List;

public class Dish {
    int id;
    String name;
    DishTypeEnum dishTypeEnum;
    List<Ingredient> ingredients;

    public Dish(int id, String name, DishTypeEnum dishTypeEnum, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishTypeEnum = dishTypeEnum;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishTypeEnum() {
        return dishTypeEnum;
    }

    public void setDishTypeEnum(DishTypeEnum dishTypeEnum) {
        this.dishTypeEnum = dishTypeEnum;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishTypeEnum=" + dishTypeEnum +
                ", ingredients=" + ingredients +
                '}';
    }

    public Double getDishPrice() {
        double total = 0.0;

        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                if (ingredient.getPrice() != null) {
                    total += ingredient.getPrice();
                }
            }
        }

        return total;
    }

}
